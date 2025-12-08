package lab6;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class App extends Application {

    // ---------- Logger simplu pentru JavaFX ----------
    public static class FXLogger {
        public static TextArea area;

        public static void log(String msg) {
            System.out.println(msg);
            if (area != null) {
                Platform.runLater(() -> area.appendText(msg + "\n"));
            }
        }
    }

    // ---------- STORE ----------
    static class Store {

        ArrayList<Integer> stock = new ArrayList<>();
        int totalProduced = 0;
        int totalConsumed = 0;

        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public void produce(String name) {
            lock.lock();
            try {
                while (stock.size() > 0) {
                    FXLogger.log(name + " asteapta: depozitul NU este gol");
                    notFull.await();
                }

                while (stock.size() < Main.D && totalProduced < Main.Z) {
                    int value = Main.IMAPRE[(int) (Math.random() * Main.IMAPRE.length)];
                    stock.add(value);
                    totalProduced++;
                    FXLogger.log(name + " a produs: " + value);
                }

                printStatus();
                notEmpty.signalAll();

            } catch (Exception ignored) {
            } finally {
                lock.unlock();
            }
        }

        public void consume(String name) {
            lock.lock();
            try {
                while (stock.size() < Main.D) {
                    FXLogger.log(name + " asteapta: depozitul NU este plin");
                    notEmpty.await();
                }

                while (!stock.isEmpty() && totalConsumed < Main.Z) {
                    int val = stock.remove(stock.size() - 1);
                    totalConsumed++;
                    FXLogger.log(name + " a consumat: " + val);
                }

                printStatus();
                notFull.signalAll();

            } catch (Exception ignored) {
            } finally {
                lock.unlock();
            }
        }

        private void printStatus() {
            if (stock.isEmpty())
                FXLogger.log("Depozitul este GOL.");
            else {
                StringBuilder sb = new StringBuilder("Depozitul: [ ");
                for (int x : stock)
                    sb.append(x).append(" ");
                sb.append("]");
                FXLogger.log(sb.toString());
            }
            FXLogger.log("");
        }
    }

    // ---------- PRODUCER ----------
    static class Producer implements Runnable {
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
            FXLogger.log(name + " si-a terminat munca.");
        }
    }

    // ---------- CONSUMER ----------
    static class Consumer implements Runnable {
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
            FXLogger.log(name + " si-a terminat munca.");
        }
    }

    // ---------- CONSTANTE ----------
    public static class Main {
        public static final int X = 3;
        public static final int Y = 4;
        public static final int Z = 45;
        public static final int D = 5;
        public static final int F = 2;
        public static final int[] IMAPRE = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
    }

    // ---------- JAVAFX ----------
    @Override
    public void start(Stage stage) {

        TextArea area = new TextArea();
        area.setEditable(false);
        FXLogger.area = area;

        BorderPane root = new BorderPane(area);
        Scene scene = new Scene(root, 700, 500);

        stage.setTitle("Rezultate LAB 6 — Producători / Consumatori");
        stage.setScene(scene);
        stage.show();

        startThreads();
    }

    private void startThreads() {
        Store store = new Store();
        ExecutorService pool = Executors.newFixedThreadPool(Main.X + Main.Y);

        for (int i = 0; i < Main.X; i++)
            pool.submit(new Producer(store, "Producator_" + (i + 1)));

        for (int i = 0; i < Main.Y; i++)
            pool.submit(new Consumer(store, "Consumator_" + (i + 1)));

        pool.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
