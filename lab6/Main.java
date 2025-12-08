package lab6;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Store {

    ArrayList<Integer> stock = new ArrayList<>();
    int totalProduced = 0;
    int totalConsumed = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition(); // producătorii așteaptă aici
    private final Condition notEmpty = lock.newCondition(); // consumatorii așteaptă aici

    public void produce(String name) {
        lock.lock();
        try {

            while (stock.size() > 0) {
                System.out.println(name + " asteapta: depozitul NU este gol");
                notFull.await();
            }

            while (stock.size() < Main.D && totalProduced < Main.Z) {
                int value = Main.IMAPRE[(int) (Math.random() * Main.IMAPRE.length)];
                stock.add(value);
                totalProduced++;
                System.out.println(name + " a produs: " + value);
            }

            printStatus();

            notEmpty.signalAll();
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }

    public void consume(String name) {
        lock.lock();
        try {

            while (stock.size() < Main.D) {
                System.out.println(name + " asteapta: depozitul NU este plin");
                notEmpty.await();
            }

            while (!stock.isEmpty() && totalConsumed < Main.Z) {
                int val = stock.remove(stock.size() - 1);
                totalConsumed++;
                System.out.println(name + " a consumat: " + val);
            }

            printStatus();

            notFull.signalAll();
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
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

    public static final int X = 3;
    public static final int Y = 4;
    public static final int Z = 45;
    public static final int D = 5;
    public static final int F = 2;
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