/*
 * 3 producatori si 4 consumatori
 * depozitul este de 14 obiecte
 * se produc 52 de obiecte in total
 * simboluri inafara de litere si cifre
 * consumatorii sa consume obiectele produse
 * de realizat problema cu pull de threaduri
 */

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerPull {

    static final int TOTAL_PRODUSE = 52;
    static final int NUM_PRODUCERS = 3;
    static final int NUM_CONSUMERS = 4;
    static final int DEPOZIT_SIZE = 14;
    static final char POISON_PILL = 'X';

    public static void main(String[] args) {

        BlockingQueue<Character> depozit = new ArrayBlockingQueue<>(DEPOZIT_SIZE);
        AtomicInteger producedCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(NUM_PRODUCERS);

        List<Character> symbols = Arrays.asList(
                '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=',
                '{', '}', '[', ']', ':', ';', '"', '\'', '<', '>', ',', '.', '?', '/'
        );

        Random randomSimbols = new Random();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_PRODUCERS + NUM_CONSUMERS);

        
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            int producerId = i;

            executor.submit(() -> {
                try {
                    while (true) {
                        int index = producedCount.incrementAndGet();
                        if (index > TOTAL_PRODUSE) break;

                        char symbol = symbols.get(randomSimbols.nextInt(symbols.size()));
                        depozit.put(symbol);

                        System.out.println("Producator " + producerId +
                                " a produs: " + symbol + " (obiect #" + index + ")");
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            int consumerId = i;

            executor.submit(() -> {
                try {
                    while (true) {
                        Character symbol = depozit.take();

                        if (symbol == POISON_PILL) {
                            System.out.println("Consumator " + consumerId + " se opreste.");
                            break;
                        }

                        System.out.println("Consumator " + consumerId +
                                " a consumat: " + symbol);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (int j = 0; j < NUM_CONSUMERS; j++) {
            try {
                depozit.put(POISON_PILL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        executor.shutdown();

        try {
            boolean terminated = executor.awaitTermination(60, TimeUnit.SECONDS);
            if (!terminated) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Toate threadurile au terminat executia.");
        }
    }
}
