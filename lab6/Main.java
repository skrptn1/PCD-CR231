package lab6;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Store {
    ArrayList<Integer> stock = new ArrayList<>();
    int totalProduced = 0;
    int totalConsumed = 0;

    public synchronized void produce(String name) {
        while (stock.size() > 0) {
            try {
                System.out.println(name + " asteapta: depozitul nu este gol");
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        // producatorii produc până se umple D
        while (stock.size() < Main.D && totalProduced < Main.Z) {
            int value = Main.IMAPRE[(int) (Math.random() * Main.IMAPRE.length)];
            stock.add(value);
            totalProduced++;
            System.out.println(name + " a produs: " + value);
        }

        printStatus();
        notifyAll();
    }

    public synchronized void consume(String name) {
        while (stock.size() < Main.D) {
            try {
                System.out.println(name + " asteapta: depozitul nu este plin");
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        // consumatorii consuma TOT
        while (!stock.isEmpty() && totalConsumed < Main.Z) {
            int val = stock.remove(stock.size() - 1);
            totalConsumed++;
            System.out.println(name + " a consumat: " + val);
        }

        printStatus();
        notifyAll();
    }

    private void printStatus() {
        if (stock.isEmpty())
            System.out.println("Depozitul este GOL.");
        else {
            System.out.print("Depozitul: [ ");
            for (int x : stock)
                System.out.print(x + " ");
            System.out.println("]");
        }
        System.out.println();
    }
}

class Producer implements Runnable {
    private final Store store;
    private final String name;

    Producer(Store s, String n) {
        store = s;
        name = n;
    }

    @Override
    public void run() {
        while (store.totalProduced < Main.Z) {
            store.produce(name);
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }
        System.out.println(name + " si-a terminat munca.");
    }
}

class Consumer implements Runnable {
    private final Store store;
    private final String name;

    Consumer(Store s, String n) {
        store = s;
        name = n;
    }

    @Override
    public void run() {
        while (store.totalConsumed < Main.Z) {
            store.consume(name);
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }
        System.out.println(name + " si-a terminat munca.");
    }
}

public class Main {

    public static final int X = 3; // producători
    public static final int Y = 4; // consumatori
    public static final int Z = 45; // total obiecte
    public static final int D = 5; // dimensiunea depozitului
    public static final int F = 2; // 2 obiecte pe producere
    public static final int[] IMAPRE = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };

    public static void main(String[] args) {

        Store store = new Store();
        ExecutorService pool = Executors.newFixedThreadPool(X + Y);

        for (int i = 0; i < X; i++)
            pool.submit(new Producer(store, "Producator_" + (i + 1)));

        for (int i = 0; i < Y; i++)
            pool.submit(new Consumer(store, "Consumator_" + (i + 1)));

        pool.shutdown();
    }
}
