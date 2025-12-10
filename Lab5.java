import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Lab5 {

    // Cerinta
    static final int X = 2;     // producatori
    static final int Y = 5;     // consumatori
    static final int Z = 13;    // fiecare consumator consuma Z obiecte
    static final int D = 12;    // capacitatea depozitului
    static final int F = 2;     // fiecare producator produce cate 2 obiecte pe tura

    static final int TOTAL_OBJECTS = Y * Z; // 65 (doar informativ)

    // Contoare
    static final AtomicInteger totalConsumed = new AtomicInteger(0);
    static final AtomicInteger consumersDone = new AtomicInteger(0);
    static final ConcurrentHashMap<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();

    // Depozit comun
    static final Store store = new Store(D);

    // UI
    static JTextArea outputArea;

    public static void main(String[] args) {
        for (int i = 1; i <= Y; i++) consumerCounters.put(i, new AtomicInteger(0));

        SwingUtilities.invokeLater(Lab5::createAndShowGUI);

        // Simulare pe thread separat (nu blocheaza UI)
        new Thread(Lab5::runSimulation, "Simulation-Thread").start();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Lab 5 - Producer/Consumer (Thread Pool) - Output");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(980, 600);
        frame.setLocationRelativeTo(null);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        log("Aplicatia a pornit.");
        log("Parametri: X=" + X + " producatori, Y=" + Y + " consumatori, Z=" + Z +
                ", D=" + D + " (capacitate), F=" + F + " pe tura, TOTAL=" + TOTAL_OBJECTS);
        log("Obiecte produse: NUMERE PARE.");
        log("------------------------------------------------------------");
    }

    private static void runSimulation() {
        long start = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(X + Y);

        for (int i = 1; i <= X; i++) executor.execute(new Producer(i));
        for (int i = 1; i <= Y; i++) executor.execute(new Consumer(i));

        executor.shutdown();
        try {
            executor.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log("Eroare: executia a fost intrerupta.");
        }

        long end = System.nanoTime();
        long ms = (end - start) / 1_000_000;

        log("");
        log("========== RAPORT FINAL ==========");
        log("Total consumate: " + totalConsumed.get());
        consumerCounters.forEach((id, count) ->
                log("Consumator " + id + ": " + count.get() + " obiecte consumate"));
        log("Timp executie: " + ms + " ms");
        log("==================================");
    }

    // Logger pentru UI (thread-safe)
    static void log(String msg) {
        String line = msg + "\n";
        if (outputArea == null) {
            System.out.print(line);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            outputArea.append(line);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    // =========================================================
    // Depozit comun cu sincronizare: synchronized + wait/notifyAll
    // =========================================================
    static class Store {
        private final int capacity;
        private final Deque<Integer> buffer = new ArrayDeque<>();
        private boolean closed = false;

        Store(int capacity) {
            this.capacity = capacity;
        }

        public synchronized boolean put(int item, int producerId) throws InterruptedException {
            while (buffer.size() == capacity && !closed) {
                log("[Producator " + producerId + "] Depozitul este PLIN, asteapta... (" +
                        buffer.size() + "/" + capacity + ")");
                wait();
            }
            if (closed) return false;

            buffer.addLast(item);
            log("[Producator " + producerId + "] a produs: " + item +
                    " | In depozit: " + buffer.size() + "/" + capacity);

            notifyAll();
            return true;
        }

        public synchronized Integer take(int consumerId) throws InterruptedException {
            while (buffer.isEmpty() && !closed) {
                log("[Consumator " + consumerId + "] Depozitul este GOL, asteapta...");
                wait();
            }
            if (buffer.isEmpty() && closed) return null;

            int item = buffer.removeFirst();
            notifyAll();
            return item;
        }

        public synchronized void close() {
            closed = true;
            notifyAll();
        }

        public synchronized boolean isClosed() {
            return closed;
        }

        public synchronized int size() {
            return buffer.size();
        }

        public int getCapacity() {
            return capacity;
        }
    }

    // =========================================================
    // Producator (produce numere pare, in ture de F=2)
    // =========================================================
    static class Producer implements Runnable {
        private final int id;
        private final Random rnd = new Random();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (!store.isClosed()) {
                    for (int k = 0; k < F; k++) {
                        if (store.isClosed()) break;

                        int evenItem = (rnd.nextInt(500) + 1) * 2; // numar PAR
                        boolean ok = store.put(evenItem, id);
                        if (!ok) {
                            log("[Producator " + id + "] S-a oprit (toti consumatorii sunt indestulati).");
                            return;
                        }

                        Thread.sleep(60);
                    }
                }
                log("[Producator " + id + "] S-a oprit (depozit inchis).");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("[Producator " + id + "] Intrerupt.");
            }
        }
    }

    // =========================================================
    // Consumator (consuma pana la Z=13)
    // =========================================================
    static class Consumer implements Runnable {
        private final int id;

        Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (consumerCounters.get(id).get() < Z) {
                    Integer item = store.take(id);
                    if (item == null) break; // depozit inchis si gol (caz de siguranta)

                    int local = consumerCounters.get(id).incrementAndGet();
                    int global = totalConsumed.incrementAndGet();

                    log("[Consumator " + id + "] a consumat: " + item +
                            " | Local: " + local + "/" + Z +
                            " | In depozit: " + store.size() + "/" + store.getCapacity() +
                            " | Total global: " + global);

                    Thread.sleep(110);
                }

                log("[Consumator " + id + "] A fost indestulat cu " + Z + " obiecte.");

                // Cand ultimul consumator termina, inchidem depozitul ca sa oprim producatorii
                if (consumersDone.incrementAndGet() == Y) {
                    log("Toti consumatorii sunt indestulati. Oprim producatorii.");
                    store.close();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("[Consumator " + id + "] Intrerupt.");
            }
        }
    }
}