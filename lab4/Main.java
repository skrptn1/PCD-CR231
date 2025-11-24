package lab4;

import java.util.ArrayList;

class Store {
    private final int CAPACITY = Main.D;
    ArrayList<Integer> stockList = new ArrayList<Integer>();

    public synchronized void get(String consumerName) {
        while (stockList.size() < 1) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        System.out.println(consumerName + " a luat din depozit: " +
                stockList.get(stockList.size() - 1));
        stockList.remove(stockList.size() - 1);
        printStockStatus();
        notifyAll();
    }

    public synchronized void put(String producerName, int a, int b) {

        while (stockList.size() + Main.F >= CAPACITY) {
            System.out.println("+++ Depozitul e plin (" + CAPACITY + "). " + producerName + " asteapta. +++");
            try {
                wait();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        stockList.add(a);
        stockList.add(b);
        System.out.println(producerName + " a pus in depozit doua numere: " + a + ", " + b);
        printStockStatus();
        notifyAll();
    }

    private void printStockStatus() {
        if (stockList.isEmpty()) {
            System.out.println("Depozitul este gol.");
        } else {
            System.out.print("Depozitul are " + stockList.size() + " numere -> ");
            for (int number : stockList) {
                System.out.print(number + " ");
            }
            System.out.println();
        }
    }
}

class Producer extends Thread {
    private Store s;

    public Producer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {
        int[] impare = new int[] { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
        while (true) {
            s.put(getName(), impare[(int) (Math.random() * 9)],
                    impare[(int) (Math.random() * 9)]);
        }
    }
}

class Consumer extends Thread {
    private Store s;
    private final int CONSUME_COUNT = Main.Z; // Z = 2 (obiecte per consumator)

    public Consumer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {

        for (int i = 0; i < CONSUME_COUNT; i++) {
            s.get(getName());
        }

        System.out.println(getName() + " a luat " + CONSUME_COUNT + " numere. Thread-ul a finalizat.");
    }
}

// Clasa Principala
public class Main {

    // Variabile statice publice pentru Varianta 2:
    public static final int X = 3; // Numarul de Producatori
    public static final int Y = 4; // Numarul de Consumatori
    public static final int Z = 2; // Obiecte pe care trebuie sa le consume fiecare Consumator
    public static final int D = 5; // Dimensiunea maxima a Depozitului (Buffer-ului)
    public static final int F = 2; // Obiecte produse de fiecare Producator la o singura apelare put()

    // Obiectivul total de consum: Y * Z = 8
    public static final int MAX_CONSUMPTION = Y * Z;

    public static void main(String[] args) throws InterruptedException {
        Store store = new Store();

        // Initializare Producatori (X=3)
        Producer p1 = new Producer(store);
        p1.setName("Producator 1");
        Producer p2 = new Producer(store);
        p2.setName("Producator 2");
        Producer p3 = new Producer(store);
        p3.setName("Producator 3");

        // Initializare Consumatori (Y=4)
        Consumer c1 = new Consumer(store);
        c1.setName("Consumator 1");
        Consumer c2 = new Consumer(store);
        c2.setName("Consumator 2");
        Consumer c3 = new Consumer(store);
        c3.setName("Consumator 3");
        Consumer c4 = new Consumer(store);
        c4.setName("Consumator 4");

        p1.start();
        p2.start();
        p3.start();
        c1.start();
        c2.start();
        c3.start();
        c4.start();
        while (c1.isAlive() || c2.isAlive() || c3.isAlive() || c4.isAlive()) {
        }
        System.out.println("\nToate thread-urile au finalizat.");
    }
}