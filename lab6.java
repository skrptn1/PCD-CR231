import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class lab6 {
    private static final int X_PRODUCATORI = 4;
    private static final int Y_CONSUMATORI = 3;
    private static final int Z_LIMIT = 50;
    private static final int D_CAPACITATE = 10;
    
    static class Depozit {
        private final Queue<String> buffer;
        private final int capacitateMaxima;
        private int obiecteProduse = 0;
        private int obiecteConsumate = 0;
        private final int limitaZ;
        
        
        private GuiMonitor monitor; 

        private final ReentrantLock lock = new ReentrantLock();
        private final Condition estePlin = lock.newCondition();
        private final Condition esteGol = lock.newCondition();
        
        private boolean trebuieConsumat = false; 
        private boolean estePrimaUmplere = true; 

        public Depozit(int capacitate, int limita, GuiMonitor monitor) {
            this.capacitateMaxima = capacitate;
            this.limitaZ = limita;
            this.buffer = new LinkedList<>();
            this.monitor = monitor;
            monitor.appendLog("Depozit inițializat cu capacitatea: " + capacitate);
        }

       
        private void updateGui(String msg) {
            monitor.updateProgressBar(buffer.size(), capacitateMaxima, obiecteConsumate, limitaZ);
            monitor.appendLog(msg);
        }

        public void produce(String obiect, int idProducator) throws InterruptedException {
            lock.lock();
            try {
                if (obiecteProduse >= limitaZ) {
                    estePlin.signalAll();
                    return;
                }

                String logMsg = "";

                while (buffer.size() == capacitateMaxima || (trebuieConsumat && buffer.size() > 0)) {
                    logMsg = "--- Depozitul PLIN/Parțial PLIN (" + buffer.size() + "/" + capacitateMaxima + "). P" + idProducator + " așteaptă golirea. ---";
                    updateGui(logMsg);
                    esteGol.await();
                    if (obiecteProduse >= limitaZ) return;
                }
                
               
                buffer.add(obiect);
                obiecteProduse++;
                logMsg = "P" + idProducator + " a produs: " + obiect + ". Stare: " + buffer.size() + "/" + capacitateMaxima + ". Total produse: " + obiecteProduse;
                updateGui(logMsg);

                
                if (buffer.size() == capacitateMaxima) {
                    trebuieConsumat = true;
                    estePrimaUmplere = false;
                    logMsg = "!!! Depozitul PLIN (" + buffer.size() + "/" + capacitateMaxima + ") !!! Se notifică Consumatorii.";
                    updateGui(logMsg);
                    estePlin.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        public String consuma(int idConsumator) throws InterruptedException {
            lock.lock();
            try {
                if (obiecteConsumate >= limitaZ) {
                    esteGol.signalAll();
                    return null;
                }

                String logMsg = "";
                
                while (buffer.isEmpty() || (estePrimaUmplere && buffer.size() < capacitateMaxima)) {
                    if (buffer.isEmpty()) {
                        logMsg = "--- Depozitul GOL (" + buffer.size() + "/" + capacitateMaxima + "). C" + idConsumator + " așteaptă producția. ---";
                        trebuieConsumat = false;
                        updateGui(logMsg);
                        esteGol.signalAll();
                        
                    }
                    estePlin.await();
                    if (obiecteConsumate >= limitaZ) return null;
                }

                
                String obiect = buffer.remove();
                obiecteConsumate++;
                logMsg = "C" + idConsumator + " a consumat: " + obiect + ". Stare: " + buffer.size() + "/" + capacitateMaxima + ". Total consumate: " + obiecteConsumate;
                updateGui(logMsg);

                
                if (buffer.isEmpty()) {
                    logMsg = "!!! Depozitul GOL (0/" + capacitateMaxima + ") !!! Se notifică Producătorii.";
                    trebuieConsumat = false;
                    updateGui(logMsg);
                    esteGol.signalAll();
                }
                
                return obiect;
            } finally {
                lock.unlock();
            }
        }

        public int getObiecteConsumate() {
            return obiecteConsumate;
        }

        public int getLimitaZ() {
            return limitaZ;
        }
    }

   
    static class Producator extends Thread {
        private final Depozit depozit;
        private final int idProducator;
        private static int contor = 1;

        public Producator(Depozit depozit) {
            this.depozit = depozit;
            this.idProducator = contor++;
        }

        @Override
        public void run() {
            setName("Producator-" + idProducator);
            try {
                while (depozit.getObiecteConsumate() < depozit.getLimitaZ()) {
                    String obiect = "Vocala_" + (depozit.getObiecteConsumate() + 1);
                    depozit.produce(obiect, idProducator);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        }
    }

   
    static class Consumator extends Thread {
        private final Depozit depozit;
        private final int idConsumator;
        private static int contor = 1;

        public Consumator(Depozit depozit) {
            this.depozit = depozit;
            this.idConsumator = contor++;
        }

        @Override
        public void run() {
            setName("Consumator-" + idConsumator);
            try {
                while (depozit.getObiecteConsumate() < depozit.getLimitaZ()) {
                    String obiectConsumat = depozit.consuma(idConsumator);
                    if (obiectConsumat == null) break;
                    Thread.sleep(75);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
             // Notificare oprire (se face în GUI)
        }
    }

   
    static class GuiMonitor extends JFrame {
        private final JTextArea logArea;
        private final JProgressBar bufferBar;
        private final JLabel statusLabel;
        private final JLabel countLabel;

        public GuiMonitor() {
            super("Simulare Producător-Consumator (D=" + D_CAPACITATE + ", Z=" + Z_LIMIT + ")");
            logArea = new JTextArea(20, 50);
            logArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(logArea);

            bufferBar = new JProgressBar(0, D_CAPACITATE);
            bufferBar.setStringPainted(true);
            bufferBar.setPreferredSize(new Dimension(400, 30));

            statusLabel = new JLabel("Status: Așteaptă start...");
            countLabel = new JLabel("Consumate: 0/" + Z_LIMIT);

            // Setări Layout (BorderLayout principal, GridBagLayout pentru Status)
            setLayout(new BorderLayout(10, 10));
            
            JPanel topPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
            topPanel.add(new JLabel("Depozit (Obiecte/Capacitate):"), gbc);
            
            gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
            topPanel.add(bufferBar, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
            topPanel.add(statusLabel, gbc);

            gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            topPanel.add(countLabel, gbc);
            
            add(topPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        
        public void appendLog(final String message) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                // Scroll automat la final
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }

      
        public void updateProgressBar(final int currentSize, final int maxSize, final int consumed, final int limit) {
             SwingUtilities.invokeLater(() -> {
                bufferBar.setValue(currentSize);
                bufferBar.setString(currentSize + "/" + maxSize);
                countLabel.setText("Consumate: " + consumed + "/" + limit);

                if (currentSize == maxSize) {
                    statusLabel.setText("Status: Depozit PLIN. Consumatori activi.");
                    bufferBar.setForeground(Color.RED);
                } else if (currentSize == 0) {
                    statusLabel.setText("Status: Depozit GOL. Producători activi.");
                    bufferBar.setForeground(Color.BLUE);
                } else {
                    statusLabel.setText("Status: În lucru...");
                    bufferBar.setForeground(Color.GREEN);
                }

                if (consumed >= limit) {
                    statusLabel.setText("Status: SIMULARE FINALIZATĂ!");
                    bufferBar.setForeground(Color.DARK_GRAY);
                    appendLog("Simularea s-a încheiat (limita Z=" + limit + " atinsă).");
                }
            });
        }
    }

   
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            GuiMonitor monitor = new GuiMonitor();
            monitor.appendLog("--- Pornire simulare Producator-Consumator ---");
            
            
            Depozit depozit = new Depozit(D_CAPACITATE, Z_LIMIT, monitor);
            
            
            for (int i = 0; i < X_PRODUCATORI; i++) {
                new Producator(depozit).start();
            }
            
            
            for (int i = 0; i < Y_CONSUMATORI; i++) {
                new Consumator(depozit).start();
            }
        });
    }
}