import javax.swing.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class ProdConsVocale {

    static final int PRODUCATORI = 3;
    static final int CONSUMATORI = 3;
    static final int CAPACITATE = 6;
    static final int TOTAL_OBIECTE = 42;
    static final char[] VOCALE = {'A', 'E', 'I', 'O', 'U'};

    public static void main(String[] args) {
      
        JFrame frame = new JFrame("Producător - Consumator");
        frame.setSize(700, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane);
        frame.setVisible(true);

        Depozit depozit = new Depozit(logArea);
        Phaser phaser = new Phaser(1);

        ExecutorService executor = Executors.newFixedThreadPool(PRODUCATORI + CONSUMATORI);

        for (int i = 0; i < PRODUCATORI; i++) {
            phaser.register();
            executor.execute(new Producator(depozit, phaser, "Producator-" + (i + 1)));
        }

        for (int i = 0; i < CONSUMATORI; i++) {
            phaser.register();
            executor.execute(new Consumator(depozit, phaser, "Consumator-" + (i + 1)));
        }

        phaser.arriveAndDeregister();
        executor.shutdown();
    }

    static class Depozit {
        final Deque<Character> buffer = new ArrayDeque<>(CAPACITATE);
        final ReentrantLock lock = new ReentrantLock(true);
        int produseTotal = 0;
        int consumateTotal = 0;
        Random rnd = new Random();
        volatile boolean gata = false;
        final JTextArea logArea;

        Depozit(JTextArea logArea) {
            this.logArea = logArea;
        }

        void log(String msg) {
            SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
        }

        boolean produce(String name) {
            lock.lock();
            try {
                if (produseTotal >= TOTAL_OBIECTE) return false;
                if (buffer.size() >= CAPACITATE) return false;

                char vocal = VOCALE[rnd.nextInt(VOCALE.length)];
                buffer.addLast(vocal);
                produseTotal++;
                log(name + " a produs: " + vocal + " | Depozit: " + buffer);

                if (buffer.size() == CAPACITATE) {
                    log(">>> Depozitul este plin, consumatorii pot începe consumul.");
                }
                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean consuma(String name) {
            lock.lock();
            try {
                if (buffer.isEmpty()) return false;

                char vocal = buffer.removeFirst();
                consumateTotal++;
                log(name + " a consumat: " + vocal + " | Depozit: " + buffer);

                if (buffer.isEmpty()) {
                    log("<<< Depozitul este gol, producătorii pot relua producția.");
                }

                if (consumateTotal >= TOTAL_OBIECTE && buffer.isEmpty() && !gata) {
                    gata = true;
                    log("=== Au fost produse și consumate " + TOTAL_OBIECTE + " obiecte. ===");
                }
                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean depozitPlin() {
            lock.lock();
            try {
                return buffer.size() == CAPACITATE;
            } finally {
                lock.unlock();
            }
        }

        boolean depozitGol() {
            lock.lock();
            try {
                return buffer.isEmpty();
            } finally {
                lock.unlock();
            }
        }
    }

    static class Producator implements Runnable {
        final Depozit depozit;
        final Phaser phaser;
        final String name;

        Producator(Depozit depozit, Phaser phaser, String name) {
            this.depozit = depozit;
            this.phaser = phaser;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                while (!depozit.gata) {
                    int faza = phaser.getPhase();
                    if (faza % 2 == 0) { // FILL
                        while (!depozit.depozitPlin() && !depozit.gata) {
                            boolean produs = depozit.produce(name);
                            if (!produs) Thread.sleep(50);
                        }
                    }
                    phaser.arriveAndAwaitAdvance();
                }
            } catch (Exception ignored) {
            } finally {
                try { phaser.arriveAndDeregister(); 
                } catch (IllegalStateException ignored) {}
            }
        }
    }

    static class Consumator implements Runnable {
        final Depozit depozit;
        final Phaser phaser;
        final String name;

        Consumator(Depozit depozit, Phaser phaser, String name) {
            this.depozit = depozit;
            this.phaser = phaser;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                while (!depozit.gata) {
                    int faza = phaser.getPhase();
                    if (faza % 2 == 1) { // DRAIN
                        while (!depozit.depozitGol() && !depozit.gata) {
                            boolean consumat = depozit.consuma(name);
                            if (!consumat) Thread.sleep(50);
                        }
                    }
                    phaser.arriveAndAwaitAdvance();
                }
            } catch (Exception ignored) {
            } finally {
                try { phaser.arriveAndDeregister(); } catch (IllegalStateException ignored) {}
            }
        }
    }
}
