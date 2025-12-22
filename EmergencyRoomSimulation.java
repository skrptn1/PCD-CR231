import javax.swing.*;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EmergencyRoomSimulation {

    // ===== UI (Swing) =====
    private static JTextArea area;

    private static void log(String msg) {
        String line = "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + msg + "\n";
        if (area == null) {
            System.out.print(line);
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            area.append(line);
            area.setCaretPosition(area.getDocument().getLength());
        } else {
            SwingUtilities.invokeLater(() -> {
                area.append(line);
                area.setCaretPosition(area.getDocument().getLength());
            });
        }
    }

    // ===== Models =====
    enum Severity { GREEN, YELLOW, RED }
    enum TestType { NONE, XRAY, CT, MRI }

    static class Patient {
        final int id;
        final Severity severity;
        final TestType test;
        final long createdAtMs;
        final CountDownLatch treatedLatch = new CountDownLatch(1); // pacientul așteaptă tratamentul

        Patient(int id, Severity severity, TestType test) {
            this.id = id;
            this.severity = severity;
            this.test = test;
            this.createdAtMs = System.currentTimeMillis();
        }
    }

    // ===== Shared primitives =====
    // Bancă de sânge: synchronized + wait/notifyAll
    static class BloodBank {
        private int units;

        BloodBank(int initialUnits) { this.units = initialUnits; }

        public synchronized void takeUnits(int need, String who) throws InterruptedException {
            while (units < need) {
                log(who + " -> sânge insuficient (" + units + "u). Aștept replenishment...");
                wait();
            }
            units -= need;
            log(who + " -> ia " + need + " unități sânge. Rămân: " + units + "u");
        }

        public synchronized void addUnits(int add, String who) {
            units += add;
            log(who + " -> replenishment +" + add + "u sânge. Total: " + units + "u");
            notifyAll();
        }

        public synchronized int snapshot() { return units; }
    }

    // Paturi: ReentrantLock + Condition
    static class BedManager {
        private final int totalBeds;
        private int freeBeds;

        private final ReentrantLock lock = new ReentrantLock(true);
        private final Condition bedAvailable = lock.newCondition();

        BedManager(int totalBeds) {
            this.totalBeds = totalBeds;
            this.freeBeds = totalBeds;
        }

        public void acquireBed(String patientName) throws InterruptedException {
            lock.lock();
            try {
                while (freeBeds == 0) {
                    log(patientName + " -> nu sunt paturi libere, aștept...");
                    bedAvailable.await();
                }
                freeBeds--;
                log(patientName + " -> a primit pat. Libere: " + freeBeds + "/" + totalBeds);
            } finally {
                lock.unlock();
            }
        }

        public void releaseBed(String patientName) {
            lock.lock();
            try {
                freeBeds++;
                log(patientName + " -> eliberează pat. Libere: " + freeBeds + "/" + totalBeds);
                bedAvailable.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    // Flag thread-safe
    static class VolatileFlag {
        private volatile boolean v;
        VolatileFlag(boolean initial) { this.v = initial; }
        boolean get() { return v; }
        void set(boolean nv) { this.v = nv; }
    }

    // ===== Main =====
    public static void main(String[] args) throws Exception {
        // UI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Emergency Room Simulation (Java 8) - Threads");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1020, 680);
            area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 13));
            frame.add(new JScrollPane(area));
            frame.setVisible(true);
        });
        Thread.sleep(300);

        // Shared
        BlockingQueue<Patient> triageQueue = new LinkedBlockingQueue<Patient>(80); // BlockingQueue
        BloodBank bloodBank = new BloodBank(6);
        BedManager beds = new BedManager(6);

        Semaphore registrationDesks = new Semaphore(2, true); // 2 ghișee
        Semaphore doctorsOnDuty = new Semaphore(3, true);     // 3 doctori simultan (resursă)
        Semaphore mriMachines = new Semaphore(1, true);       // 1 MRI
        Semaphore ctMachines = new Semaphore(1, true);        // 1 CT
        Semaphore xrayMachines = new Semaphore(2, true);      // 2 XRay

        CountDownLatch openLatch = new CountDownLatch(1);     // start simultan
        Phaser shiftPhaser = new Phaser(1);                   // manager party
        VolatileFlag closing = new VolatileFlag(false);

        AtomicInteger patientIdGen = new AtomicInteger(0);
        AtomicInteger treatedTotal = new AtomicInteger(0);
        AtomicLong totalWaitMs = new AtomicLong(0);

        ConcurrentHashMap<Severity, AtomicInteger> severityStats = new ConcurrentHashMap<Severity, AtomicInteger>();
        for (Severity s : Severity.values()) severityStats.put(s, new AtomicInteger(0));

        ConcurrentHashMap<TestType, AtomicInteger> testStats = new ConcurrentHashMap<TestType, AtomicInteger>();
        for (TestType t : TestType.values()) testStats.put(t, new AtomicInteger(0));

        // ===== Workers (Threads + Runnable) =====
        List<Thread> workers = new ArrayList<Thread>();

        // Triage nurse (doar log + ritm)
        Thread triageNurse = new Thread(() -> {
            String name = Thread.currentThread().getName();
            shiftPhaser.register();
            try {
                openLatch.await();
                while (!closing.get()) {
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> stop intake (phase 1)");
                shiftPhaser.arriveAndAwaitAdvance();
                cleanupSleep(name);
                shiftPhaser.arriveAndAwaitAdvance();
                shiftPhaser.arriveAndDeregister();
                log(name + " -> terminat.");
            }
        }, "TriageNurse");
        workers.add(triageNurse);

        // Doctors (consumă pacienți din coadă)
        int DOCTORS = 3;
        for (int i = 1; i <= DOCTORS; i++) {
            Thread doc = new Thread(new Doctor(
                    "Doctor-" + i,
                    openLatch,
                    closing,
                    shiftPhaser,
                    triageQueue,
                    doctorsOnDuty,
                    bloodBank,
                    mriMachines,
                    ctMachines,
                    xrayMachines,
                    severityStats,
                    testStats,
                    treatedTotal,
                    totalWaitMs
            ), "Doctor-" + i);
            workers.add(doc);
        }

        // Blood replenisher (sânge)
        Thread bloodReplenisher = new Thread(() -> {
            String name = Thread.currentThread().getName();
            Random rnd = new Random();
            shiftPhaser.register();
            try {
                openLatch.await();
                while (!closing.get()) {
                    Thread.sleep(700 + rnd.nextInt(600));
                    int now = bloodBank.snapshot();
                    if (now < 4) bloodBank.addUnits(6, name);
                    else bloodBank.addUnits(2, name);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> stop service (phase 1)");
                shiftPhaser.arriveAndAwaitAdvance();
                cleanupSleep(name);
                shiftPhaser.arriveAndAwaitAdvance();
                shiftPhaser.arriveAndDeregister();
                log(name + " -> terminat.");
            }
        }, "BloodReplenisher");
        workers.add(bloodReplenisher);

        // Start workers
        for (Thread t : workers) t.start();

        // ===== ThreadPoolExecutor: pacienții sosesc ca task-uri =====
        ThreadPoolExecutor arrivalsPool = new ThreadPoolExecutor(
                4, 8,
                25, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(30),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // Thread separat (ca să folosim join)
        Thread arrivalSupervisor = new Thread(() -> {
            String name = Thread.currentThread().getName();
            Random rnd = new Random();
            try {
                openLatch.await();

                int PATIENTS = 28;
                for (int i = 1; i <= PATIENTS; i++) {
                    final int idx = i;
                    arrivalsPool.submit(() -> {
                        String pName = "Patient-" + idx;
                        try {
                            // 1) Pat (ReentrantLock+Condition)
                            beds.acquireBed(pName);

                            // 2) Înregistrare (Semaphore)
                            registrationDesks.acquire();
                            Patient patient;
                            try {
                                Thread.sleep(120 + rnd.nextInt(220));
                                Severity sev = randomSeverity(rnd);
                                TestType test = randomTest(rnd, sev);
                                int id = patientIdGen.incrementAndGet();
                                patient = new Patient(id, sev, test);
                                log(pName + " -> înregistrat ca #" + id + " | " + sev + " | test=" + test);
                            } finally {
                                registrationDesks.release();
                            }

                            // 3) Intră în coada de triere (BlockingQueue)
                            triageQueue.put(patient);
                            log(pName + " -> a intrat în triageQueue (size=" + triageQueue.size() + ")");

                            // 4) Așteaptă tratamentul (CountDownLatch per pacient)
                            boolean ok = patient.treatedLatch.await(8, TimeUnit.SECONDS);
                            if (!ok) log(pName + " -> așteptare prea lungă (timeout), părăsește ER!");

                            // 5) Eliberează pat
                            beds.releaseBed(pName);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    Thread.sleep(110 + rnd.nextInt(180));
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> nu mai aduce pacienți. shutdown arrivals pool...");
                arrivalsPool.shutdown();
            }
        }, "ArrivalSupervisor");

        arrivalSupervisor.start();

        // ===== Open ER =====
        log("MANAGER -> pregătiri... SE DESCHIDE URGENȚA!");
        openLatch.countDown();

        // join
        arrivalSupervisor.join();
        arrivalsPool.awaitTermination(12, TimeUnit.SECONDS);

        // așteptăm să se proceseze ce a mai rămas
        Thread.sleep(1300);

        // ===== Closing: interrupt + Phaser phases =====
        log("MANAGER -> ÎNCHIDERE TURĂ: stop + interrupt + phase shutdown.");
        closing.set(true);
        for (Thread t : workers) t.interrupt();

        log("MANAGER -> aștept phase 1 (stop-service)...");
        shiftPhaser.arriveAndAwaitAdvance();

        log("MANAGER -> aștept phase 2 (cleanup)...");
        shiftPhaser.arriveAndAwaitAdvance();

        // ===== Callable + Future report =====
        ExecutorService reportExec = Executors.newFixedThreadPool(2);

        Future<String> totalsFuture = reportExec.submit(new Callable<String>() {
            @Override public String call() {
                long avg = treatedTotal.get() == 0 ? 0 : (totalWaitMs.get() / treatedTotal.get());
                return "Total tratați: " + treatedTotal.get()
                        + "\nCoada rămasă: " + triageQueue.size()
                        + "\nSânge rămas: " + bloodBank.snapshot() + "u"
                        + "\nTimp mediu (ms) până la finalizare: " + avg;
            }
        });

        Future<String> statsFuture = reportExec.submit(new Callable<String>() {
            @Override public String call() {
                StringBuilder sb = new StringBuilder();
                sb.append("Statistici severitate:\n");
                for (Severity s : Severity.values()) {
                    sb.append(" - ").append(s).append(": ").append(severityStats.get(s).get()).append("\n");
                }
                sb.append("\nStatistici teste:\n");
                for (TestType t : TestType.values()) {
                    sb.append(" - ").append(t).append(": ").append(testStats.get(t).get()).append("\n");
                }
                return sb.toString();
            }
        });

        String report = "\n===== RAPORT FINAL (ER) =====\n"
                + totalsFuture.get() + "\n\n"
                + statsFuture.get()
                + "============================\n";

        reportExec.shutdown();
        log(report);

        // join workers
        for (Thread t : workers) t.join();
        log("MANAGER -> tură închisă. Program terminat.");
    }

    static class Doctor implements Runnable {
        private final String name;
        private final CountDownLatch openLatch;
        private final VolatileFlag closing;
        private final Phaser phaser;

        private final BlockingQueue<Patient> queue;
        private final Semaphore doctorsLimit;
        private final BloodBank blood;
        private final Semaphore mri, ct, xray;

        private final ConcurrentHashMap<Severity, AtomicInteger> sevStats;
        private final ConcurrentHashMap<TestType, AtomicInteger> testStats;

        private final AtomicInteger treatedTotal;
        private final AtomicLong totalWaitMs;

        Doctor(String name,
               CountDownLatch openLatch,
               VolatileFlag closing,
               Phaser phaser,
               BlockingQueue<Patient> queue,
               Semaphore doctorsLimit,
               BloodBank blood,
               Semaphore mri,
               Semaphore ct,
               Semaphore xray,
               ConcurrentHashMap<Severity, AtomicInteger> sevStats,
               ConcurrentHashMap<TestType, AtomicInteger> testStats,
               AtomicInteger treatedTotal,
               AtomicLong totalWaitMs) {
            this.name = name;
            this.openLatch = openLatch;
            this.closing = closing;
            this.phaser = phaser;
            this.queue = queue;
            this.doctorsLimit = doctorsLimit;
            this.blood = blood;
            this.mri = mri;
            this.ct = ct;
            this.xray = xray;
            this.sevStats = sevStats;
            this.testStats = testStats;
            this.treatedTotal = treatedTotal;
            this.totalWaitMs = totalWaitMs;

            phaser.register();
        }

        @Override
        public void run() {
            Random rnd = new Random();
            try {
                openLatch.await();

                while (!closing.get() || !queue.isEmpty()) {
                    Patient p = queue.poll(300, TimeUnit.MILLISECONDS);
                    if (p == null) continue;

                    doctorsLimit.acquire(); // limitează “doctori simultan”
                    try {
                        // Caz sever: poate cere sânge (synchronized+wait/notifyAll)
                        if (p.severity == Severity.RED) {
                            blood.takeUnits(2, name);
                        } else if (p.severity == Severity.YELLOW && rnd.nextBoolean()) {
                            blood.takeUnits(1, name);
                        }

                        // Teste (Semaphore resurse)
                        doTestIfNeeded(p, rnd);

                        // Tratament
                        Thread.sleep(250 + rnd.nextInt(500));

                        sevStats.get(p.severity).incrementAndGet();
                        testStats.get(p.test).incrementAndGet();

                        int done = treatedTotal.incrementAndGet();
                        long latency = System.currentTimeMillis() - p.createdAtMs;
                        totalWaitMs.addAndGet(latency);

                        log(name + " -> tratează #" + p.id + " | " + p.severity + " | test=" + p.test
                                + " | total=" + done + " | t=" + latency + "ms");

                        p.treatedLatch.countDown(); // pacientul poate pleca

                    } finally {
                        doctorsLimit.release();
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> stop service (phase 1)");
                phaser.arriveAndAwaitAdvance();
                cleanupSleep(name);
                phaser.arriveAndAwaitAdvance();
                phaser.arriveAndDeregister();
                log(name + " -> terminat.");
            }
        }

        private void doTestIfNeeded(Patient p, Random rnd) throws InterruptedException {
            if (p.test == TestType.NONE) return;

            if (p.test == TestType.MRI) {
                mri.acquire();
                try {
                    Thread.sleep(220 + rnd.nextInt(260));
                    log(name + " -> MRI pentru #" + p.id);
                } finally {
                    mri.release();
                }
            } else if (p.test == TestType.CT) {
                ct.acquire();
                try {
                    Thread.sleep(180 + rnd.nextInt(240));
                    log(name + " -> CT pentru #" + p.id);
                } finally {
                    ct.release();
                }
            } else if (p.test == TestType.XRAY) {
                xray.acquire();
                try {
                    Thread.sleep(140 + rnd.nextInt(200));
                    log(name + " -> XRAY pentru #" + p.id);
                } finally {
                    xray.release();
                }
            }
        }
    }

    static void cleanupSleep(String who) {
        try {
            log(who + " -> cleanup...");
            Thread.sleep(450);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static Severity randomSeverity(Random rnd) {
        int x = rnd.nextInt(100);
        if (x < 60) return Severity.GREEN;
        if (x < 90) return Severity.YELLOW;
        return Severity.RED;
    }

    static TestType randomTest(Random rnd, Severity sev) {
        // RED are șanse mai mari de CT/MRI
        int x = rnd.nextInt(100);
        if (sev == Severity.RED) {
            if (x < 40) return TestType.CT;
            if (x < 65) return TestType.MRI;
            if (x < 85) return TestType.XRAY;
            return TestType.NONE;
        } else {
            if (x < 15) return TestType.XRAY;
            if (x < 22) return TestType.CT;
            if (x < 26) return TestType.MRI;
            return TestType.NONE;
        }
    }
}
