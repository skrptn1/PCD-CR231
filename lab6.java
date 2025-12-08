package seminare;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class seminar5 {

    static final int PRODUCATORI = 2;  // X = 2
    static final int CONSUMATORI = 3;   // Y = 3
    static final int CAPACITATE = 8;    // D = 8
    static final int TOTAL_OBIECTE = 40; // Z = 40

    public static void main(String[] args) {
        Depozit depozit = new Depozit();
        Phaser phaser = new Phaser(1);

        Thread[] producatori = new Thread[PRODUCATORI];
        Thread[] consumatori = new Thread[CONSUMATORI];

        // Crearea și pornirea producătorilor
        for (int i = 0; i < PRODUCATORI; i++) {
            phaser.register();
            producatori[i] = new Producator(depozit, phaser, "Producator-" + (i + 1));
            producatori[i].start();
        }

        // Crearea și pornirea consumatorilor
        for (int i = 0; i < CONSUMATORI; i++) {
            phaser.register();
            consumatori[i] = new Consumator(depozit, phaser, "Consumator-" + (i + 1));
            consumatori[i].start();
        }

        phaser.arriveAndDeregister();
    }

    static class Depozit {
        final Deque<Integer> depozit = new ArrayDeque<>(CAPACITATE);
        final ReentrantLock lock = new ReentrantLock(true);

        int totalProds = 0;
        int totalCons = 0;
        Random random = new Random();
        volatile boolean terminat = false;

        // Producerea unui obiect (număr par)
        boolean produce(String name) {
            lock.lock();
            try {
                // Verifică dacă depozitul este plin
                if (depozit.size() >= CAPACITATE) {
                    System.out.println(">>> Depozitul este PLIN! " + name + " așteaptă.");
                    return false;
                }

                // Verifică dacă s-au produs deja toate obiectele necesare
                if (totalProds >= TOTAL_OBIECTE) {
                    return false;
                }

                // Generează număr par (100-200)
                int numar = random.nextInt(51) * 2 + 100; // numere pare între 100-200
                depozit.addLast(numar);
                totalProds++;

                System.out.println(name + " a PRODUS: " + numar +
                        " | Dimensiune depozit: " + depozit.size() + "/" + CAPACITATE +
                        " | Total produse: " + totalProds + "/" + TOTAL_OBIECTE);

                // Mesaj când depozitul devine plin
                if (depozit.size() == CAPACITATE) {
                    System.out.println("*** DEPOZITUL ESTE PLIN! Consumatorii pot începe consumarea. ***");
                }

                return true;
            } finally {
                lock.unlock();
            }
        }

        // Consumarea unui obiect
        boolean consuma(String name) {
            lock.lock();
            try {
                // Verifică dacă depozitul este gol
                if (depozit.isEmpty()) {
                    System.out.println(">>> Depozitul este GOL! " + name + " așteaptă.");
                    return false;
                }

                int numar = depozit.removeFirst();
                totalCons++;

                System.out.println(name + " a CONSUMAT: " + numar +
                        " | Dimensiune depozit: " + depozit.size() + "/" + CAPACITATE +
                        " | Total consumate: " + totalCons + "/" + TOTAL_OBIECTE);

                // Mesaj când depozitul devine gol
                if (depozit.isEmpty()) {
                    System.out.println("*** DEPOZITUL ESTE GOL! Producătorii pot produce din nou. ***");
                }

                // Verifică dacă s-a terminat procesul
                if (totalCons >= TOTAL_OBIECTE) {
                    terminat = true;
                    System.out.println("\n=== PROCESUL S-A TERMINAT! ===");
                    System.out.println("Total produse: " + totalProds);
                    System.out.println("Total consumate: " + totalCons);
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

                    // Producătorii lucrează în fazele pare (0, 2, 4, ...)
                    // Producătorii produc doar când depozitul este gol
                    if (faza % 2 == 0) {
                        if (depozit.depozitulEsteGol() || depozit.depozit.size() < CAPACITATE) {
                            boolean produs = depozit.produce(getName());
                            if (!produs) {
                                Thread.sleep(10);
                            }
                        }
                    }

                    phaser.arriveAndAwaitAdvance();

                    if (depozit.esteTerminat()) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Eroare la " + getName() + ": " + e.getMessage());
            } finally {
                phaser.arriveAndDeregister();
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

                    // Consumatorii lucrează în fazele impare (1, 3, 5, ...)
                    // Consumatorii consumă doar când depozitul este plin
                    if (faza % 2 == 1) {
                        if (depozit.depozitulEstePlin() || !depozit.depozitulEsteGol()) {
                            boolean consumat = depozit.consuma(getName());
                            if (!consumat) {
                                Thread.sleep(10);
                            }
                        }
                    }

                    phaser.arriveAndAwaitAdvance();

                    if (depozit.esteTerminat()) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Eroare la " + getName() + ": " + e.getMessage());
            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }
}