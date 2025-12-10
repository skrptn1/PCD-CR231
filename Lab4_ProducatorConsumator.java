import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Lab4_ProducatorConsumator {

    public static void main(String[] args) throws InterruptedException {

        // Datele din enunt:
        final int X = 2;   // producatori
        final int Y = 5;   // consumatori
        final int Z = 3;   // obiecte pentru fiecare consumator
        final int D = 12;  // capacitatea depozitului

        Store store = new Store(D);

        // Producatori (X = 2)
        Producer p1 = new Producer(store);
        p1.setDaemon(true);
        p1.setName("Producator #1");

        Producer p2 = new Producer(store);
        p2.setDaemon(true);
        p2.setName("Producator #2");

        // Consumatori (Y = 5), fiecare ia Z obiecte (cate 1 pe operatie)
        Consumer c1 = new Consumer(store, Z); c1.setName("Consumator #1");
        Consumer c2 = new Consumer(store, Z); c2.setName("Consumator #2");
        Consumer c3 = new Consumer(store, Z); c3.setName("Consumator #3");
        Consumer c4 = new Consumer(store, Z); c4.setName("Consumator #4");
        Consumer c5 = new Consumer(store, Z); c5.setName("Consumator #5");

        // Pornim firele
        p1.start();
        p2.start();

        c1.start();
        c2.start();
        c3.start();
        c4.start();
        c5.start();

        // Asteptam pana termina toti consumatorii (fara busy-wait)
        c1.join();
        c2.join();
        c3.join();
        c4.join();
        c5.join();

        Log.println("\n==============================");
        Log.println("Toti consumatorii au fost indestulati cu " + Z + " obiecte.");
        Log.println("Programul se incheie.");
    }
}

// Logger sincronizat (nu se amesteca liniile intre thread-uri)
class Log {
    private static final Object LOCK = new Object();

    public static void println(String msg) {
        synchronized (LOCK) {
            System.out.println(msg);
        }
    }
}

// Depozitul partajat
class Store {

    private final ArrayList<Integer> stockList = new ArrayList<>();
    private final int capacity;

    public Store(int capacity) {
        this.capacity = capacity;
    }

    // Consumatorul ia 1 obiect
    public synchronized int get(String consumerName) {
        while (stockList.isEmpty()) {
            Log.println(consumerName + ": depozitul este gol, astept...");
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            }
        }

        int value = stockList.remove(stockList.size() - 1);

        Log.println(consumerName + " a luat din depozit: " + value + " | depozit=" + snapshot());

        notifyAll();
        return value;
    }

    // Producatorul pune 2 obiecte o data (asteapta pana are loc pentru 2)
    public synchronized void putTwo(String producerName, int v1, int v2) {
        while (stockList.size() > capacity - 2) { // NU e loc pentru 2
            Log.println(producerName + ": nu e loc pentru 2 obiecte (capacitate=" + capacity + "), astept... | depozit=" + snapshot());
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        stockList.add(v1);
        stockList.add(v2);

        Log.println(producerName + " a pus in depozit 2 obiecte: [" + v1 + ", " + v2 + "] | depozit=" + snapshot());

        notifyAll();
    }

    private String snapshot() {
        return stockList.size() + "/" + capacity + " " + stockList;
    }
}

// Producator – genereaza NUMERE PARE si le pune in depozit cate 2
class Producer extends Thread {

    private final Store store;

    public Producer(Store store) {
        this.store = store;
    }

    @Override
    public void run() {
        int[] pare = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20,
                               22, 24, 26, 28, 30, 32, 34, 36, 38, 40};

        while (true) {
            int v1 = pare[ThreadLocalRandom.current().nextInt(pare.length)];
            int v2 = pare[ThreadLocalRandom.current().nextInt(pare.length)];

            store.putTwo(getName(), v1, v2);

            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

// Consumator – ia Z obiecte (cate 1 pe operatie) si apoi se opreste
class Consumer extends Thread {

    private final Store store;
    private final int need;

    public Consumer(Store store, int need) {
        this.store = store;
        this.need = need;
    }

    @Override
    public void run() {
        int taken = 0;

        for (int i = 0; i < need; i++) {
            int value = store.get(getName());
            if (value == -1) return; // intrerupt
            taken++;
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Log.println(getName() + " a luat " + taken + " obiecte. Thread-ul a finalizat.");
    }
}
