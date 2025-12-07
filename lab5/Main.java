package lab5;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Store {
    ArrayList<Integer> stockList = new ArrayList<>();
    int total_consumat = 0;

    public synchronized void get(String consumerName) {
        while (stockList.isEmpty()) {
            try {
                wait();
            } catch (Exception ignored) {
            }
        }

        int val = stockList.remove(stockList.size() - 1);
        total_consumat++;
        System.out.println(consumerName + " a consumat: " + val);

        printStockStatus();
        notifyAll();
    }

    public synchronized void put(String producerName, int a, int b) {

        if (total_consumat >= Main.CONSUM_MAXIM_TOTAL) {
            return;
        }

        while (stockList.size() + Main.OB_MAX_PE_PRODUCATOR > Main.MAX_DEPOZIT) {
            System.out.println(producerName + " încearcă să pună, dar depozitul este plin!");
            try {
                wait();
            } catch (Exception ignored) {
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
            for (int x : stockList)
                System.out.print(x + " ");
            System.out.println("]");
        }
        System.out.println();
    }
}

class Producer implements Runnable {
    private final Store store;
    private final String name;

    public Producer(Store store, String name) {
        this.store = store;
        this.name = name;
    }

    @Override
    public void run() {
        int[] impare = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
        while (Main.running) {
            int a = impare[(int) (Math.random() * impare.length)];
            int b = impare[(int) (Math.random() * impare.length)];

            store.put(name, a, b);

            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }
    }
}

class Consumer implements Runnable {
    private final Store store;
    private final String name;

    public Consumer(Store store, String name) {
        this.store = store;
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.OB_MAX_PE_CONSUMATOR; i++) {
            store.get(name);
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }

        System.out.println(name + " a finalizat consumul.");
    }
}

public class Main {

    public static final int NR_PROD = 3; // X
    public static final int NR_CONS = 4; // Y
    public static final int OB_MAX_PE_CONSUMATOR = 2; // Z
    public static final int MAX_DEPOZIT = 5; // D
    public static final int OB_MAX_PE_PRODUCATOR = 2; // F
    public static final int CONSUM_MAXIM_TOTAL = NR_CONS * OB_MAX_PE_CONSUMATOR;

    public static volatile boolean running = true;

    public static void main(String[] args) {

        Store store = new Store();

        ExecutorService pool = Executors.newFixedThreadPool(NR_PROD + NR_CONS);

        // pornește producători
        for (int i = 0; i < NR_PROD; i++) {
            pool.submit(new Producer(store, "Producator_" + (i + 1)));
        }

        // pornește consumatori
        for (int i = 0; i < NR_CONS; i++) {
            pool.submit(new Consumer(store, "Consumator_" + (i + 1)));
        }

        // așteptăm finalizarea consumatorilor
        while (store.total_consumat < CONSUM_MAXIM_TOTAL) {
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }

        running = false; // oprim producătorii

        pool.shutdownNow();

        System.out.println("\n=== Toate thread-urile au finalizat ===");
    }
}