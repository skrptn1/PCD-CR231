import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Lab4_ProducatorConsumator {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lab 4 - Producator / Consumator (GUI)");
            frame.setSize(900, 650);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane sp = new JScrollPane(textArea);

            frame.setContentPane(sp);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Log.setTextArea(textArea);

            new Thread(() -> {
                try {
                    runSimulation();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.println("Simularea a fost intrerupta.");
                }
            }, "Controller").start();
        });
    }

    private static void runSimulation() throws InterruptedException {

        final int X = 2;
        final int Y = 5;
        final int Z = 3;
        final int D = 12;

        Store store = new Store(D);

        Producer p1 = new Producer(store);
        p1.setDaemon(false);
        p1.setName("Producator #1");

        Producer p2 = new Producer(store);
        p2.setDaemon(false);
        p2.setName("Producator #2");

        Consumer c1 = new Consumer(store, Z); c1.setName("Consumator #1");
        Consumer c2 = new Consumer(store, Z); c2.setName("Consumator #2");
        Consumer c3 = new Consumer(store, Z); c3.setName("Consumator #3");
        Consumer c4 = new Consumer(store, Z); c4.setName("Consumator #4");
        Consumer c5 = new Consumer(store, Z); c5.setName("Consumator #5");

        Log.println("Aplicatia a pornit.");
        Log.println("Parametri: X=" + X + " producatori, Y=" + Y + " consumatori, Z=" + Z
                + " (per consumator), D=" + D + " (capacitate)\n");

        p1.start();
        p2.start();

        c1.start(); c2.start(); c3.start(); c4.start(); c5.start();

        c1.join(); c2.join(); c3.join(); c4.join(); c5.join();

        Log.println("\n==============================");
        Log.println("Toti consumatorii au fost indestulati cu " + Z + " obiecte.");

        store.enableStopWhenFull();

        p1.join();
        p2.join();

        Log.println("Depozitul este plin. Producatorii s-au oprit.");
        Log.println("Programul se incheie.");
    }
}

class Log {
    private static final Object LOCK = new Object();
    private static JTextArea area;

    public static void setTextArea(JTextArea ta) {
        area = ta;
    }

    public static void println(String msg) {
        synchronized (LOCK) {
            System.out.println(msg);
            if (area != null) {
                SwingUtilities.invokeLater(() -> {
                    area.append(msg + "\n");
                    area.setCaretPosition(area.getDocument().getLength());
                });
            }
        }
    }
}

class Store {

    private final ArrayList<Integer> stockList = new ArrayList<>();
    private final int capacity;

    private boolean stopWhenFull = false;
    private boolean stopped = false;

    public Store(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void enableStopWhenFull() {
        stopWhenFull = true;
        if (stockList.size() >= capacity) {
            stopped = true;
        }
        notifyAll();
    }

    public synchronized boolean isStopped() {
        return stopped;
    }

    public synchronized int get(String consumerName) {
        while (stockList.isEmpty()) {
            if (stopped) return -1;

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

    public synchronized boolean putTwo(String producerName, int v1, int v2) {

        for (;;) {
            if (stopped) return false;

            int free = capacity - stockList.size();

            if (stopWhenFull) {
                if (free == 0) {
                    stopped = true;
                    notifyAll();
                    Log.println("STOP: Depozitul este plin (" + snapshot() + ")");
                    return false;
                }
                if (free == 1) {
                    stockList.add(v1);
                    stopped = true;
                    Log.println(producerName + " a pus 1 obiect (final fill): [" + v1 + "] | depozit=" + snapshot());
                    Log.println("STOP: Depozitul a ajuns plin (" + snapshot() + ")");
                    notifyAll();
                    return false;
                }

                stockList.add(v1);
                stockList.add(v2);
                Log.println(producerName + " a pus 2 obiecte: [" + v1 + ", " + v2 + "] | depozit=" + snapshot());

                if (stockList.size() >= capacity) {
                    stopped = true;
                    Log.println("STOP: Depozitul a ajuns plin (" + snapshot() + ")");
                    notifyAll();
                    return false;
                }

                notifyAll();
                return true;
            }

            while (stockList.size() > capacity - 2) {
                Log.println(producerName + ": nu e loc pentru 2 obiecte, astept... | depozit=" + snapshot());
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                if (stopped) return false;
            }

            stockList.add(v1);
            stockList.add(v2);
            Log.println(producerName + " a pus 2 obiecte: [" + v1 + ", " + v2 + "] | depozit=" + snapshot());
            notifyAll();
            return true;
        }
    }

    private String snapshot() {
        return stockList.size() + "/" + capacity + " " + stockList;
    }
}

class Producer extends Thread {

    private final Store store;

    public Producer(Store store) {
        this.store = store;
    }

    @Override
    public void run() {
        int[] pare = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20,
                22, 24, 26, 28, 30, 32, 34, 36, 38, 40};

        while (!store.isStopped()) {

            int v1 = pare[ThreadLocalRandom.current().nextInt(pare.length)];
            int v2 = pare[ThreadLocalRandom.current().nextInt(pare.length)];

            boolean ok = store.putTwo(getName(), v1, v2);
            if (!ok) break;

            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Log.println(getName() + ": STOP (producer finished).");
    }
}

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
            if (value == -1) return;
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
