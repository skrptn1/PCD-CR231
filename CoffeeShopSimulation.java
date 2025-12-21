import javax.swing.*;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CoffeeShopSimulation {

    // ===== UI (Swing) =====
    private static JTextArea area;

    private static void log(String msg) {
        String line = "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + msg + "\n";
        if (area == null) {
            System.out.print(line);
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            area.append(line);
            area.setCaretPosition(area.getDocument().getLength());
        } else {
            SwingUtilities.invokeLater(() -> {
                area.append(line);
                area.setCaretPosition(area.getDocument().getLength());
            });
        }
    }

    // ===== Menu (Java 8) =====
    private static final List<String> MENU = Collections.unmodifiableList(
            Arrays.asList("Espresso", "Cappuccino", "Latte", "Americano", "Flat White")
    );

    // ===== Model =====
    static class Order {
        final int id;
        final String drink;
        final long createdAtMs;

        Order(int id, String drink) {
            this.id = id;
            this.drink = drink;
            this.createdAtMs = System.currentTimeMillis();
        }
    }

    // ===== Shared concurrency primitives =====
    static class Inventory {
        private int beansGrams;

        Inventory(int initialBeansGrams) {
            this.beansGrams = initialBeansGrams;
        }

        // synchronized + wait/notifyAll
        public synchronized void takeBeans(int grams, String who) throws InterruptedException {
            while (beansGrams < grams) {
                log(who + " -> stoc boabe insuficient (" + beansGrams + "g). Aștept restock...");
                wait();
            }
            beansGrams -= grams;
            log(who + " -> ia " + grams + "g boabe. Stoc rămas: " + beansGrams + "g");
        }

        public synchronized void restock(int grams, String who) {
            beansGrams += grams;
            log(who + " -> RESTOCK +" + grams + "g. Stoc: " + beansGrams + "g");
            notifyAll();
        }

        public synchronized int snapshot() {
            return beansGrams;
        }
    }

    // ReentrantLock + Condition (mese limitate)
    static class SeatingArea {
        private final int totalTables;
        private int freeTables;

        private final ReentrantLock lock = new ReentrantLock(true);
        private final Condition tableAvailable = lock.newCondition();

        SeatingArea(int totalTables) {
            this.totalTables = totalTables;
            this.freeTables = totalTables;
        }

        public void takeTable(String customer) throws InterruptedException {
            lock.lock();
            try {
                while (freeTables == 0) {
                    log(customer + " -> nu sunt mese libere, aștept...");
                    tableAvailable.await();
                }
                freeTables--;
                log(customer + " -> a ocupat o masă. Mese libere: " + freeTables + "/" + totalTables);
            } finally {
                lock.unlock();
            }
        }

        public void releaseTable(String customer) {
            lock.lock();
            try {
                freeTables++;
                log(customer + " -> a eliberat masa. Mese libere: " + freeTables + "/" + totalTables);
                tableAvailable.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    // Wrapper simplu pentru flag thread-safe
    static class VolatileFlag {
        private volatile boolean v;
        VolatileFlag(boolean initial) { this.v = initial; }
        boolean get() { return v; }
        void set(boolean nv) { this.v = nv; }
    }

    public static void main(String[] args) throws Exception {
        // UI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Coffee Shop Simulation - Threads Demo (Java 8)");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(980, 650);
            area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 13));
            frame.add(new JScrollPane(area));
            frame.setVisible(true);
        });

        Thread.sleep(300);

        // ===== Shared objects =====
        BlockingQueue<Order> ordersQueue = new LinkedBlockingQueue<Order>(50); // BlockingQueue
        Inventory inventory = new Inventory(200);
        SeatingArea seating = new SeatingArea(5);

        Semaphore cashierStations = new Semaphore(2, true);
        Semaphore espressoMachines = new Semaphore(3, true);

        CountDownLatch openLatch = new CountDownLatch(1);
        AtomicInteger orderIdGen = new AtomicInteger(0);
        AtomicInteger ordersCompleted = new AtomicInteger(0);

        ConcurrentHashMap<String, AtomicInteger> drinkStats = new ConcurrentHashMap<String, AtomicInteger>();
        for (String d : MENU) drinkStats.put(d, new AtomicInteger(0));

        Phaser closingPhaser = new Phaser(1); // manager (main)
        VolatileFlag closingFlag = new VolatileFlag(false);

        // ===== Workers =====
        List<Thread> workers = new ArrayList<Thread>();

        // Cashier thread
        Thread cashier = new Thread(() -> {
            String name = Thread.currentThread().getName();
            closingPhaser.register();
            try {
                openLatch.await();
                while (!closingFlag.get()) {
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> stop service (phaser phase 1)");
                closingPhaser.arriveAndAwaitAdvance(); // phase 1
                cleanupSleep(name);
                closingPhaser.arriveAndAwaitAdvance(); // phase 2
                closingPhaser.arriveAndDeregister();
                log(name + " -> terminat.");
            }
        }, "Cashier");
        workers.add(cashier);

        // Baristas
        int BARISTAS = 3;
        for (int i = 1; i <= BARISTAS; i++) {
            Thread barista = new Thread(new Barista(
                    "Barista-" + i,
                    openLatch,
                    closingFlag,
                    closingPhaser,
                    ordersQueue,
                    espressoMachines,
                    inventory,
                    drinkStats,
                    ordersCompleted
            ), "Barista-" + i);
            workers.add(barista);
        }

        // Restocker
        Thread restocker = new Thread(() -> {
            String name = Thread.currentThread().getName();
            closingPhaser.register();
            try {
                openLatch.await();
                Random rnd = new Random();
                while (!closingFlag.get()) {
                    Thread.sleep(800 + rnd.nextInt(900));
                    int current = inventory.snapshot();
                    if (current < 80) inventory.restock(200, name);
                    else inventory.restock(80, name);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> stop service (phaser phase 1)");
                closingPhaser.arriveAndAwaitAdvance();
                cleanupSleep(name);
                closingPhaser.arriveAndAwaitAdvance();
                closingPhaser.arriveAndDeregister();
                log(name + " -> terminat.");
            }
        }, "Restocker");
        workers.add(restocker);

        for (Thread t : workers) t.start();

        // ===== ThreadPoolExecutor for customers =====
        ThreadPoolExecutor customerPool = new ThreadPoolExecutor(
                4, 8,
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(20),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        Thread customerSupervisor = new Thread(() -> {
            String name = Thread.currentThread().getName();
            Random rnd = new Random();
            try {
                openLatch.await();

                int CUSTOMERS = 25;
                for (int i = 1; i <= CUSTOMERS; i++) {
                    final int customerNo = i;
                    customerPool.submit(() -> {
                        String customerName = "Customer-" + customerNo;
                        try {
                            seating.takeTable(customerName);

                            cashierStations.acquire();
                            try {
                                Thread.sleep(150 + rnd.nextInt(300));
                                String drink = MENU.get(rnd.nextInt(MENU.size()));
                                int id = orderIdGen.incrementAndGet();

                                Order o = new Order(id, drink);
                                ordersQueue.put(o);

                                log(customerName + " -> a comandat #" + id + " " + drink +
                                        " (coada=" + ordersQueue.size() + ")");
                            } finally {
                                cashierStations.release();
                            }

                            Thread.sleep(250 + rnd.nextInt(350));
                            seating.releaseTable(customerName);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    Thread.sleep(120 + rnd.nextInt(180));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> nu mai generează clienți. shutdown pool...");
                customerPool.shutdown();
            }
        }, "CustomerSupervisor");

        customerSupervisor.start();

        // ===== Open shop =====
        log("MANAGER -> pregătiri... DESCHIDEM cafeneaua!");
        openLatch.countDown();

        // join
        customerSupervisor.join();
        customerPool.awaitTermination(10, TimeUnit.SECONDS);

        Thread.sleep(1200);

        // ===== Closing =====
        log("MANAGER -> ÎNCHIDERE: stop + interrupt + faze (phaser).");
        closingFlag.set(true);
        for (Thread t : workers) t.interrupt();

        log("MANAGER -> aștept faza 1 (stop-service)...");
        closingPhaser.arriveAndAwaitAdvance();

        log("MANAGER -> aștept faza 2 (cleanup)...");
        closingPhaser.arriveAndAwaitAdvance();

        // ===== Callable + Future report =====
        ExecutorService reportExec = Executors.newFixedThreadPool(2);

        Future<String> topDrinksFuture = reportExec.submit(new Callable<String>() {
            @Override public String call() {
                return buildTopDrinksReport(drinkStats);
            }
        });

        Future<String> totalsFuture = reportExec.submit(new Callable<String>() {
            @Override public String call() {
                return "Total comenzi finalizate: " + ordersCompleted.get()
                        + "\nStoc boabe rămas: " + inventory.snapshot() + "g"
                        + "\nComenzi rămase în coadă: " + ordersQueue.size();
            }
        });

        String report = "\n===== RAPORT FINAL =====\n"
                + totalsFuture.get() + "\n\n"
                + topDrinksFuture.get()
                + "\n=======================\n";

        reportExec.shutdown();

        log(report);

        for (Thread t : workers) t.join();
        log("MANAGER -> gata. Program terminat.");
    }

    static class Barista implements Runnable {
        private final String name;
        private final CountDownLatch openLatch;
        private final VolatileFlag closing;
        private final Phaser phaser;

        private final BlockingQueue<Order> queue;
        private final Semaphore machines;
        private final Inventory inventory;
        private final ConcurrentHashMap<String, AtomicInteger> stats;
        private final AtomicInteger completed;

        Barista(String name,
                CountDownLatch openLatch,
                VolatileFlag closing,
                Phaser phaser,
                BlockingQueue<Order> queue,
                Semaphore machines,
                Inventory inventory,
                ConcurrentHashMap<String, AtomicInteger> stats,
                AtomicInteger completed) {
            this.name = name;
            this.openLatch = openLatch;
            this.closing = closing;
            this.phaser = phaser;
            this.queue = queue;
            this.machines = machines;
            this.inventory = inventory;
            this.stats = stats;
            this.completed = completed;

            phaser.register();
        }

        @Override
        public void run() {
            Random rnd = new Random();
            try {
                openLatch.await();

                while (!closing.get() || !queue.isEmpty()) {
                    Order o = queue.poll(250, TimeUnit.MILLISECONDS);
                    if (o == null) continue;

                    machines.acquire();
                    try {
                        inventory.takeBeans(12, name);
                        Thread.sleep(250 + rnd.nextInt(450));

                        stats.get(o.drink).incrementAndGet();
                        int done = completed.incrementAndGet();

                        long latency = System.currentTimeMillis() - o.createdAtMs;
                        log(name + " -> a făcut #" + o.id + " " + o.drink
                                + " | totalDone=" + done + " | t=" + latency + "ms");
                    } finally {
                        machines.release();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                log(name + " -> stop service (phaser phase 1)");
                phaser.arriveAndAwaitAdvance();
                cleanupSleep(name);
                phaser.arriveAndAwaitAdvance();
                phaser.arriveAndDeregister();
                log(name + " -> terminat.");
            }
        }
    }

    static void cleanupSleep(String who) {
        try {
            log(who + " -> cleanup...");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static String buildTopDrinksReport(ConcurrentHashMap<String, AtomicInteger> stats) {
        List<Map.Entry<String, AtomicInteger>> list = new ArrayList<Map.Entry<String, AtomicInteger>>(stats.entrySet());
        list.sort((a, b) -> Integer.compare(b.getValue().get(), a.getValue().get()));

        StringBuilder sb = new StringBuilder("Top băuturi:\n");
        int rank = 1;
        for (Map.Entry<String, AtomicInteger> e : list) {
            sb.append(rank++).append(") ").append(e.getKey()).append(" = ").append(e.getValue().get()).append("\n");
        }
        return sb.toString();
    }
}
