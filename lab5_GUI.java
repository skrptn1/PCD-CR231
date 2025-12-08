import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class lab5_GUI extends JFrame {

    private static final int BUFFER_CAPACITY = 10;
    private static final int PRODUCER_COUNT = 4;
    private static final int CONSUMER_COUNT = 3;
    private static final int CONSUMER_GOAL = 3;
    private static final int TOTAL_OBJECTS = CONSUMER_COUNT * CONSUMER_GOAL;

    private final Object produceLock = new Object();

    private BlockingQueue<Character> depozit;
    private ExecutorService executor;

    private AtomicInteger totalProduced = new AtomicInteger(0);
    private AtomicInteger totalConsumed = new AtomicInteger(0);
    private Map<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();

    private JTextArea logArea = new JTextArea();
    private JButton startBtn = new JButton("Porneste");

    public lab5_GUI() {
        setTitle("Producer–Consumer (FIX CORECT)");
        setSize(750, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea.setEditable(false);
        startBtn.addActionListener(e -> start());

        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(startBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void start() {
        startBtn.setEnabled(false);
        logArea.setText("");

        depozit = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
        executor = Executors.newFixedThreadPool(PRODUCER_COUNT + CONSUMER_COUNT);

        totalProduced.set(0);
        totalConsumed.set(0);
        consumerCounters.clear();

        for (int i = 1; i <= CONSUMER_COUNT; i++)
            consumerCounters.put(i, new AtomicInteger(0));

        for (int i = 1; i <= PRODUCER_COUNT; i++)
            executor.execute(new Producer(i));

        for (int i = 1; i <= CONSUMER_COUNT; i++)
            executor.submit(new Consumer(i));

        executor.execute(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {}

            log("\n========== RAPORT FINAL ==========");
            log("Total produse: " + totalProduced.get());
            log("Total consumate: " + totalConsumed.get());
            consumerCounters.forEach((id, count) ->
                    log("Consumator " + id + ": " + count.get() + " obiecte"));
            log("==================================");

            SwingUtilities.invokeLater(() -> startBtn.setEnabled(true));
        });
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

   
    class Producer implements Runnable {

        private final int id;
        private final Random r = new Random();
        private final char[] vocale = {'A','E','I','O','U'};

        Producer(int id) { this.id = id; }

        @Override
        public void run() {
            try {
                while (true) {

                    char v1 = vocale[r.nextInt(vocale.length)];
                    char v2 = vocale[r.nextInt(vocale.length)];
                    boolean produs2 = true;

                    synchronized (produceLock) {

                        if (totalProduced.get() >= TOTAL_OBJECTS) break;

                        depozit.put(v1);
                        totalProduced.incrementAndGet();

                        if (totalProduced.get() < TOTAL_OBJECTS) {
                            depozit.put(v2);
                            totalProduced.incrementAndGet();
                        } else {
                            produs2 = false;
                        }

                        if (produs2) {
                            log("[Producator " + id + "] a produs: " + v1 + " " + v2 +
                                    " | Total produse: " + totalProduced.get() +
                                    " | In depozit: " + depozit.size() + "/" + BUFFER_CAPACITY);
                        }
                    }

                    Thread.sleep(200);
                }

                log("Producator " + id + " s-a finalizat");

            } catch (InterruptedException ignored) {}
        }
    }


    class Consumer implements Runnable {

        private final int id;

        Consumer(int id) { this.id = id; }

        @Override
        public void run() {
            try {
                while (consumerCounters.get(id).get() < CONSUMER_GOAL) {

                    char v = depozit.take();
                    consumerCounters.get(id).incrementAndGet();
                    int consumed = totalConsumed.incrementAndGet();

                    log("[Consumator " + id + "] a consumat: " + v +
                            " | Consumate de el: " +
                            consumerCounters.get(id).get() +
                            " | In depozit: " + depozit.size() +
                            " | Total global: " + consumed);

                    Thread.sleep(300);
                }

                log("[Consumator " + id + "] a fost indestulat (" +
                        CONSUMER_GOAL + " obiecte)");

            } catch (InterruptedException ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(lab5_GUI::new);
    }
}
