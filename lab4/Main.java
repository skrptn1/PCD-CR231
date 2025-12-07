package lab4;

import java.util.ArrayList;

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

class Producer extends Thread {
    private Store s;

    public Producer(Store s) {
        this.s = s;
    }

    // test
    @Override
    public void run() {
        int[] impare = new int[] { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
        while (true) {
            try {
                s.put(getName(), impare[(int) (Math.random() * 9)],
                        impare[(int) (Math.random() * 9)]);
                sleep(100);
            } catch (Exception e) {
                break;
            }
        }
    }
}

class Consumer extends Thread {
    private Store s;

    public Consumer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {

        for (int i = 0; i < Main.OB_MAX_PE_CONSUMATOR; i++) {
            s.get(getName());
            if (i == Main.OB_MAX_PE_CONSUMATOR - 1) {
                System.out.println(
                        getName() + " a finalizat.");
            }
            try {
                sleep(100);
            } catch (Exception e) {
                // TODO: handle exception
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
        Producer[] producers = new Producer[NR_PROD];
        Consumer[] consumers = new Consumer[NR_CONS];
        for (int i = 0; i < NR_PROD; i++) {
            producers[i] = new Producer(store);
            producers[i].setName("Producator_" + (i + 1));
        }
        for (int i = 0; i < NR_CONS; i++) {
            consumers[i] = new Consumer(store);
            consumers[i].setName("Consumator_" + (i + 1));
        }
        for (Producer p : producers) {
            p.start();
        }
        for (Consumer c : consumers) {
            c.start();
        }

        for (Producer p : producers) {
            p.join();
        }
        for (Consumer c : consumers) {
            c.join();
        }
        System.out.println("\nToate thread-urile au finalizat.");
        // Finalizarea programului
    }
}
