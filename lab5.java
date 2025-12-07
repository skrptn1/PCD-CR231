import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class lab5 {

    // Parametrii problemei:
    private static final int PRODUCER_COUNT = 4; // X = 4
    private static final int CONSUMER_COUNT = 3; // Y = 3
    private static final int CONSUMER_GOAL = 3;  // Z = 3
    private static final int BUFFER_CAPACITY = 10; // D = 10
    private static final int PRODUCTION_BATCH_SIZE = 2; // F = 2

    // Calculul obiectivelor:
    private static final int TOTAL_OBJECTS_REQUIRED = CONSUMER_GOAL * CONSUMER_COUNT; // 3 * 3 = 9
    
    // Resurse partajate și contoare
    private static final BlockingQueue<Character> buffer = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
    private static final AtomicInteger totalProduced = new AtomicInteger(0);
    private static final AtomicInteger totalConsumed = new AtomicInteger(0);
    private static final Map<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();
    
    // Obiecte de produs (Vocale)
    private static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u'};

    public static void main(String[] args) {
        // Inițializăm ExecutorService cu un pool fix de fire
        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_COUNT + CONSUMER_COUNT);

        // Inițializăm contoarele pentru fiecare consumator
        for (int i = 1; i <= CONSUMER_COUNT; i++) {
            consumerCounters.put(i, new AtomicInteger(0));
        }

        System.out.println("🏁 Simularea Producator-Consumator a inceput...");
        System.out.println("Obiectiv: Fiecare din cei " + CONSUMER_COUNT + " consumatori trebuie sa consume " + CONSUMER_GOAL + " obiecte (Total: " + TOTAL_OBJECTS_REQUIRED + " obiecte).");
                
        // Pornim producătorii
        for (int i = 1; i <= PRODUCER_COUNT; i++) {
            executor.execute(new Producer(i));
        }

        // Pornim consumatorii
        for (int i = 1; i <= CONSUMER_COUNT; i++) {
            executor.execute(new Consumer(i));
        }

        // Executorul se închide după finalizarea task-urilor
        executor.shutdown(); 

        try {
            // Așteptăm ca toate thread-urile să se termine
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("❌ Timp de așteptare expirat. Unele thread-uri nu s-au finalizat.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Asteptarea thread-urilor a fost întrerupta.");
        }

        System.out.println("\n========== RAPORT FINAL ==========");
        System.out.println("Total produse: " + totalProduced.get());
        System.out.println("Total consumate: " + totalConsumed.get());
        consumerCounters.forEach((id, count) ->
            System.out.println("Consumator " + id + ": " + count.get() + " obiecte consumate (Obiectiv: " + CONSUMER_GOAL + ")")
        );
        System.out.println("==================================");
    }

    // ================================
    // CLASA PRODUCĂTOR
    // ================================
    static class Producer implements Runnable {
        private final int id;
        private final Random random = new Random();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                // Producătorii rulează atâta timp cât nu s-a atins totalul necesar
                while (totalConsumed.get() < TOTAL_OBJECTS_REQUIRED) {
                    
                    // Verificăm dacă mai avem obiecte de produs în total (deși consumatorii dictează finalizarea)
                    if (totalProduced.get() >= TOTAL_OBJECTS_REQUIRED && buffer.isEmpty()) {
                        break; 
                    }

                    // Logica de producție F=2 obiecte:
                    int itemsProducedThisCycle = 0;
                    for(int i = 0; i < PRODUCTION_BATCH_SIZE; i++) {
                        // Verificăm capacitatea înainte de a produce
                        if (buffer.remainingCapacity() == 0) {
                            System.out.println("⚠ [Producator " + id + "] Depozitul e plin (" + BUFFER_CAPACITY + "/" + BUFFER_CAPACITY + "), asteapta...");
                            break; // Se oprește producția în acest ciclu dacă bufferul e plin
                        }

                        char item = VOWELS[random.nextInt(VOWELS.length)];
                        
                        buffer.put(item); // Blocant, așteaptă dacă e plin
                        itemsProducedThisCycle++;
                        int producedTotal = totalProduced.incrementAndGet();

                        System.out.println("🧱 [Producator " + id + "] a produs: " + item + 
                            " | Total produse: " + producedTotal + 
                            " | Capacitate curenta: " + buffer.size() + "/" + BUFFER_CAPACITY);
                            
                        if (producedTotal >= TOTAL_OBJECTS_REQUIRED) {
                             // Dacă totalul necesar a fost atins, terminăm producția
                             System.out.println("✅ Producatorul " + id + " si-a finalizat sarcina (Total: " + producedTotal + ")");
                             return; 
                        }
                    }
                    
                    if (itemsProducedThisCycle > 0) {
                        Thread.sleep(random.nextInt(200)); // Timp de pauză după producția unui batch (F=2)
                    } else {
                         // Dacă nu a produs nimic din cauza bufferului plin, așteaptă mai puțin pentru a verifica din nou
                        Thread.sleep(50); 
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
        private final Random random = new Random(); // Variabila 'random' adăugată pentru a evita eroarea

        Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                // Consumatorul rulează atâta timp cât nu și-a atins obiectivul Z
                while (consumerCounters.get(id).get() < CONSUMER_GOAL) {
                    
                    // Mesaj despre depozitul gol
                    if (buffer.isEmpty() && totalProduced.get() < TOTAL_OBJECTS_REQUIRED) {
                        System.out.println("⏳ [Consumator " + id + "] Depozitul e gol, asteapta...");
                    }
                    
                    Character item = buffer.take(); // Blocant, așteaptă dacă e gol
                    
                    // Sincronizare pe contorul global și pe cel local (Logica lipsă a fost adăugată aici)
                    int consumedTotal = totalConsumed.incrementAndGet();
                    int consumedLocal = consumerCounters.get(id).incrementAndGet();
                    
                    System.out.println("🗑️ [Consumator " + id + "] a consumat: " + item + 
                        " | Local: " + consumedLocal + "/" + CONSUMER_GOAL +
                        " | Total consumate: " + consumedTotal +
                        " | Capacitate curenta: " + buffer.size() + "/" + BUFFER_CAPACITY);

                    if (consumedLocal >= CONSUMER_GOAL) {
                        System.out.println("🎉 Consumatorul " + id + " si-a atins obiectivul (Z=" + CONSUMER_GOAL + ").");
                        break; 
                    }
                    
                    Thread.sleep(random.nextInt(500)); // Timp de procesare/consum
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}