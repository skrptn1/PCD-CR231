import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class lab5_GUI {

    // =========================================================================
    // PARAMETRI ȘI RESURSE PARTAJATE (STATICE)
    // =========================================================================
    private static final int PRODUCER_COUNT = 4; // X = 4
    private static final int CONSUMER_COUNT = 3; // Y = 3
    private static final int CONSUMER_GOAL = 3;  // Z = 3
    private static final int BUFFER_CAPACITY = 10; // D = 10
    private static final int PRODUCTION_BATCH_SIZE = 2; // F = 2

    private static final int TOTAL_OBJECTS_REQUIRED = CONSUMER_GOAL * CONSUMER_COUNT; // 9
    
    // Resurse partajate și contoare
    private static final BlockingQueue<Character> buffer = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
    private static final AtomicInteger totalProduced = new AtomicInteger(0);
    private static final AtomicInteger totalConsumed = new AtomicInteger(0);
    private static final Map<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();
    
    private static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u'};

    // Componente GUI
    private static JLabel bufferStatusLabel;
    private static JLabel producedLabel;
    private static JLabel consumedLabel;
    private static JTextArea logArea;
    private static JLabel[] consumerGoalLabels;

    // =========================================================================
    // METODE GUI
    // =========================================================================

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Simulare Producator-Consumator (Lab 5)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // --- Panoul de Sus (Starea Globală) ---
        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        
        bufferStatusLabel = new JLabel("Depozit: 0/" + BUFFER_CAPACITY, SwingConstants.CENTER);
        producedLabel = new JLabel("Total Produs: 0", SwingConstants.CENTER);
        consumedLabel = new JLabel("Total Consumat: 0 (Obiectiv: " + TOTAL_OBJECTS_REQUIRED + ")", SwingConstants.CENTER);
        
        topPanel.add(new JLabel("CAPACITATE:", SwingConstants.RIGHT));
        topPanel.add(bufferStatusLabel);
        topPanel.add(new JLabel("PRODUS GLOBAL:", SwingConstants.RIGHT));
        topPanel.add(producedLabel);
        topPanel.add(new JLabel("CONSUMAT GLOBAL:", SwingConstants.RIGHT));
        topPanel.add(consumedLabel);

        // --- Panoul Central (Progres Consumatori) ---
        JPanel consumerPanel = new JPanel(new GridLayout(CONSUMER_COUNT, 1));
        consumerPanel.setBorder(BorderFactory.createTitledBorder("Progres Consumatori (Z=" + CONSUMER_GOAL + ")"));
        consumerGoalLabels = new JLabel[CONSUMER_COUNT];
        
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumerGoalLabels[i] = new JLabel("Consumator " + (i + 1) + ": 0/" + CONSUMER_GOAL);
            consumerPanel.add(consumerGoalLabels[i]);
        }
        
        // --- Panoul de Jos (Log) ---
        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Jurnal de Evenimente"));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(consumerPanel, BorderLayout.CENTER);
        frame.add(logScrollPane, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        logMessage("🏁 Simularea Producator-Consumator a inceput...");
    }

    private static void logMessage(String message) {
        // Actualizarea GUI trebuie făcută în Firele de Execuție Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // Scroll automat la final
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private static void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            bufferStatusLabel.setText(buffer.size() + "/" + BUFFER_CAPACITY);
            producedLabel.setText("Total Produs: " + totalProduced.get());
            consumedLabel.setText("Total Consumat: " + totalConsumed.get() + " (Obiectiv: " + TOTAL_OBJECTS_REQUIRED + ")");
            
            for (int i = 0; i < CONSUMER_COUNT; i++) {
                int id = i + 1;
                int count = consumerCounters.get(id).get();
                consumerGoalLabels[i].setText("Consumator " + id + ": " + count + "/" + CONSUMER_GOAL);
                if (count >= CONSUMER_GOAL) {
                    consumerGoalLabels[i].setForeground(Color.BLUE);
                    consumerGoalLabels[i].setFont(consumerGoalLabels[i].getFont().deriveFont(Font.BOLD));
                }
            }
            if (totalConsumed.get() >= TOTAL_OBJECTS_REQUIRED) {
                 consumedLabel.setForeground(Color.GREEN.darker());
                 consumedLabel.setFont(consumedLabel.getFont().deriveFont(Font.BOLD));
            }
        });
    }

    // =========================================================================
    // METODA MAIN ȘI LOGICA DE CONCURENȚĂ
    // =========================================================================

    public static void main(String[] args) {
        // Inițializarea GUI pe Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(lab5_GUI::createAndShowGUI);
        
        // Configurarea executorului de thread-uri
        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_COUNT + CONSUMER_COUNT);

        // Inițializăm contoarele pentru fiecare consumator
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

        // Se adaugă un thread de monitorizare simplu pentru a aștepta finalizarea
        // Rulăm pe un thread separat pentru a nu bloca EDT-ul sau main thread-ul de execuție
        new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(300, TimeUnit.SECONDS)) {
                    logMessage("❌ Timp de așteptare expirat. Thread-urile nu s-au finalizat.");
                } else {
                    logMessage("\n========== SIMULARE FINALIZATĂ ==========");
                    logMessage("Total produse: " + totalProduced.get());
                    logMessage("Total consumate: " + totalConsumed.get());
                    consumerCounters.forEach((id, count) ->
                        logMessage("Consumator " + id + ": " + count.get() + " obiecte consumate.")
                    );
                    logMessage("==========================================");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // ==========================================================
    // CLASA PRODUCĂTOR (Producer)
    // ==========================================================
    static class Producer implements Runnable {
        private final int id;
        private final Random random = new Random();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (totalConsumed.get() < TOTAL_OBJECTS_REQUIRED) {
                    
                    if (totalProduced.get() >= TOTAL_OBJECTS_REQUIRED && buffer.isEmpty()) {
                        break; 
                    }

                    int itemsProducedThisCycle = 0;
                    for(int i = 0; i < PRODUCTION_BATCH_SIZE; i++) {
                        
                        if (buffer.remainingCapacity() == 0) {
                            logMessage("⚠ [Producator " + id + "] Depozitul e plin, asteapta...");
                            break;
                        }

                        char item = VOWELS[random.nextInt(VOWELS.length)];
                        
                        buffer.put(item); 
                        itemsProducedThisCycle++;
                        int producedTotal = totalProduced.incrementAndGet();

                        logMessage("🧱 [Producator " + id + "] a produs: " + item + 
                            " | Buffer: " + buffer.size() + "/" + BUFFER_CAPACITY);
                            
                        updateGUI();

                        if (producedTotal >= TOTAL_OBJECTS_REQUIRED) {
                             logMessage("✅ Producatorul " + id + " si-a finalizat sarcina.");
                             return; 
                        }
                    }
                    
                    if (itemsProducedThisCycle > 0) {
                        Thread.sleep(random.nextInt(200)); 
                    } else {
                        Thread.sleep(50); 
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==========================================================
    // CLASA CONSUMATOR (Consumer)
    // ==========================================================
    static class Consumer implements Runnable {
        private final int id;
        private final Random random = new Random(); 

        Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (consumerCounters.get(id).get() < CONSUMER_GOAL) {
                    
                    if (buffer.isEmpty() && totalProduced.get() < TOTAL_OBJECTS_REQUIRED) {
                        logMessage("⏳ [Consumator " + id + "] Depozitul e gol, asteapta...");
                    }
                    
                    Character item = buffer.take(); 
                    
                    int consumedTotal = totalConsumed.incrementAndGet();
                    int consumedLocal = consumerCounters.get(id).incrementAndGet();
                    
                    logMessage("🗑️ [Consumator " + id + "] a consumat: " + item + 
                        " | Local: " + consumedLocal + "/" + CONSUMER_GOAL +
                        " | Buffer: " + buffer.size() + "/" + BUFFER_CAPACITY);

                    updateGUI();

                    if (consumedLocal >= CONSUMER_GOAL) {
                        logMessage("🎉 Consumatorul " + id + " si-a atins obiectivul (Z=" + CONSUMER_GOAL + ").");
                        break; 
                    }
                    
                    Thread.sleep(random.nextInt(500)); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}