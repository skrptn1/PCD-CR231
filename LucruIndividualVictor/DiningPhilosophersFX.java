package LucruIndividualVictor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersFX extends Application {

    // Constante pentru configurare
    private static final int NUM_PHILOSOPHERS = 5;
    private static final double CENTER_X = 400;
    private static final double CENTER_Y = 300;
    private static final double TABLE_RADIUS = 150;
    private static final double PHILOSOPHER_RADIUS = 30;

    // Culori pentru stări
    private static final Color COLOR_THINKING = Color.LIGHTBLUE;
    private static final Color COLOR_HUNGRY = Color.ORANGE;
    private static final Color COLOR_EATING = Color.LIGHTGREEN;

    private final Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
    private final Fork[] forks = new Fork[NUM_PHILOSOPHERS];
    private volatile boolean running = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        // 1. Inițializare Furculițe (Linii)
        // Le plasăm între filozofi
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            double angle = 2 * Math.PI * i / NUM_PHILOSOPHERS + (Math.PI / NUM_PHILOSOPHERS); // Între filozofi
            double startX = CENTER_X + (TABLE_RADIUS - 40) * Math.cos(angle);
            double startY = CENTER_Y + (TABLE_RADIUS - 40) * Math.sin(angle);
            double endX = CENTER_X + (TABLE_RADIUS + 10) * Math.cos(angle);
            double endY = CENTER_Y + (TABLE_RADIUS + 10) * Math.sin(angle);

            forks[i] = new Fork(i, startX, startY, endX, endY);
            root.getChildren().add(forks[i].visualLine);
        }

        // 2. Inițializare Filozofi (Cercuri)
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            double angle = 2 * Math.PI * i / NUM_PHILOSOPHERS;
            double x = CENTER_X + TABLE_RADIUS * Math.cos(angle);
            double y = CENTER_Y + TABLE_RADIUS * Math.sin(angle);

            // Furculița din stânga este i, cea din dreapta este (i+1) % N
            // ATENȚIE: Pentru a evita deadlock, folosim ierarhia resurselor:
            // Luăm întâi lock-ul cu ID mai mic, apoi cel cu ID mai mare.
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % NUM_PHILOSOPHERS];

            philosophers[i] = new Philosopher(i, x, y, leftFork, rightFork);

            root.getChildren().addAll(philosophers[i].visual, philosophers[i].label);
        }

        // 3. Pornirea Thread-urilor
        for (Philosopher p : philosophers) {
            new Thread(p).start();
        }

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Problema Filozofilor Mâncăcioși (Vizualizare)");
        primaryStage.setScene(scene);

        // Oprire corectă a thread-urilor la închiderea ferestrei
        primaryStage.setOnCloseRequest(e -> {
            running = false;
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    // --- Clasa pentru Furculiță ---
    class Fork {
        final int id;
        final Lock lock = new ReentrantLock();
        final Line visualLine;

        // Coordonatele originale (pe masă)
        final double tableStartX, tableStartY, tableEndX, tableEndY;

        public Fork(int id, double sx, double sy, double ex, double ey) {
            this.id = id;
            this.tableStartX = sx;
            this.tableStartY = sy;
            this.tableEndX = ex;
            this.tableEndY = ey;

            this.visualLine = new Line(sx, sy, ex, ey);
            this.visualLine.setStroke(Color.WHITE);
            this.visualLine.setStrokeWidth(4);
        }

        // Mută vizual furculița către un filozof
        void moveTo(double px, double py) {
            Platform.runLater(() -> {
                visualLine.setStartX(px);
                visualLine.setStartY(py);
                visualLine.setEndX(px + 20); // O mică lungime relativă
                visualLine.setEndY(py + 20);
                visualLine.setStroke(Color.RED); // Arată că e ocupată
            });
        }

        // Pune furculița înapoi pe masă
        void putDown() {
            Platform.runLater(() -> {
                visualLine.setStartX(tableStartX);
                visualLine.setStartY(tableStartY);
                visualLine.setEndX(tableEndX);
                visualLine.setEndY(tableEndY);
                visualLine.setStroke(Color.WHITE);
            });
        }
    }

    // --- Clasa pentru Filozof (Thread) ---
    class Philosopher implements Runnable {
        final int id;
        final Circle visual;
        final Text label;
        final Fork leftFork;
        final Fork rightFork;
        final double x, y; // Poziția filozofului

        public Philosopher(int id, double x, double y, Fork left, Fork right) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.leftFork = left;
            this.rightFork = right;

            this.visual = new Circle(x, y, PHILOSOPHER_RADIUS);
            this.visual.setFill(COLOR_THINKING);
            this.visual.setStroke(Color.BLACK);

            this.label = new Text(x - 5, y + 5, String.valueOf(id + 1));
            this.label.setFont(Font.font(20));
            this.label.setFill(Color.BLACK);
        }

        // Metodă ajutătoare pentru a actualiza culoarea UI
        private void updateStatus(Color color) {
            Platform.runLater(() -> visual.setFill(color));
        }

        private void sleepRandom(int min, int max) throws InterruptedException {
            Thread.sleep((long) (Math.random() * (max - min) + min));
        }

        @Override
        public void run() {
            try {
                while (running) {
                    // 1. GÂNDEȘTE
                    updateStatus(COLOR_THINKING);
                    sleepRandom(1000, 3000);

                    // 2. I SE FACE FOAME
                    updateStatus(COLOR_HUNGRY);

                    // Determinăm ordinea de blocare pentru a evita Deadlock
                    // Luăm furculița cu ID mai mic prima dată
                    Fork first = leftFork.id < rightFork.id ? leftFork : rightFork;
                    Fork second = leftFork.id < rightFork.id ? rightFork : leftFork;

                    // Încearcă să ia prima furculiță
                    first.lock.lock();
                    try {
                        first.moveTo(x - 10, y + 10); // Vizualizare: ia furculița

                        // Încearcă să ia a doua furculiță
                        second.lock.lock();
                        try {
                            second.moveTo(x + 10, y + 10); // Vizualizare: ia furculița

                            // 3. MĂNÂNCĂ (are ambele furculițe)
                            updateStatus(COLOR_EATING);
                            sleepRandom(1000, 3000);

                        } finally {
                            // Pune a doua furculiță jos
                            second.putDown();
                            second.lock.unlock();
                        }
                    } finally {
                        // Pune prima furculiță jos
                        first.putDown();
                        first.lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}