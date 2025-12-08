package lab5;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    
    public static class FXLogger {
        public static TextArea area;

        public static void log(String msg) {
            System.out.println(msg); 

            if (area != null)
                Platform.runLater(() -> area.appendText(msg + "\n"));
        }
    }

    
    static class Store {
        ArrayList<Integer> stockList = new ArrayList<>();
        int total_consumat = 0;

        public synchronized void get(String consumerName) {
            while (stockList.size() < 1) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
            FXLogger.log(consumerName + " a consumat: " +
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
                FXLogger.log(producerName + " incearca sa puna, dar nu incape in depozit");
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }

            stockList.add(a);
            stockList.add(b);
            FXLogger.log(producerName + " a produs: " + a + ", " + b);

            printStockStatus();
            notifyAll();
        }

        private void printStockStatus() {
            if (stockList.isEmpty()) {
                FXLogger.log("Depozitul este gol.");
            } else {
                StringBuilder sb = new StringBuilder("[ ");
                for (int n : stockList)
                    sb.append(n).append(" ");
                sb.append("]");
                FXLogger.log(sb.toString());
            }
            FXLogger.log("");
        }
    }

    
    static class Producer implements Runnable {
        private Store s;
        private String name;

        public Producer(Store s, String name) {
            this.s = s;
            this.name = name;
        }

        @Override
        public void run() {
            int[] impare = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
            while (true) {
                try {
                    s.put(name,
                            impare[(int) (Math.random() * 10)],
                            impare[(int) (Math.random() * 10)]);
                    Thread.sleep(100);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

   
    static class Consumer implements Runnable {
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
                    FXLogger.log(name + " a finalizat.");
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
            }
        }
    }

    
    public static class Main {
        public static final int NR_PROD = 3;
        public static final int NR_CONS = 4;
        public static final int OB_MAX_PE_CONSUMATOR = 2;
        public static final int MAX_DEPOZIT = 5;
        public static final int OB_MAX_PE_PRODUCATOR = 2;
        public static final int CONSUM_MAXIM_TOTAL = NR_CONS * OB_MAX_PE_CONSUMATOR;
    }

    
    @Override
    public void start(Stage stage) {
        TextArea area = new TextArea();
        area.setEditable(false);
        FXLogger.area = area;

        BorderPane root = new BorderPane(area);
        Scene scene = new Scene(root, 700, 500);

        stage.setTitle("Rezultate Producatori / Consumatori");
        stage.setScene(scene);
        stage.show();

        startThreads();
    }

    private void startThreads() {
        Store store = new Store();
        ExecutorService pool = Executors.newFixedThreadPool(Main.NR_PROD + Main.NR_CONS);

        for (int i = 0; i < Main.NR_PROD; i++)
            pool.execute(new Producer(store, "Producator_" + (i + 1)));

        for (int i = 0; i < Main.NR_CONS; i++)
            pool.execute(new Consumer(store, "Consumator_" + (i + 1)));

        pool.shutdown();

        new Thread(() -> {
            try {
                while (!pool.isTerminated())
                    Thread.sleep(50);
                FXLogger.log("\nToate thread-urile au finalizat.");
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
