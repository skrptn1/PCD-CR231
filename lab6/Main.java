package lab6;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends JFrame {
    static final int X_PRODUCERS = 4;
    static final int Y_CONSUMERS = 2;
    static final int Z_TARGET = 45;
    static final int D_CAPACITY = 5;
    static final String CONSOANE = "BCDFGHJKLMNPQRSTVWXYZ";

    private final JTextArea logArea = new JTextArea(15, 40);
    private final JProgressBar progressBar = new JProgressBar(0, Z_TARGET);
    private final JLabel statusLabel = new JLabel("Faza: Așteptare Start");
    private final JPanel depotPanel = new JPanel(new FlowLayout());
    private final Depozit depozit;

    public Main() {
        setTitle("Sistem Producător-Consumator (Varianta 8 - Consoane)");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        progressBar.setStringPainted(true);
        topPanel.add(statusLabel);
        topPanel.add(progressBar);
        add(topPanel, BorderLayout.NORTH);

        depotPanel.setBorder(BorderFactory.createTitledBorder("Stoc Depozit (Capacitate: " + D_CAPACITY + ")"));
        add(depotPanel, BorderLayout.CENTER);

        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        depozit = new Depozit();
        setVisible(true);
        startSimulation();
    }

    private void updateUI(String message, int currentTotal, String phase) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            progressBar.setValue(currentTotal);
            statusLabel.setText("Faza: " + phase + " | Total procesat: " + currentTotal + "/" + Z_TARGET);
            depotPanel.removeAll();
            for (Character c : depozit.getBufferSnapshot()) {
                JLabel label = new JLabel(c.toString());
                label.setOpaque(true);
                label.setBackground(Color.CYAN);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setPreferredSize(new Dimension(40, 40));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                depotPanel.add(label);
            }
            depotPanel.revalidate();
            depotPanel.repaint();
        });
    }

    class Depozit {
        private final Deque<Character> buffer = new ArrayDeque<>(D_CAPACITY);
        private final ReentrantLock lock = new ReentrantLock(true);
        private final Random rnd = new Random();
        private int producedTotal = 0;
        private int consumedTotal = 0;
        volatile boolean done = false;

        public synchronized Character[] getBufferSnapshot() {
            return buffer.toArray(new Character[0]);
        }

        boolean tryProduce(String name) {
            lock.lock();
            try {
                if (producedTotal >= Z_TARGET || buffer.size() >= D_CAPACITY) return false;
                char consoana = CONSOANE.charAt(rnd.nextInt(CONSOANE.length()));
                buffer.addLast(consoana);
                producedTotal++;
                updateUI(name + " a produs '" + consoana + "'", producedTotal, "UMPLERE (FILL)");
                if (buffer.size() == D_CAPACITY) updateUI(">>> DEPOZIT PLIN!", producedTotal, "UMPLERE (FILL)");
                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean tryConsume(String name) {
            lock.lock();
            try {
                if (buffer.isEmpty()) return false;
                char val = buffer.removeFirst();
                consumedTotal++;
                updateUI(name + " a consumat '" + val + "'", consumedTotal, "GOLIRE (DRAIN)");
                if (buffer.isEmpty()) {
                    updateUI("<<< DEPOZIT GOL!", consumedTotal, "GOLIRE (DRAIN)");
                    if (consumedTotal >= Z_TARGET) done = true;
                }
                return true;
            } finally {
                lock.unlock();
            }
        }
    }

    private void startSimulation() {
        Phaser phaser = new Phaser(X_PRODUCERS + Y_CONSUMERS);

        for (int i = 1; i <= X_PRODUCERS; i++) {
            final String name = "Prod-" + i;
            new Thread(() -> {
                while (!depozit.done) {
                    if (phaser.getPhase() % 2 == 0) {
                        while (!depozit.done && depozit.tryProduce(name)) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    try {
                        phaser.arriveAndAwaitAdvance();
                    } catch (Exception e) {
                        break;
                    }
                }
            }).start();
        }

        for (int i = 1; i <= Y_CONSUMERS; i++) {
            final String name = "Cons-" + i;
            new Thread(() -> {
                while (!depozit.done) {
                    if (phaser.getPhase() % 2 == 1) {
                        while (!depozit.done && depozit.tryConsume(name)) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    try {
                        phaser.arriveAndAwaitAdvance();
                    } catch (Exception e) {
                        break;
                    }
                }
                if (depozit.done) updateUI("=== SIMULARE FINALIZATA ===", Z_TARGET, "TERMINAT");
            }).start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}