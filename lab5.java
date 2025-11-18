// X producatori genereaza aleatoriu F obiecte care sunt consumate de Y consumatori
// Fiecare consumator trebuie sa consume Z obiecte
// Dimensiunea depozitului este D
// X=2, Y=3, Z=11, D=8, Tip obiecte=numere pare, F=2 obiecte per ciclu de productie

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class lab5{
    static final int BUFFER_SIZE = 8; // D
    static final int PRODUCER = 2; // X
    static final int CONSUMER = 3; // Y
    static final int OBJECTS_PER_CONSUMER = 11; // Z
    static final int TOTAL_ITEMS = CONSUMER * OBJECTS_PER_CONSUMER; // 3 * 11 = 33
    static final int OBJECTS_PER_PRODUCTION = 2; // F
    static final Integer POISON_PILL = -1;
    static AtomicInteger consumedCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(BUFFER_SIZE);
        AtomicInteger producedCount = new AtomicInteger(0);
        CountDownLatch producerFinished = new CountDownLatch(PRODUCER);
        
        Random random = new Random();
        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER + CONSUMER);
        
        for(int i = 0; i < PRODUCER; i++) {
            int producerId = i;
            executor.submit(() -> {
                try {
                    while (true) {
                        for (int j = 0; j < OBJECTS_PER_PRODUCTION; j++) {
                            int currentCount = producedCount.incrementAndGet();
                            if (currentCount > TOTAL_ITEMS) {
                                producedCount.decrementAndGet();
                                return;
                            }
                            
                            int evenNumber = random.nextInt(51) * 2;
                            
                            if (buffer.remainingCapacity() == 0) {
                                System.out.println("Producer " + producerId + " - DEPOZITUL ESTE PLIN! Așteaptă...");
                            }
                            buffer.put(evenNumber);
                            System.out.println("Producer " + producerId + " a produs: " + evenNumber + " (elementul #" + currentCount + ")");
                        }
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    producerFinished.countDown();
                }
            });
        }
        for(int i = 0; i < CONSUMER; i++) {
            int consumerId = i;
            executor.submit(() -> {
                int consumedByThis = 0;
                while (consumedByThis < OBJECTS_PER_CONSUMER) {
                    try {
                        if (buffer.isEmpty()) {
                            System.out.println("Consumatorul " + consumerId + " - DEPOZITUL ESTE GOL! Asteapta...");
                        }
                        
                        Integer item = buffer.take();
                        if (POISON_PILL.equals(item)) {
                            System.out.println("Consumatorul " + consumerId + " a primit poison pill.");
                            break;
                        }
                        
                        consumedByThis++;
                        int totalCount = consumedCount.incrementAndGet();
                        System.out.println("Consumatorul " + consumerId + " a consumat: " + item + " (obiect #" + consumedByThis + "/" + OBJECTS_PER_CONSUMER + ", total: " + totalCount + ")");
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                System.out.println("Consumatorul " + consumerId + " este îndestulat! (a consumat " + consumedByThis + " obiecte)");
            });
        }

        producerFinished.await();
        
        for (int i = 0; i < CONSUMER; i++) {
            buffer.put(POISON_PILL);
        }

        executor.shutdown();
        try {
            boolean terminated = executor.awaitTermination(60, TimeUnit.SECONDS);
            if (!terminated) {
                executor.shutdownNow();
                System.out.println("Toate procesele au fost fortat finalizate.");
            }
        } catch (InterruptedException e) {
            System.out.println("Eroare la terminarea executorului.");
        } finally {
            executor.shutdownNow();
        }
        System.out.println("Toate procesele au fost finalizate corect.");
    }
}