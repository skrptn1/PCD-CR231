import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerVariant7 {

    // === Parametrii variantei 7 ===
    private static final int PRODUCERS = 3;        // X
    private static final int CONSUMERS = 3;        // Y
    private static final int GOAL = 5;             // Z (fiecare consumator consumă 5)
    private static final int BUFFER_SIZE = 6;      // D
    private static final int F = 2;                // fiecare producător produce câte 2 obiecte / tură

    private static final int TOTAL_NEEDED = PRODUCERS * F * GOAL; // nu e obligatoriu, doar informativ

    private static final BlockingQueue<Character> buffer =
            new ArrayBlockingQueue<>(BUFFER_SIZE);

    private static final AtomicInteger totalProduced = new AtomicInteger(0);
    private static final AtomicInteger totalConsumed = new AtomicInteger(0);

    private static final Map<Integer, AtomicInteger> consumedByConsumer =
            new ConcurrentHashMap<>();

    private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U'};

    public static void main(String[] args) {

        ExecutorService executor =
                Executors.newFixedThreadPool(PRODUCERS + CONSUMERS);

        // inițializăm contoarele consumatorilor
        for (int i = 1; i <= CONSUMERS; i++) {
            consumedByConsumer.put(i, new AtomicInteger(0));
        }

        // pornim producătorii
        for (int i = 1; i <= PRODUCERS; i++) {
            executor.execute(new Producer(i));
        }

        // pornim consumatorii
        for (int i = 1; i <= CONSUMERS; i++) {
            executor.execute(new Consumer(i));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}

        // === RAPORT FINAL ===
        System.out.println("\n========== RAPORT FINAL ==========");
        System.out.println("Total produse: " + totalProduced.get());
        System.out.println("Total consumate: " + totalConsumed.get());
        consumedByConsumer.forEach((id, count) ->
                System.out.println("Consumator " + id + ": " + count.get() + " obiecte consumate"));
        System.out.println("==================================");
    }

    // ===============================
    // CLASA PRODUCĂTOR
    // ===============================
    static class Producer implements Runnable {
        private final int id;
        private final Random random = new Random();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {

                    for (int i = 0; i < F; i++) {  // produce 2 obiecte de fiecare dată
                        if (buffer.remainingCapacity() == 0) {
                            System.out.println("⚠️ [Producător " + id + "] Depozitul e plin, așteaptă...");
                        }

                        char item = VOWELS[random.nextInt(VOWELS.length)];
                        buffer.put(item);

                        int p = totalProduced.incrementAndGet();

                        System.out.println("🧱 [Producător " + id + "] a produs: " + item +
                                " | Total produse: " + p +
                                " | În depozit: " + buffer.size() + "/" + BUFFER_SIZE);

                        // Stop dacă toți consumatorii sunt îndestulați
                        if (allConsumersSatisfied()) {
                            System.out.println("🛑 Producătorul " + id + " s-a finalizat.");
                            return;
                        }
                    }

                    Thread.sleep(200);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ===============================
    // CLASA CONSUMATOR
    // ===============================
    static class Consumer implements Runnable {
        private final int id;

        Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (consumedByConsumer.get(id).get() < GOAL) {

                    if (buffer.isEmpty()) {
                        System.out.println("⚠️ [Consumator " + id + "] Depozitul este gol, așteaptă...");
                    }

                    char item = buffer.take();
                    consumedByConsumer.get(id).incrementAndGet();
                    int c = totalConsumed.incrementAndGet();

                    System.out.println("🍽️ [Consumator " + id + "] a consumat: " + item +
                            " | Total consumate de el: " + consumedByConsumer.get(id).get() +
                            " | În depozit: " + buffer.size() +
                            " | Total global: " + c);

                    Thread.sleep(300);
                }

                System.out.println("✅ Consumatorul " + id + " a fost îndestulat cu 5 obiecte!");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ===============================
    // METODĂ pentru verificare final consumatori
    // ===============================
    private static boolean allConsumersSatisfied() {
        for (AtomicInteger counter : consumedByConsumer.values()) {
            if (counter.get() < GOAL) return false;
        }
        return true;
    }
}
