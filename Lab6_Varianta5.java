import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Lab6_Varianta5 {

    // ====== VARIANTA 5 ======
    static final int X = 2;      // producatori
    static final int Y = 5;      // consumatori
    static final int Z = 60;     // total produse/consumate
    static final int D = 12;     // capacitatea depozitului
    static final int F = 1;      // fiecare producator produce 1 obiect per încercare

    enum Mode {
        MONITOR_WAIT_NOTIFY("synchronized + wait/notify/notifyAll"),
        LOCK_CONDITION("ReentrantLock + Condition (await/signal/signalAll)"),
        BLOCKING_QUEUE("BlockingQueue (put/take) + Phaser (FILL/DRAIN)");

        final String label;
        Mode(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    // ====== UI =====
    private JFrame frame;
    private JTextArea area;
    private JLabel lblMode, lblPhase, lblStatus, lblStock, lblProd, lblCons;
    private JComboBox<Mode> cmbMode;
    private JButton btnStart, btnStop, btnClear;

    // ====== Runtime =====
    private volatile boolean running = false;
    private Simulation sim;

    interface Simulation {
        void start();
        void stop();
    }

    // ====== Utils =====
    private static void sleepQuiet(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void log(String s) {
        String t = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            area.append("[" + t + "] " + s + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        });
    }

    private void refreshStats(String mode, String phase, String status, int stock, int prod, int cons) {
        SwingUtilities.invokeLater(() -> {
            lblMode.setText("Metodă: " + mode);
            lblPhase.setText("Fază: " + phase);
            lblStatus.setText("Status: " + status);
            lblStock.setText("Stoc: " + stock + "/" + D);
            lblProd.setText("Total produse: " + prod + "/" + Z);
            lblCons.setText("Total consumate: " + cons + "/" + Z);
        });
    }

    // 1) synchronized + wait()/notify()/notifyAll()
    class MonitorSimulation implements Simulation {

        class Depot {
            final Deque<Integer> buffer = new ArrayDeque<>(D);
            final Random rnd = new Random();

            boolean fillPhase = true;      // true=FILL, false=DRAIN
            boolean stop = false;
            boolean done = false;

            int producedTotal = 0;
            int consumedTotal = 0;

            int randomEven() { return 2 * (1 + rnd.nextInt(50)); } // 2..100

            synchronized boolean produceOne(String name) throws InterruptedException {
                while (!stop && !done && (!fillPhase || buffer.size() == D || producedTotal >= Z)) {
                    wait(); // wait()
                }
                if (stop || done) return false;
                if (producedTotal >= Z) return false;

                int val = randomEven();
                buffer.addLast(val);
                producedTotal++;

                log(String.format("%s a produs: %d | stoc=%d/%d | totalProd=%d",
                        name, val, buffer.size(), D, producedTotal));
                refreshStats(Mode.MONITOR_WAIT_NOTIFY.label, "FILL", "RUNNING",
                        buffer.size(), producedTotal, consumedTotal);

                if (buffer.size() == D) {
                    log(String.format(">>> Depozitul e plin (%d/%d). Consumatorii pornesc.", D, D));
                    fillPhase = false;

                    // notify() + notifyAll() (ambele sunt folosite)
                    notify();      // notify()
                    notifyAll();   // notifyAll()
                } else {
                    // trezim cel puțin un thread (demonstrăm notify fără a forța trezirea tuturor)
                    notify(); // notify()
                }
                return true;
            }

            synchronized boolean consumeOne(String name) throws InterruptedException {
                while (!stop && !done && (fillPhase || buffer.isEmpty())) {
                    wait(); // wait()
                }
                if (stop || done) return false;

                int val = buffer.removeFirst();
                consumedTotal++;

                log(String.format("%s a consumat: %d | stoc=%d/%d | totalCons=%d",
                        name, val, buffer.size(), D, consumedTotal));
                refreshStats(Mode.MONITOR_WAIT_NOTIFY.label, "DRAIN", "RUNNING",
                        buffer.size(), producedTotal, consumedTotal);

                if (buffer.isEmpty()) {
                    log(String.format("<<< Depozitul e gol (0/%d). Producătorii pornesc.", D));

                    if (consumedTotal >= Z) {
                        done = true;
                        log("=== GATA: au fost produse și consumate " + Z + " obiecte. ===");
                        refreshStats(Mode.MONITOR_WAIT_NOTIFY.label, "DONE", "DONE",
                                0, producedTotal, consumedTotal);

                        notify();      // notify()
                        notifyAll();   // notifyAll()
                        return false;
                    }

                    fillPhase = true;
                    notify();      // notify()
                    notifyAll();   // notifyAll()
                } else {
                    // continuă DRAIN, trezim un consumator
                    notify(); // notify()
                }
                return true;
            }

            synchronized void requestStop() {
                stop = true;
                notifyAll(); // notifyAll()
            }

            synchronized int stock() { return buffer.size(); }
        }

        private final Depot depot = new Depot();
        private Thread[] producers;
        private Thread[] consumers;

        @Override public void start() {
            producers = new Thread[X];
            consumers = new Thread[Y];

            log("=== START (Monitor): X=2, Y=5, Z=60, D=12, F=1, obiecte=numere pare ===");
            refreshStats(Mode.MONITOR_WAIT_NOTIFY.label, "FILL", "RUNNING", 0, 0, 0);

            for (int i = 0; i < X; i++) {
                final int id = i + 1;
                producers[i] = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            boolean ok = depot.produceOne("Producator-" + id); // F=1
                            if (!ok) break;
                            sleepQuiet(30);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }, "Producer-" + id);
            }

            for (int i = 0; i < Y; i++) {
                final int id = i + 1;
                consumers[i] = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            boolean ok = depot.consumeOne("Consumator-" + id);
                            if (!ok) break;
                            sleepQuiet(60);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }, "Consumer-" + id);
            }

            for (Thread t : producers) t.start();
            for (Thread t : consumers) t.start();

            new Thread(() -> {
                try {
                    for (Thread t : producers) t.join();
                    for (Thread t : consumers) t.join();
                } catch (InterruptedException ignored) {
                } finally {
                    running = false;
                    String st = depot.done ? "DONE" : "STOP";
                    String ph = depot.done ? "DONE" : "-";
                    refreshStats(Mode.MONITOR_WAIT_NOTIFY.label, ph, st,
                            depot.stock(), depot.producedTotal, depot.consumedTotal);
                    SwingUtilities.invokeLater(() -> {
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        cmbMode.setEnabled(true);
                    });
                }
            }, "Joiner").start();
        }

        @Override public void stop() {
            depot.requestStop();
            if (producers != null) for (Thread t : producers) t.interrupt();
            if (consumers != null) for (Thread t : consumers) t.interrupt();
        }
    }

    // 2) ReentrantLock + Condition (await/signal/signalAll)
    class LockConditionSimulation implements Simulation {

        class Depot {
            final Deque<Integer> buffer = new ArrayDeque<>(D);
            final Random rnd = new Random();

            final ReentrantLock lock = new ReentrantLock(true);
            final Condition canProduce = lock.newCondition();
            final Condition canConsume = lock.newCondition();

            boolean fillPhase = true;
            boolean stop = false;
            boolean done = false;

            int producedTotal = 0;
            int consumedTotal = 0;

            int randomEven() { return 2 * (1 + rnd.nextInt(50)); }

            boolean produceOne(String name) throws InterruptedException {
                lock.lock();
                try {
                    while (!stop && !done && (!fillPhase || buffer.size() == D || producedTotal >= Z)) {
                        canProduce.await(); // await()
                    }
                    if (stop || done) return false;
                    if (producedTotal >= Z) return false;

                    int val = randomEven();
                    buffer.addLast(val);
                    producedTotal++;

                    log(String.format("%s a produs: %d | stoc=%d/%d | totalProd=%d",
                            name, val, buffer.size(), D, producedTotal));
                    refreshStats(Mode.LOCK_CONDITION.label, "FILL", "RUNNING",
                            buffer.size(), producedTotal, consumedTotal);

                    if (buffer.size() == D) {
                        log(String.format(">>> Depozitul e plin (%d/%d). Consumatorii pornesc.", D, D));
                        fillPhase = false;

                        canConsume.signal();     // signal()
                        canConsume.signalAll();  // signalAll()
                    } else {
                        canProduce.signal(); // signal()
                    }
                    return true;
                } finally {
                    lock.unlock(); // unlock() în finally (bună practică)
                }
            }

            boolean consumeOne(String name) throws InterruptedException {
                lock.lock();
                try {
                    while (!stop && !done && (fillPhase || buffer.isEmpty())) {
                        canConsume.await(); // await()
                    }
                    if (stop || done) return false;

                    int val = buffer.removeFirst();
                    consumedTotal++;

                    log(String.format("%s a consumat: %d | stoc=%d/%d | totalCons=%d",
                            name, val, buffer.size(), D, consumedTotal));
                    refreshStats(Mode.LOCK_CONDITION.label, "DRAIN", "RUNNING",
                            buffer.size(), producedTotal, consumedTotal);

                    if (buffer.isEmpty()) {
                        log(String.format("<<< Depozitul e gol (0/%d). Producătorii pornesc.", D));

                        if (consumedTotal >= Z) {
                            done = true;
                            log("=== GATA: au fost produse și consumate " + Z + " obiecte. ===");
                            refreshStats(Mode.LOCK_CONDITION.label, "DONE", "DONE",
                                    0, producedTotal, consumedTotal);

                            canProduce.signalAll();
                            canConsume.signalAll();
                            return false;
                        }

                        fillPhase = true;
                        canProduce.signal();     // signal()
                        canProduce.signalAll();  // signalAll()
                    } else {
                        canConsume.signal(); // signal()
                    }
                    return true;
                } finally {
                    lock.unlock(); // unlock() în finally
                }
            }

            void requestStop() {
                lock.lock();
                try {
                    stop = true;
                    canProduce.signalAll();
                    canConsume.signalAll();
                } finally {
                    lock.unlock();
                }
            }

            int stock() {
                lock.lock();
                try { return buffer.size(); }
                finally { lock.unlock(); }
            }
        }

        private final Depot depot = new Depot();
        private Thread[] producers;
        private Thread[] consumers;

        @Override public void start() {
            producers = new Thread[X];
            consumers = new Thread[Y];

            log("=== START (Lock+Condition): X=2, Y=5, Z=60, D=12, F=1, obiecte=numere pare ===");
            refreshStats(Mode.LOCK_CONDITION.label, "FILL", "RUNNING", 0, 0, 0);

            for (int i = 0; i < X; i++) {
                final int id = i + 1;
                producers[i] = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            boolean ok = depot.produceOne("Producator-" + id);
                            if (!ok) break;
                            sleepQuiet(30);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }, "Producer-" + id);
            }

            for (int i = 0; i < Y; i++) {
                final int id = i + 1;
                consumers[i] = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            boolean ok = depot.consumeOne("Consumator-" + id);
                            if (!ok) break;
                            sleepQuiet(60);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }, "Consumer-" + id);
            }

            for (Thread t : producers) t.start();
            for (Thread t : consumers) t.start();

            new Thread(() -> {
                try {
                    for (Thread t : producers) t.join();
                    for (Thread t : consumers) t.join();
                } catch (InterruptedException ignored) {
                } finally {
                    running = false;
                    String st = depot.done ? "DONE" : "STOP";
                    String ph = depot.done ? "DONE" : "-";
                    refreshStats(Mode.LOCK_CONDITION.label, ph, st,
                            depot.stock(), depot.producedTotal, depot.consumedTotal);
                    SwingUtilities.invokeLater(() -> {
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        cmbMode.setEnabled(true);
                    });
                }
            }, "Joiner").start();
        }

        @Override public void stop() {
            depot.requestStop();
            if (producers != null) for (Thread t : producers) t.interrupt();
            if (consumers != null) for (Thread t : consumers) t.interrupt();
        }
    }

    // 3) BlockingQueue (put/take) + Phaser (FILL/DRAIN)
\    class BlockingQueuePhaserSimulation implements Simulation {

        final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(D, true);
        final Random rnd = new Random();

        final AtomicInteger producedTotal = new AtomicInteger(0);
        final AtomicInteger consumedTotal = new AtomicInteger(0);

        final AtomicInteger fillSlots = new AtomicInteger(0);
        final AtomicInteger drainSlots = new AtomicInteger(0);

        volatile boolean stopRequested = false;
        volatile boolean done = false;

        class StopPhaser extends Phaser {
            StopPhaser(int parties) { super(parties); }
            @Override protected boolean onAdvance(int phase, int registeredParties) {
                if (stopRequested || done || registeredParties == 0) return true;

                // la trecerea de fază resetăm contoarele de sloturi
                if (phase % 2 == 0) { // FILL -> DRAIN
                    drainSlots.set(0);
                } else {              // DRAIN -> FILL
                    fillSlots.set(0);
                }
                return false;
            }
        }

        final StopPhaser phaser = new StopPhaser(X + Y);

        private Thread[] producers;
        private Thread[] consumers;

        int randomEven() { return 2 * (1 + rnd.nextInt(50)); }

        @Override public void start() {
            producers = new Thread[X];
            consumers = new Thread[Y];

            log("=== START (BlockingQueue+Phaser): X=2, Y=5, Z=60, D=12, F=1, obiecte=numere pare ===");
            refreshStats(Mode.BLOCKING_QUEUE.label, "FILL", "RUNNING", 0, 0, 0);

            // PRODUCATORI (rulează doar în faza FILL)
            for (int i = 0; i < X; i++) {
                final int id = i + 1;
                producers[i] = new Thread(() -> {
                    try {
                        while (!phaser.isTerminated() && !Thread.currentThread().isInterrupted()) {
                            int phase = phaser.getPhase();
                            boolean fillPhase = (phase % 2 == 0);

                            if (fillPhase) {
                                while (!phaser.isTerminated()) {
                                    int slot = fillSlots.getAndIncrement();
                                    if (slot >= D) break; // umplem exact D pe ciclu

                                    // dacă deja am produs tot, nu mai punem
                                    int pNow = producedTotal.get();
                                    if (pNow >= Z) break;

                                    int val = randomEven();

                                    // put() (blocant) - obiectiv BlockingQueue
                                    queue.put(val);

                                    int p = producedTotal.incrementAndGet();
                                    log(String.format("Producator-%d a produs: %d | stoc=%d/%d | totalProd=%d",
                                            id, val, queue.size(), D, p));
                                    refreshStats(Mode.BLOCKING_QUEUE.label, "FILL", "RUNNING",
                                            queue.size(), producedTotal.get(), consumedTotal.get());

                                    if (queue.size() == D) {
                                        log(String.format(">>> Depozitul e plin (%d/%d). Consumatorii pornesc.", D, D));
                                    }

                                    sleepQuiet(30);
                                }
                            }

                            // barieră: producătorii NU produc în DRAIN
                            phaser.arriveAndAwaitAdvance();
                        }
                    } catch (InterruptedException ignored) {
                    } finally {
                        try { phaser.arriveAndDeregister(); } catch (Exception ignored) {}
                    }
                }, "Producer-" + id);
            }

            // CONSUMATORI (rulează doar în faza DRAIN)
            for (int i = 0; i < Y; i++) {
                final int id = i + 1;
                consumers[i] = new Thread(() -> {
                    try {
                        while (!phaser.isTerminated() && !Thread.currentThread().isInterrupted()) {
                            int phase = phaser.getPhase();
                            boolean drainPhase = (phase % 2 == 1);

                            if (drainPhase) {
                                while (!phaser.isTerminated()) {
                                    int slot = drainSlots.getAndIncrement();
                                    if (slot >= D) break; // golim exact D pe ciclu

                                    // take() (blocant) - obiectiv BlockingQueue
                                    int val = queue.take();

                                    int c = consumedTotal.incrementAndGet();
                                    log(String.format("Consumator-%d a consumat: %d | stoc=%d/%d | totalCons=%d",
                                            id, val, queue.size(), D, c));
                                    refreshStats(Mode.BLOCKING_QUEUE.label, "DRAIN", "RUNNING",
                                            queue.size(), producedTotal.get(), consumedTotal.get());

                                    if (queue.isEmpty()) {
                                        log(String.format("<<< Depozitul e gol (0/%d). Producătorii pornesc.", D));
                                    }

                                    if (c >= Z && queue.isEmpty()) {
                                        done = true;
                                        log("=== GATA: au fost produse și consumate " + Z + " obiecte. ===");
                                        refreshStats(Mode.BLOCKING_QUEUE.label, "DONE", "DONE",
                                                0, producedTotal.get(), consumedTotal.get());
                                        phaser.forceTermination();
                                        break;
                                    }

                                    sleepQuiet(60);
                                }
                            }

                            // barieră: consumatorii NU consumă în FILL
                            phaser.arriveAndAwaitAdvance();
                        }
                    } catch (InterruptedException ignored) {
                    } finally {
                        try { phaser.arriveAndDeregister(); } catch (Exception ignored) {}
                    }
                }, "Consumer-" + id);
            }

            for (Thread t : producers) t.start();
            for (Thread t : consumers) t.start();

            new Thread(() -> {
                try {
                    for (Thread t : producers) t.join();
                    for (Thread t : consumers) t.join();
                } catch (InterruptedException ignored) {
                } finally {
                    running = false;
                    String st = done ? "DONE" : "STOP";
                    String ph = done ? "DONE" : "-";
                    refreshStats(Mode.BLOCKING_QUEUE.label, ph, st,
                            queue.size(), producedTotal.get(), consumedTotal.get());
                    SwingUtilities.invokeLater(() -> {
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        cmbMode.setEnabled(true);
                    });
                }
            }, "Joiner").start();
        }

        @Override public void stop() {
            stopRequested = true;
            phaser.forceTermination();
            if (producers != null) for (Thread t : producers) t.interrupt();
            if (consumers != null) for (Thread t : consumers) t.interrupt();
        }
    }

    // =========================================================
    // UI
    // =========================================================
    private void buildUI() {
        frame = new JFrame("Lab 6 – Producător–Consumator (Varianta 5) – Toate obiectivele");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1100, 760);
        frame.setLocationRelativeTo(null);

        JPanel top = new JPanel(new GridLayout(2, 3, 10, 6));
        lblMode = new JLabel("Metodă: -");
        lblPhase = new JLabel("Fază: -");
        lblStatus = new JLabel("Status: STOP");
        lblStock = new JLabel("Stoc: 0/" + D);
        lblProd = new JLabel("Total produse: 0/" + Z);
        lblCons = new JLabel("Total consumate: 0/" + Z);

        top.add(lblMode);
        top.add(lblPhase);
        top.add(lblStatus);
        top.add(lblStock);
        top.add(lblProd);
        top.add(lblCons);

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(area);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbMode = new JComboBox<>(Mode.values());

        btnStart = new JButton("START");
        btnStop = new JButton("STOP");
        btnClear = new JButton("CLEAR");
        btnStop.setEnabled(false);

        bottom.add(new JLabel("Alege metoda: "));
        bottom.add(cmbMode);
        bottom.add(btnStart);
        bottom.add(btnStop);
        bottom.add(btnClear);

        btnStart.addActionListener(e -> {
            if (running) return;
            running = true;

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            cmbMode.setEnabled(false);
            area.setText("");

            Mode mode = (Mode) cmbMode.getSelectedItem();
            if (mode == Mode.MONITOR_WAIT_NOTIFY) sim = new MonitorSimulation();
            else if (mode == Mode.LOCK_CONDITION) sim = new LockConditionSimulation();
            else sim = new BlockingQueuePhaserSimulation();

            refreshStats(mode.label, "FILL", "RUNNING", 0, 0, 0);
            sim.start();
        });

        btnStop.addActionListener(e -> {
            if (!running) return;
            log("!!! STOP cerut de utilizator.");
            if (sim != null) sim.stop();

            running = false;
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            cmbMode.setEnabled(true);
            refreshStats(String.valueOf(cmbMode.getSelectedItem()), "-", "STOP", 0, 0, 0);
        });

        btnClear.addActionListener(e -> area.setText(""));

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(top, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        refreshStats("-", "-", "STOP", 0, 0, 0);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab6_Varianta5().buildUI());
    }
}
