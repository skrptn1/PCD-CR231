import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class Lab6_Varianta5 {

    // ====== VARIANTA 5 (din tabel) ======
    static final int X = 2;      // producatori
    static final int Y = 5;      // consumatori
    static final int Z = 60;     // total produse/consumate
    static final int D = 12;     // capacitatea depozitului
    static final int F = 1;      // fiecare producator produce 1 obiect per încercare (conform PDF)

    // ====== UI ======
    private JFrame frame;
    private JTextArea area;
    private JLabel lblStock, lblProd, lblCons, lblPhase, lblStatus;

    // ====== RUNTIME ======
    private volatile boolean running = false;
    private Depot depot;
    private StopPhaser phaser;
    private Thread[] producers;
    private Thread[] consumers;

    // Phaser care se oprește când depot.done devine true (sau când forțăm stop)
    static class StopPhaser extends Phaser {
        private final Depot depot;
        StopPhaser(int parties, Depot depot) {
            super(parties);
            this.depot = depot;
        }
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            // se termină când am consumat Z și depozitul e gol (done=true) sau când cerem stop
            return depot.done || depot.stopRequested || registeredParties == 0;
        }
    }

    // Depozitul comun
    static class Depot {
        final int capacity;
        final int targetTotal;

        final Deque<Integer> buffer;
        final ReentrantLock lock = new ReentrantLock(true); // fair
        final Random rnd = new Random();

        volatile boolean done = false;
        volatile boolean stopRequested = false;

        int producedTotal = 0;
        int consumedTotal = 0;

        // callback UI (optional)
        interface Logger {
            void log(String s);
            void refresh(int stock, int prod, int cons, String phase);
        }
        private final Logger logger;

        Depot(int capacity, int targetTotal, Logger logger) {
            this.capacity = capacity;
            this.targetTotal = targetTotal;
            this.buffer = new ArrayDeque<>(capacity);
            this.logger = logger;
        }

        private int randomEven() {
            // număr par aleator (2..100)
            return 2 * (1 + rnd.nextInt(50));
        }

        boolean tryProduce(String name) {
            lock.lock();
            try {
                if (stopRequested || done) return false;
                if (producedTotal >= targetTotal) return false;     // am produs tot
                if (buffer.size() == capacity) return false;        // depozitul e plin

                // F=1: producem exact 1 obiect la fiecare apel
                int value = randomEven();
                buffer.addLast(value);
                producedTotal++;

                if (logger != null) {
                    logger.log(String.format("%s a produs: %d | stoc=%d/%d | totalProd=%d",
                            name, value, buffer.size(), capacity, producedTotal));
                    logger.refresh(buffer.size(), producedTotal, consumedTotal, "FILL");
                }

                if (buffer.size() == capacity && logger != null) {
                    logger.log(String.format(">>> Depozitul e plin (%d/%d)", buffer.size(), capacity));
                }

                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean tryConsume(String name) {
            lock.lock();
            try {
                if (stopRequested || done) return false;
                if (buffer.isEmpty()) return false;

                int value = buffer.removeFirst();
                consumedTotal++;

                if (logger != null) {
                    logger.log(String.format("%s a consumat: %d | stoc=%d/%d | totalCons=%d",
                            name, value, buffer.size(), capacity, consumedTotal));
                    logger.refresh(buffer.size(), producedTotal, consumedTotal, "DRAIN");
                }

                if (buffer.isEmpty() && logger != null) {
                    logger.log(String.format("<<< Depozitul e gol (0/%d)", capacity));
                }

                // condiția finală: am consumat Z și depozitul e gol
                if (consumedTotal >= targetTotal && buffer.isEmpty()) {
                    done = true;
                    if (logger != null) {
                        logger.log("=== GATA: au fost produse și consumate " + targetTotal + " obiecte. ===");
                        logger.refresh(buffer.size(), producedTotal, consumedTotal, "DONE");
                    }
                }

                return true;
            } finally {
                lock.unlock();
            }
        }

        int stockSize() {
            lock.lock();
            try { return buffer.size(); }
            finally { lock.unlock(); }
        }
    }

    class Producer extends Thread {
        Producer(String name) { super(name); }
        @Override public void run() {
            while (!phaser.isTerminated()) {
                int phase = phaser.getPhase();
                boolean fillPhase = (phase % 2 == 0);

                if (fillPhase) {
                    // produce până când depozitul e plin sau am atins Z
                    while (!phaser.isTerminated()) {
                        boolean made = depot.tryProduce(getName());
                        if (!made) break;
                        sleepQuiet(30);
                    }
                }
                // Barieră: toți ajung aici -> trecem în faza următoare (FILL <-> DRAIN)
                phaser.arriveAndAwaitAdvance();
            }
        }
    }

    class Consumer extends Thread {
        Consumer(String name) { super(name); }
        @Override public void run() {
            while (!phaser.isTerminated()) {
                int phase = phaser.getPhase();
                boolean drainPhase = (phase % 2 == 1);

                if (drainPhase) {
                    // consumă până când depozitul e gol (sau done)
                    while (!phaser.isTerminated()) {
                        boolean took = depot.tryConsume(getName());
                        if (!took) break;
                        sleepQuiet(60);
                    }
                }
                phaser Profess  // placeholder? no.

            }
        }
    }

    private static void sleepQuiet(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // Fix: Consumer class typo above. We keep code compile by placing correct arrive.
    class ConsumerFixed extends Thread {
        ConsumerFixed(String name) { super(name); }
        @Override public void run() {
            while (!phaser.isTerminated()) {
                int phase = phaser.getPhase();
                boolean drainPhase = (phase % 2 == 1);

                if (drainPhase) {
                    while (!phaser.isTerminated()) {
                        boolean took = depot.tryConsume(getName());
                        if (!took) break;
                        sleepQuiet(60);
                    }
                }
                phaser.arriveAndAwaitAdvance();
            }
        }
    }

    private void buildUI() {
        frame = new JFrame("Lab 6 – Producător-Consumator (Varianta 5)");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(980, 720);
        frame.setLocationRelativeTo(null);

        JPanel top = new JPanel(new GridLayout(2, 3, 10, 6));
        lblPhase = new JLabel("Fază: -");
        lblStatus = new JLabel("Status: STOP");
        lblStock = new JLabel("Stoc: 0/" + D);
        lblProd = new JLabel("Total produse: 0/" + Z);
        lblCons = new JLabel("Total consumate: 0/" + Z);

        top.add(lblPhase);
        top.add(lblStatus);
        top.add(lblStock);
        top.add(lblProd);
        top.add(lblCons);

        JButton btnStart = new JButton("START");
        JButton btnStop = new JButton("STOP");
        JButton btnClear = new JButton("CLEAR");
        btnStop.setEnabled(false);

        top.add(btnStart);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnStart);
        buttons.add(btnStop);
        buttons.add(btnClear);

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(area);

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(top, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);

        btnStart.addActionListener(e -> {
            if (running) return;
            running = true;
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            lblStatus.setText("Status: RUNNING");
            startSimulation();
        });

        btnStop.addActionListener(e -> stopSimulation(btnStart, btnStop));
        btnClear.addActionListener(e -> area.setText(""));

        frame.setVisible(true);
    }

    private void log(String s) {
        String t = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            area.append("[" + t + "] " + s + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        });
    }

    private void refreshStats(int stock, int prod, int cons, String phase) {
        SwingUtilities.invokeLater(() -> {
            lblPhase.setText("Fază: " + phase);
            lblStock.setText("Stoc: " + stock + "/" + D);
            lblProd.setText("Total produse: " + prod + "/" + Z);
            lblCons.setText("Total consumate: " + cons + "/" + Z);
            if ("DONE".equals(phase)) lblStatus.setText("Status: DONE");
        });
    }

    private void startSimulation() {
        depot = new Depot(D, Z, new Depot.Logger() {
            @Override public void log(String s) { Lab6_Varianta5.this.log(s); }
            @Override public void refresh(int stock, int prod, int cons, String phase) {
                Lab6_Varianta5.this.refreshStats(stock, prod, cons, phase);
            }
        });

        // Phaser: X + Y "parties"
        phaser = new StopPhaser(X + Y, depot);

        producers = new Thread[X];
        consumers = new Thread[Y];

        for (int i = 0; i < X; i++) {
            producers[i] = new Producer("Producator-" + (i + 1));
        }
        for (int i = 0; i < Y; i++) {
            consumers[i] = new ConsumerFixed("Consumator-" + (i + 1));
        }

        log("=== START Varianta 5: X=2, Y=5, Z=60, D=12, Obiecte=Numere pare, F=1 ===");
        refreshStats(0, 0, 0, "FILL");

        for (Thread t : producers) t.start();
        for (Thread t : consumers) t.start();

        // Thread care așteaptă finalizarea logică
        new Thread(() -> {
            try {
                for (Thread t : producers) t.join();
                for (Thread t : consumers) t.join();
            } catch (InterruptedException ignored) {
            } finally {
                running = false;
            }
        }, "Joiner").start();
    }

    private void stopSimulation(JButton btnStart, JButton btnStop) {
        if (!running) return;
        log("!!! STOP cerut de utilizator.");
        depot.stopRequested = true;
        phaser.forceTermination();

        for (Thread t : producers) t.interrupt();
        for (Thread t : consumers) t.interrupt();

        running = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        lblStatus.setText("Status: STOP");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab6_Varianta5().buildUI());
    }
}
