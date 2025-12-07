package lab5;

import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Exemplu {

    private static final int BUFFER_CAPACITY = 9;
    private static final BlockingQueue<Character> buffer = new ArrayBlockingQueue<>(BUFFER_CAPACITY);

    private static final int CONSUMER_GOAL = 13;
    private static final int PRODUCER_COUNT = 3;
    private static final int CONSUMER_COUNT = 4;

    private static final int TOTAL_OBJECTS = CONSUMER_GOAL * CONSUMER_COUNT; // 52

    // Contoare atomice
    private static final AtomicInteger totalProduced = new AtomicInteger(0);
    private static final AtomicInteger totalConsumed = new AtomicInteger(0);
    private static final Map<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_COUNT +
                CONSUMER_COUNT);

        for (int i = 1; i <= CONSUMER_COUNT; i++) {
            consumerCounters.put(i, new AtomicInteger(0));
        }

        // Pornim producătorii
        for (int i = 1; i <= PRODUCER_COUNT; i++) {
            executor.execute(new Producer(i));
        }

        // Pornim consumatorii
        for (int i = 1; i <= CONSUMER_COUNT; i++) {
            executor.execute(new Consumer(i));
        }

        executor.shutdown();

        try {
            // Așteptăm ca toate thread-urile să se termine
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== RAPORT FINAL ==========");
        System.out.println("Total produse: " + totalProduced.get());
        System.out.println("Total consumate: " + totalConsumed.get());
        consumerCounters.forEach(
                (id, count) -> System.out.println("Consumator " + id + ": " + count.get() + "obiecte consumate"));
        System.out.println("==================================");
    }

    // ================================
    // CLASA PRODUCĂTOR
    // ================================
    static class Producer implements Runnable {
        private final int id;
        private final Random random = new Random();
        private final char[] consonants = "BCDFGHJKLMNPQRSTVWXYZ".toCharArray();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (totalProduced.get() < TOTAL_OBJECTS) {
                    char item = consonants[random.nextInt(consonants.length)];

                    if (buffer.remainingCapacity() == 0) {
                        System.out.println("⚠   [Producător " + id + "] Depozitul e plin, așteaptă...");
                    }

                    buffer.put(item);
                    int produced = totalProduced.incrementAndGet();

                    System.out.println("🧱 [Producător " + id + "] a produs: " +
                            item +
                            " | Total produse: " + produced +
                            " | Capacitate curentă: " + buffer.size() + "/" +
                            BUFFER_CAPACITY);
                    // Thread.sleep(200);

                    if (produced >= TOTAL_OBJECTS) {
                        System.out.println("Producatorul  " + id + " s-a finalizat");
                        break;
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ================================
    // CLASA CONSUMATOR
    // ================================
    static class Consumer implements Runnable {
        private final int id;

        Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (consumerCounters.get(id).get() < CONSUMER_GOAL) {
                    if (buffer.isEmpty()) {
                        System.out.println("⚠   [Consumator " + id + "] Depozitul e gol, așteaptă...");
                    }

                    char item = buffer.take();
                    consumerCounters.get(id).incrementAndGet();
                    int consumed = totalConsumed.incrementAndGet();

                    System.out.println("🍽  [Consumator " + id + "] a consumat: " +
                            item +
                            " | Total consumate: " + consumerCounters.get(id).get() +
                            " | În depozit: " + buffer.size() +
                            " | Total global: " + consumed);

                    Thread.sleep(300);
                }

                System.out.println("✅ [Consumator " + id + "] a fost îndestulat cu 13 obiecte!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}