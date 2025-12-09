package lab5;


import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends JFrame {
    private JTextArea logArea;

    private static final int NR_PRODUCATORI = 4;
    private static final int NR_CONSUMATORI = 2;
    private static final int OBIECTE_PER_CONSUMATOR = 4;
    private static final int BUFFER_SIZE = 5;
    private static final int OBIECTE_PER_PRODUCTIE = 2;

    private final AtomicInteger consumatoriTerminati = new AtomicInteger(0);

    private final BlockingQueue<Character> buffer = new ArrayBlockingQueue<>(BUFFER_SIZE);

    public Main() {
        super("Producător-Consumator - Varianta 8");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startSimulation();
    }

    private void startSimulation() {
        logArea.setText("--- Simularea Producător-Consumator (Variaanta 8) ---\n" +
                "Producatori: " + NR_PRODUCATORI + ", Consumatori: " + NR_CONSUMATORI +
                ", Obiecte/Consumator: " + OBIECTE_PER_CONSUMATOR + ", Capacitate Depozit: " + BUFFER_SIZE + "\n" +
                "--------------------------------------------------\n");

        ExecutorService executor = Executors.newFixedThreadPool(NR_PRODUCATORI + NR_CONSUMATORI);
        final Future<?>[] producatorFutures = new Future<?>[NR_PRODUCATORI];
        for (int i = 1; i <= NR_PRODUCATORI; i++) {
            producatorFutures[i - 1] = executor.submit(new Producer(i, buffer, this));
        }

        for (int i = 1; i <= NR_CONSUMATORI; i++) {
            executor.submit(new Consumer(i, buffer, this, consumatoriTerminati, executor));
        }
        executor.shutdown();
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private static class Producer implements Runnable {
        private final int id;
        private final BlockingQueue<Character> buffer;
        private final Main gui;
        private final Random random = new Random();
        private final char[] consoane = "BCDFGHJKLMNPQRSTVWXYZ".toCharArray();

        public Producer(int id, BlockingQueue<Character> buffer, Main gui) {
            this.id = id;
            this.buffer = buffer;
            this.gui = gui;
        }

        @Override
        public void run() {
            String name = "Producator " + id;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(800 + random.nextInt(1000));

                    for (int i = 0; i < OBIECTE_PER_PRODUCTIE; i++) {
                        char item = consoane[random.nextInt(consoane.length)];

                        if (buffer.remainingCapacity() == 0) {
                            gui.log("[" + name + "] Depozitul e plin (" + buffer.size() + "/" + BUFFER_SIZE + "), așteaptă...");
                        }

                        buffer.put(item);

                        gui.log("  [" + name + "] a produs: " + item +
                                " | Depozit: " + buffer.size() + "/" + BUFFER_SIZE);
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                gui.log("[" + name + "] s-a oprit.");
            }
        }
    }

    private static class Consumer implements Runnable {
        private final int id;
        private final BlockingQueue<Character> buffer;
        private final Main gui;
        private final AtomicInteger consumatoriTerminati;
        private final ExecutorService executor;
        private int consumate = 0;

        public Consumer(int id, BlockingQueue<Character> buffer, Main gui, AtomicInteger consumatoriTerminati, ExecutorService executor) {
            this.id = id;
            this.buffer = buffer;
            this.gui = gui;
            this.consumatoriTerminati = consumatoriTerminati;
            this.executor = executor;
        }

        @Override
        public void run() {
            String name = "Consumator " + id;
            try {
                while (consumate < OBIECTE_PER_CONSUMATOR) {
                    Thread.sleep(1000 + (long) (Math.random() * 1000));

                    if (buffer.isEmpty()) {
                        gui.log("[" + name + "] Depozitul e gol (0/" + BUFFER_SIZE + "), așteaptă...");
                    }
                    char item = buffer.take();
                    consumate++;
                    gui.log("[" + name + "] a consumat: " + item +
                            " | Total consumate: " + consumate + "/" + OBIECTE_PER_CONSUMATOR +
                            " | În depozit: " + buffer.size() + "/" + BUFFER_SIZE);
                }
            } catch (InterruptedException e) {
            } finally {
                gui.log("🎉 [" + name + "] a fost îndestulat cu " + consumate + " obiecte!");

                if (consumatoriTerminati.incrementAndGet() == NR_CONSUMATORI) {
                    gui.log("\n==================================================");
                    gui.log("Toti consumatorii si-au atins obiectivul. Se initiaza oprirea ordonata a executorului.");
                    executor.shutdownNow();
                    gui.log("==================================================");
                }
            }
        }
    }
}
