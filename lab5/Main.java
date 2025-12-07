package lab5;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Store {
    ArrayList<Integer> stockList = new ArrayList<Integer>();
    int total_consumat = 0;

    public synchronized void get(String consumerName) {
        while (stockList.size() < 1) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        System.out.println(consumerName + " a consumat: " +
                stockList.get(stockList.size() - 1));
        stockList.remove(stockList.size() - 1);
        total_consumat++;
        printStockStatus();
        notifyAll();
    }

    public synchronized void put(String producerName, int a, int b) {
        if (total_consumat >= Main.CONSUM_MAXIM_TOTAL) {
            throw new RuntimeException("A fost consumat numarul necesar");
        }
        while (stockList.size() + Main.OB_MAX_PE_PRODUCATOR >= Main.MAX_DEPOZIT) {
            System.out.println(producerName + " incearca sa puna, dar nu incape in depozit");
            try {
                wait();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        stockList.add(a);
        stockList.add(b);
        System.out.println(producerName + " a produs: " + a + ", " + b);
        printStockStatus();
        notifyAll();
    }

    private void printStockStatus() {
        if (stockList.isEmpty()) {
            System.out.println("Depozitul este gol.");
        } else {
            System.out.print("[ ");
            for (int number : stockList) {
                System.out.print(number + " ");
            }
            System.out.print("]");
            System.out.println();
        }
        System.out.println();
    }
}

class Producer implements Runnable {
    private Store s;
    private String name;

    public Producer(Store s, String name) {
        this.s = s;
        this.name = name;
    }

    @Override
    public void run() {
        int[] impare = new int[] { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
        while (true) {
            try {
                s.put(name, impare[(int) (Math.random() * 9)],
                        impare[(int) (Math.random() * 9)]);
                Thread.sleep(100);
            } catch (Exception e) {
                break;
            }
        }
    }
}

class Consumer implements Runnable {
    private Store s;
    private String name;

    public Consumer(Store s, String name) {
        this.s = s;
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.OB_MAX_PE_CONSUMATOR; i++) {
            s.get(name);
            if (i == Main.OB_MAX_PE_CONSUMATOR - 1) {
                System.out.println(name + " a finalizat.");
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    }
}

public class Main {
    public static final int NR_PROD = 3;
    public static final int NR_CONS = 4;
    public static final int OB_MAX_PE_CONSUMATOR = 2;
    public static final int MAX_DEPOZIT = 5;
    public static final int OB_MAX_PE_PRODUCATOR = 2;
    public static final int CONSUM_MAXIM_TOTAL = NR_CONS * OB_MAX_PE_CONSUMATOR;

    public static void main(String[] args) throws InterruptedException {
        Store store = new Store();

        ExecutorService pool = Executors.newFixedThreadPool(NR_PROD + NR_CONS);

        for (int i = 0; i < NR_PROD; i++) {
            pool.execute(new Producer(store, "Producator_" + (i + 1)));
        }
        for (int i = 0; i < NR_CONS; i++) {
            pool.execute(new Consumer(store, "Consumator_" + (i + 1)));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            Thread.sleep(50);
        }

        System.out.println("\nToate thread-urile au finalizat.");
    }
}
