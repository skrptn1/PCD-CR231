package Lucru_individual.Victor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger; // NOU: Pentru contor thread-safe
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Victor extends Application {

    private static final int NUM_PHILOSOPHERS = 5;
    private static final int MAX_MEALS = 15; // NOU: Limita de mese

    private final AtomicInteger mealsCompleted = new AtomicInteger(0);

    private Fork[] forks;
    private Philosopher[] philosophers;

    // Concurrency control for pausing/resuming
    private final ReentrantLock pauseLock = new ReentrantLock();
    private final Condition unpaused = pauseLock.newCondition();
    private volatile boolean isPaused = false;

    // UI Elements
    private VBox logContainer;
    private Button pauseButton;
    private ScheduledExecutorService scheduler;

    // Modern Color Palette
    private static final Color THINKING_COLOR = Color.web("#4A90E2");
    private static final Color HUNGRY_COLOR = Color.web("#F5A623");
    private static final Color EATING_COLOR = Color.web("#7ED321");
    private static final Color FREE_FORK_COLOR = Color.web("#C3C3C3");
    private static final Color BUSY_FORK_COLOR = Color.web("#E06666");

    /**
     * Represents a single resource (fork) that philosophers must acquire.
     */
    private static class Fork {
        private final int id;
        private final ReentrantLock lock;
        private final Circle uiIcon;

        public Fork(int id, Circle uiIcon) {
            this.id = id;
            this.lock = new ReentrantLock();
            this.uiIcon = uiIcon;
            updateUIIcon(false);
        }

        public boolean tryPickUp() {
            return lock.tryLock();
        }

        public void putDown() {
            if (lock.isHeldByCurrentThread()) {
                updateUIIcon(false);
                lock.unlock();
            }
        }

        public void updateUIIcon(boolean inUse) {
            Platform.runLater(() -> {
                uiIcon.setFill(inUse ? BUSY_FORK_COLOR : FREE_FORK_COLOR);
                uiIcon.setStroke(Color.DARKGRAY);
            });
        }
    }

    /**
     * Represents a philosopher thread.
     */
    private class Philosopher implements Runnable {
        private final int id;
        private final Fork leftFork;
        private final Fork rightFork;
        private State state;
        private final Label stateLabel;
        private final Circle circle;
        private final Random random = new Random();

        private enum State {
            THINKING("Gândire", THINKING_COLOR),
            HUNGRY("Așteptare", HUNGRY_COLOR),
            EATING("Mănâncă", EATING_COLOR);

            private final String text;
            private final Color color;

            State(String text, Color color) {
                this.text = text;
                this.color = color;
            }
        }

        public Philosopher(int id, Fork leftFork, Fork rightFork, Label stateLabel, Circle circle) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.stateLabel = stateLabel;
            this.circle = circle;
            setState(State.THINKING);
        }

        private void setState(State newState) {
            this.state = newState;
            Platform.runLater(() -> {
                stateLabel.setText(newState.text);
                circle.setFill(newState.color);

                if (newState != State.THINKING || scheduler != null) {
                    log("P" + id + " este în starea: " + newState.text + ".");
                }
            });
        }

        private void checkPause() throws InterruptedException {
            if (isPaused) {
                pauseLock.lock();
                try {
                    while (isPaused) {
                        unpaused.await();
                    }
                } finally {
                    pauseLock.unlock();
                }
            }
        }

        private void think() throws InterruptedException {
            setState(State.THINKING);
            long endTime = System.currentTimeMillis() + random.nextInt(3000) + 1000;
            while (System.currentTimeMillis() < endTime) {
                checkPause();
                Thread.sleep(100);
            }
        }

        private void eat() throws InterruptedException {
            setState(State.EATING);
            long endTime = System.currentTimeMillis() + random.nextInt(3000) + 1000;
            while (System.currentTimeMillis() < endTime) {
                checkPause();
                Thread.sleep(100);
            }

            int currentMeals = mealsCompleted.incrementAndGet();
            log("P" + id + " a terminat masa (" + currentMeals + "/" + MAX_MEALS + ").");

            if (currentMeals >= MAX_MEALS) {
                log("--- Limita de " + MAX_MEALS + " mese atinsă. Oprire simulare în curs... ---");
                // Oprim scheduler-ul din thread-ul curent (P4/P3 etc.)
                // Folosim Platform.runLater pentru a executa oprirea din UI thread
                Platform.runLater(() -> stopSimulation());
            }
        }

        private void acquireForks() throws InterruptedException {
            setState(State.HUNGRY);

            // Deadlock prevention: The last philosopher (P4) picks up the right fork (Fork
            // 0) first.
            Fork firstFork = (id == NUM_PHILOSOPHERS - 1) ? rightFork : leftFork;
            Fork secondFork = (id == NUM_PHILOSOPHERS - 1) ? leftFork : rightFork;

            while (mealsCompleted.get() < MAX_MEALS) {
                checkPause();

                if (firstFork.tryPickUp()) {
                    if (secondFork.tryPickUp()) {
                        // SUCCESS: Both forks acquired. VISUALLY UPDATE THEM NOW.
                        firstFork.updateUIIcon(true);
                        secondFork.updateUIIcon(true);
                        log("P" + id + " a dobândit furcile " + firstFork.id + " și " + secondFork.id
                                + ". Gata să MĂNÂNCE!");
                        return;
                    } else {
                        // Fails to get second, must put down the first.
                        firstFork.putDown();
                    }
                }

                Thread.sleep(random.nextInt(500) + 100);
            }
            throw new InterruptedException("Simularea s-a oprit din cauza limitei de mese.");
        }

        private void releaseForks() {
            leftFork.putDown();
            rightFork.putDown();
        }

        @Override
        public void run() {
            try {
                // MODIFICAT: Bucla rulează cât timp limita nu a fost atinsă
                while (mealsCompleted.get() < MAX_MEALS && !Thread.currentThread().isInterrupted()) {
                    checkPause();
                    think();

                    checkPause();
                    acquireForks();

                    checkPause();
                    eat();

                    checkPause();
                    releaseForks();
                }
            } catch (InterruptedException e) {
                // S-a oprit din cauza întreruperii (oprire grațioasă)
                setState(State.THINKING);
                Thread.currentThread().interrupt();
            } finally {
                // Ne asigurăm că eliberăm furcile chiar dacă a fost întrerupt brusc
                releaseForks();
            }
        }
    }

    /**
     * Toggles the simulation state (Pause/Resume).
     */
    private void togglePause() {
        pauseLock.lock();
        try {
            isPaused = !isPaused;
            if (!isPaused) {
                unpaused.signalAll();
                log("--- Simulare Reluată ---");
                Platform.runLater(() -> pauseButton.setText("Pauză Simulare"));
            } else {
                log("--- Simulare Pauzată ---");
                Platform.runLater(() -> pauseButton.setText("Reia Simulare"));
            }
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * Appends a message to the UI log container.
     */
    private void log(String message) {
        Platform.runLater(() -> {
            Label logEntry = new Label(
                    "[" + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
                            + "] " + message);
            logEntry.setFont(new Font("Monospaced", 10));
            logContainer.getChildren().add(logEntry);

            if (logContainer.getChildren().size() > 50) {
                logContainer.getChildren().remove(0);
            }
        });
    }

    // --- JavaFX Setup Methods ---

    @Override
    public void start(Stage primaryStage) {
        // ... (Inițializarea structurilor de date rămâne la fel)
        Circle[] forkIcons = new Circle[NUM_PHILOSOPHERS];
        forks = new Fork[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forkIcons[i] = new Circle(8);
            forks[i] = new Fork(i, forkIcons[i]);
        }

        Label[] stateLabels = new Label[NUM_PHILOSOPHERS];
        Circle[] philosopherCircles = new Circle[NUM_PHILOSOPHERS];
        philosophers = new Philosopher[NUM_PHILOSOPHERS];

        // 2. Setup the Main Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #F7F7F7;");

        // 3. Create Controls
        HBox controlBox = createControls();
        root.setBottom(controlBox);

        // 4. Create the Table/Diagram View (Top/Center)
        VBox tableContainer = createTableDiagram(forks, forkIcons, stateLabels, philosopherCircles);
        root.setTop(tableContainer);

        // 5. Create the Log/Terminal View (Center/Bottom)
        VBox logBox = createLogTerminalView();
        root.setCenter(logBox);
        BorderPane.setMargin(logBox, new Insets(10, 0, 10, 0));

        // 6. Initialize Philosophers
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % NUM_PHILOSOPHERS];
            philosophers[i] = new Philosopher(i, leftFork, rightFork, stateLabels[i], philosopherCircles[i]);
        }

        // 7. Start the simulation
        startSimulation();

        // 8. Configure and Show Stage
        primaryStage.setOnCloseRequest(e -> stopSimulation());
        primaryStage.setTitle("Problema Filosofilor la Masă");
        primaryStage.setScene(new Scene(root, 650, 750));
        primaryStage.show();
    }

    private HBox createControls() {
        pauseButton = new Button("Pauză Simulare");
        pauseButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        pauseButton.setStyle("-fx-background-color: #5BC0DE; -fx-text-fill: white; -fx-padding: 8 15 8 15;");
        pauseButton.setOnAction(e -> togglePause());

        HBox controls = new HBox(pauseButton);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10, 0, 0, 0));
        return controls;
    }

    private VBox createTableDiagram(Fork[] forks, Circle[] forkIcons, Label[] stateLabels,
            Circle[] philosopherCircles) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Simulare Masă Filosofi");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        container.getChildren().add(title);

        StackPane tableStack = new StackPane();
        Pane circlePane = new Pane();
        circlePane.setMinSize(450, 450);
        circlePane.setMaxSize(450, 450);

        Circle tableSurface = new Circle(225, 225, 120, Color.web("#D2B48C"));
        tableSurface.setStroke(Color.web("#8B4513"));
        tableSurface.setStrokeWidth(3);

        tableStack.getChildren().addAll(circlePane, tableSurface);

        double radius = 180;
        double centerX = 225;
        double centerY = 225;

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            // Philosopher position (Pi)
            double angle = 2 * Math.PI * i / NUM_PHILOSOPHERS;
            double phX = centerX + radius * Math.sin(angle);
            double phY = centerY - radius * Math.cos(angle);

            // Fork position (Fi) - Correctly aligned
            double forkAngle = 2 * Math.PI * (i - 0.5) / NUM_PHILOSOPHERS;
            double forkRadius = 135;
            double forkX = centerX + forkRadius * Math.sin(forkAngle);
            double forkY = centerY - forkRadius * Math.cos(forkAngle);

            // 1. Philosopher Circle/State
            philosopherCircles[i] = new Circle(phX, phY, 15);
            philosopherCircles[i].setStroke(Color.DARKGRAY);
            philosopherCircles[i].setStrokeWidth(1.5);

            // Philosopher ID Label
            Label phId = new Label("P" + i);
            phId.setLayoutX(phX - 8);
            phId.setLayoutY(phY - 10);
            phId.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            phId.setTextFill(Color.WHITE);

            // State Label
            stateLabels[i] = new Label();
            stateLabels[i].setLayoutX(phX - 50);
            stateLabels[i].setLayoutY(phY + 20);
            stateLabels[i].setPrefWidth(100);
            stateLabels[i].setAlignment(Pos.CENTER);
            stateLabels[i].setStyle(
                    "-fx-background-color: #EFEFEF; -fx-padding: 2px; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-background-radius: 4;");

            // 2. Fork Icon and Label (Visual Fork Fi)
            Circle forkIcon = forkIcons[i];
            forkIcon.setLayoutX(forkX);
            forkIcon.setLayoutY(forkY);

            Label forkId = new Label("F" + i);
            forkId.setLayoutX(forkX + 10);
            forkId.setLayoutY(forkY - 10);
            forkId.setFont(Font.font("Arial", FontWeight.NORMAL, 10));

            circlePane.getChildren().addAll(philosopherCircles[i], phId, stateLabels[i], forkIcon, forkId);
        }

        VBox paneWrapper = new VBox(circlePane);
        paneWrapper.setAlignment(Pos.CENTER);

        container.getChildren().add(paneWrapper);
        return container;
    }

    private VBox createLogTerminalView() {
        logContainer = new VBox(2);
        logContainer.setPadding(new Insets(8));

        ScrollPane scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.setStyle("-fx-border-color: #AAAAAA; -fx-border-width: 1px; -fx-background-color: white;");

        logContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });

        VBox logBox = new VBox(5);
        Label logTitle = new Label("Jurnal Simulare (Flux Activitate)");
        logTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logBox.getChildren().addAll(logTitle, scrollPane);

        return logBox;
    }

    private void startSimulation() {
        scheduler = Executors.newScheduledThreadPool(NUM_PHILOSOPHERS);
        for (Philosopher p : philosophers) {
            scheduler.execute(p);
        }
        log("--- Simulare Pornită cu " + NUM_PHILOSOPHERS + " filosofi și furci. ---");
        log("Strategia de prevenire a blocajului activă (P" + (NUM_PHILOSOPHERS - 1) + " sparge simetria).");
        log("Simularea se va opri automat după " + MAX_MEALS + " mese completate."); // NOU: Mesaj pentru limită
    }

    private void stopSimulation() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            log("--- Simulare Oprită. Total mese: " + mealsCompleted.get() + " ---");
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    log("Avertisment: Unele fire de execuție nu s-au oprit grațios.");
                } else {
                    log("Oprire grațioasă reușită.");
                }
            } catch (InterruptedException e) {
                log("Eroare: Procesul de oprire a fost întrerupt.");
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        stopSimulation();
    }

    public static void main(String[] args) {
        launch(args);
    }
}