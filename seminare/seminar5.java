package seminare;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class seminar5 {

    static final int PRODUCATORI = 3;
    static final int CONSUMATORI = 5;
    static final int CAPACITATE = 6;
    static final int TOTALCONSUMAT = 58;

   public static void main(String[] args) {

    Depozit depozit = new Depozit();
    Phaser phaser = new Phaser(1); 

    Thread[] producatori = new Thread[PRODUCATORI];
    Thread[] consumatori = new Thread[CONSUMATORI];

    for (int i = 0; i < PRODUCATORI; i++) {
        phaser.register();
        producatori[i] = new Producator(depozit, phaser,  "Producator-" + (i + 1));
        producatori[i].start();
    }

    for (int i = 0; i < CONSUMATORI; i++) {
        phaser.register(); 
        consumatori[i] = new Consumator(depozit, phaser,   "Consumator-" + (i + 1));
        consumatori[i].start();
    }
    phaser.arriveAndDeregister();
}


    static class Depozit {
        final Deque<Integer> depozit = new ArrayDeque<>(CAPACITATE);
        final ReentrantLock lock = new ReentrantLock(true);

        int prodcount = 0;
        int conscount = 0;
        Random random = new Random();
        volatile boolean gata = false;

        void notFull() {
            System.out.println("Depozitul este plin, producatorul asteapta.");
        }

        void notEmpty() {
            System.out.println("Depozitul este gol, consumatorul asteapta.");
        }

        boolean Get(String name) {
            lock.lock();
            try {
                if (depozit.size() == CAPACITATE) {
                    notFull();
                    return false;
                }

                int number = random.nextInt(100, 201);
                depozit.addLast(number);
                prodcount++;

                System.out.println(name + " a produs: " + number +
                        " | Stare depozit: " + depozit);

                if (depozit.size() == CAPACITATE)
                    notFull();

                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean Put(String name) {
            lock.lock();
            try {
                if (depozit.isEmpty()) {
                    notEmpty();
                    return false;
                }

                int number = depozit.removeFirst();
                conscount++;

                System.out.println(name + " a consumat: " + number +
                        " | Stare depozit: " + depozit);

                if (conscount >= TOTALCONSUMAT && depozit.isEmpty())
                    gata = true;

                return true;
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
                while (!depozit.gata) {

                    int faza = phaser.getPhase();

                    
                    if (faza % 2 == 0) {
                        boolean produs = depozit.Get(getName());

                        if (!produs) {
                            Thread.sleep(50);
                        }
                    }

                    phaser.arriveAndAwaitAdvance();
                }
            } catch (Exception e) {
                System.out.println("Eroare la producator: " + e.getMessage());
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
                while (!depozit.gata) {
                    int faza = phaser.getPhase();
                    if (faza % 2 == 1) {
                        boolean consumat = depozit.Put(getName());
                        if (!consumat) {
                        }
                    }
                    else {
                    }
                    phaser.arriveAndAwaitAdvance();
                }
            } catch (Exception e) {
                System.out.println("Eroare la consumator: " + e.getMessage());
            }
        }
    }
}

