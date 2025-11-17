package lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Clasa Resursa Comuna (Depozitul)
class Store {

    // Variabilele preluate din clasa Main (public static final)
    private final int CAPACITY = Main.D;
    private final int MAX_CONSUMPTION = Main.MAX_CONSUMPTION;

    private int totalConsumed = 0;
    private List<Integer> stockList = new ArrayList<>();

    public synchronized boolean isFinished() {
        return totalConsumed >= MAX_CONSUMPTION;
    }

    // Metoda get() - pentru Consumator
    public synchronized int get(String consumerName) throws InterruptedException {
        // Iesire rapida daca s-a terminat consumul total (Y*Z)
        if (isFinished()) {
            notifyAll();
            return -1;
        }

        // Asteapta pana cand depozitul nu este gol SAU s-a terminat consumul
        while (stockList.isEmpty() && !isFinished()) {
            System.out.println("--- Depozitul este gol. " + consumerName + " asteapta. ---");
            wait();
        }

        // Re-verifica dupa ce a fost trezit
        if (isFinished()) {
            notifyAll();
            return -1;
        }

        // Extrage ultimul element
        int value = stockList.remove(stockList.size() - 1);
        totalConsumed++;

        System.out.println(consumerName + " a luat din depozit: " + value);

        printStockStatus();

        // Notifica Producatorii (depozitul ar putea avea loc acum) si alti Consumatori
        notifyAll();

        return value;
    }

    // Metoda put() - pentru Producator
    public synchronized void put(String producerName, int a, int b) throws InterruptedException {
        // Nu mai produce daca obiectivul de consum a fost atins
        if (isFinished()) {
            notifyAll();
            return;
        }

        // Asteapta pana cand depozitul nu este plin (se verifica daca pot fi adaugate
        // cele F obiecte)
        while (stockList.size() + Main.F > CAPACITY) {
            System.out.println("+++ Depozitul e plin (" + CAPACITY + "). " + producerName + " asteapta. +++");
            wait();
        }

        // Re-verifica dupa ce a fost trezit
        if (isFinished()) {
            notifyAll();
            return;
        }

        // Plaseaza Main.F obiecte (F=2)
        stockList.add(a);
        stockList.add(b);

        System.out.println(producerName + " a pus in depozit doua numere: " + a + ", " + b);

        printStockStatus();

        // Notifica Consumatorii blocati (ca s-au adaugat elemente)
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
            System.out.println("\n(Consumate total: " + totalConsumed + ")");
        }
    }
}

// Clasa Producator (X=3)
class Producer extends Thread {
    private Store s;
    private Random random = new Random();
    // Numere impare (Tip Obiecte)
    private final int[] ODD_NUMBERS = new int[] { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };

    public Producer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            while (!s.isFinished()) {
                // Genereaza Main.F (2) numere impare
                int num1 = ODD_NUMBERS[random.nextInt(ODD_NUMBERS.length)];
                int num2 = ODD_NUMBERS[random.nextInt(ODD_NUMBERS.length)];

                s.put(getName(), num1, num2);

                Thread.sleep(random.nextInt(100) + 50);
            }
        } catch (InterruptedException e) {
            // Se iese din bucla la intrerupere (la finalul programului)
        }
        System.out.println(getName() + " a finalizat productia.");
    }
}

// Clasa Consumator (Y=4)
class Consumer extends Thread {
    private Store s;
    private final int CONSUME_COUNT = Main.Z; // Z = 2 (obiecte per consumator)
    private int consumed = 0;

    public Consumer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            while (consumed < CONSUME_COUNT) {
                int value = s.get(getName());

                // Verifica semnalul de terminare (-1)
                if (value == -1) {
                    break;
                }

                consumed++;

                Thread.sleep(new Random().nextInt(150) + 50);
            }
        } catch (InterruptedException e) {
            // Se iese din bucla la intrerupere
        }
        System.out.println(getName() + " a luat " + consumed + " numere. Thread-ul a finalizat.");
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

        // Asteapta finalizarea Consumatorilor
        c1.join();
        c2.join();
        c3.join();
        c4.join();

        // Opreste Producatorii blocati (folosind .interrupt())
        p1.interrupt();
        p2.interrupt();
        p3.interrupt();

        System.out.println("\nToate thread-urile au finalizat.");
    }
}