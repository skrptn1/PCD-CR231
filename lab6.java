package seminare;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class lab6 {

    static final int PRODUCATORI = 2;
    static final int CONSUMATORI = 3;
    static final int CAPACITATE = 8;
    static final int TOTAL_OBIECTE = 40;

    static JTextArea sharedTextArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Producător-Consumator cu Phaser");
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        sharedTextArea = new JTextArea();
        sharedTextArea.setEditable(false);
        sharedTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(sharedTextArea);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton startButton = new JButton("Start Simulare");
        JButton pauseButton = new JButton("Pauză");
        JButton resumeButton = new JButton("Continuă");

        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resumeButton);

        JPanel statusPanel = new JPanel(new GridLayout(1, 4));
        JLabel prodLabel = new JLabel("Producători: " + PRODUCATORI);
        JLabel consLabel = new JLabel("Consumatori: " + CONSUMATORI);
        JLabel capLabel = new JLabel("Capacitate: " + CAPACITATE);
        JLabel totalLabel = new JLabel("Total Obiecte: " + TOTAL_OBIECTE);

        statusPanel.add(prodLabel);
        statusPanel.add(consLabel);
        statusPanel.add(capLabel);
        statusPanel.add(totalLabel);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> log("*** Pauză apăsată ***\n"));
        resumeButton.addActionListener(e -> log("*** Continuare apăsată ***\n"));

        log("=== Aplicație Producător-Consumator cu Phaser ===\n");
        log("Producători: " + PRODUCATORI + ", Consumatori: " + CONSUMATORI + "\n");
        log("Capacitate depozit: " + CAPACITATE + ", Total obiecte: " + TOTAL_OBIECTE + "\n");
        log("\nApăsați 'Start Simulare' pentru a începe.\n");
    }

    private static void startSimulation() {
        new Thread(() -> runSimulation()).start();
    }

    private static void runSimulation() {
        log("\n=== ÎNCEPERE SIMULARE ===\n");

        Depozit depozit = new Depozit();
        Phaser phaser = new Phaser(1);

        Thread[] producatori = new Thread[PRODUCATORI];
        Thread[] consumatori = new Thread[CONSUMATORI];

        for (int i = 0; i < PRODUCATORI; i++) {
            phaser.register();
            producatori[i] = new Producator(depozit, phaser, "Producator-" + (i + 1));
            producatori[i].start();
        }

        for (int i = 0; i < CONSUMATORI; i++) {
            phaser.register();
            consumatori[i] = new Consumator(depozit, phaser, "Consumator-" + (i + 1));
            consumatori[i].start();
        }

        phaser.arriveAndDeregister();

        try {
            for (Thread p : producatori) p.join();
            for (Thread c : consumatori) c.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log("\n=== SIMULARE TERMINATĂ ===\n");
    }

    public static void log(String msg) {
        System.out.print(msg);
        SwingUtilities.invokeLater(() -> {
            sharedTextArea.append(msg);
            sharedTextArea.setCaretPosition(sharedTextArea.getDocument().getLength());
        });
    }

    static class Depozit {
        final Deque<Integer> depozit = new ArrayDeque<>(CAPACITATE);
        final ReentrantLock lock = new ReentrantLock(true);
        final Random random = new Random();

        int totalProds = 0;
        int totalCons = 0;
        volatile boolean terminat = false;

        boolean produce(String name) {
            lock.lock();
            try {
                if (depozit.size() >= CAPACITATE) {
                    log(">>> Depozitul este PLIN! " + name + " așteaptă.\n");
                    return false;
                }

                if (totalProds >= TOTAL_OBIECTE) {
                    return false;
                }

                int numar = random.nextInt(51) * 2 + 100;
                depozit.addLast(numar);
                totalProds++;

                log(name + " a PRODUS: " + numar +
                        " | Dimensiune depozit: " + depozit.size() + "/" + CAPACITATE +
                        " | Total produse: " + totalProds + "/" + TOTAL_OBIECTE + "\n");

                if (depozit.size() == CAPACITATE) {
                    log("*** DEPOZITUL ESTE PLIN! Consumatorii pot începe consumarea. ***\n");
                }

                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean consuma(String name) {
            lock.lock();
            try {
                if (depozit.isEmpty()) {
                    log(">>> Depozitul este GOL! " + name + " așteaptă.\n");
                    return false;
                }

                int numar = depozit.removeFirst();
                totalCons++;

                log(name + " a CONSUMAT: " + numar +
                        " | Dimensiune depozit: " + depozit.size() + "/" + CAPACITATE +
                        " | Total consumate: " + totalCons + "/" + TOTAL_OBIECTE + "\n");

                if (depozit.isEmpty()) {
                    log("*** DEPOZITUL ESTE GOL! Producătorii pot produce din nou. ***\n");
                }

                if (totalCons >= TOTAL_OBIECTE) {
                    terminat = true;
                    log("\n=== PROCESUL S-A TERMINAT! ===\n");
                    log("Total produse: " + totalProds + "\n");
                    log("Total consumate: " + totalCons + "\n");
                }

                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean esteTerminat() {
            lock.lock();
            try {
                return terminat;
            } finally {
                lock.unlock();
            }
        }

        boolean depozitulEstePlin() {
            lock.lock();
            try {
                return depozit.size() == CAPACITATE;
            } finally {
                lock.unlock();
            }
        }

        boolean depozitulEsteGol() {
            lock.lock();
            try {
                return depozit.isEmpty();
            } finally {
                lock.unlock();
            }
        }
    }

    static class Producator extends Thread {
        final Depozit depozit;
        final Phaser phaser;

        Producator(Depozit depozit, Phaser phaser, String name) {
            super(name);
            this.depozit = depozit;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            try {
                while (!depozit.esteTerminat()) {
                    int faza = phaser.getPhase();

                    if (faza % 2 == 0) {
                        // produce until full or total target reached
                        while (depozit.produce(getName())) {
                            if (depozit.depozitulEstePlin()) break;
                        }
                    }

                    phaser.arriveAndAwaitAdvance();

                    if (depozit.esteTerminat()) {
                        break;
                    }
                }
            } catch (Exception e) {
                log("Eroare la " + getName() + ": " + e.getMessage() + "\n");
            } finally {
                phaser.arriveAndDeregister();
                log(getName() + " s-a terminat.\n");
            }
        }
    }

    static class Consumator extends Thread {
        final Depozit depozit;
        final Phaser phaser;

        Consumator(Depozit depozit, Phaser phaser, String name) {
            super(name);
            this.depozit = depozit;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            try {
                while (!depozit.esteTerminat()) {
                    int faza = phaser.getPhase();

                    if (faza % 2 == 1) {
                        // consume until empty or total target reached
                        while (depozit.consuma(getName())) {
                            if (depozit.depozitulEsteGol()) break;
                        }
                    }


                    phaser.arriveAndAwaitAdvance();

                    if (depozit.esteTerminat()) {
                        break;
                    }
                }
            } catch (Exception e) {
                log("Eroare la " + getName() + ": " + e.getMessage() + "\n");
            } finally {
                phaser.arriveAndDeregister();
                log(getName() + " s-a terminat.\n");
            }
        }
    }
}