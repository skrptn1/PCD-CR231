package lab3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    private TextArea outputArea = new TextArea();
    private Button startBtn = new Button("Start");
    private Button clearBtn = new Button("Sterge");
    private int[] tablou;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        // git pull test
        root.setStyle("-fx-padding: 15;");
        root.getChildren().addAll(startBtn, clearBtn, outputArea);
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        startBtn.setOnAction(e -> runThreads());
        clearBtn.setOnAction(e -> outputArea.clear());

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("Lab 2");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runThreads() {
        outputArea.clear();
        int start = 1, end = 100;
        tablou = new int[100];
        for (int i = 0; i < tablou.length; i++) {
            tablou[i] = (int) (Math.random() * (end - start)) + start;
        }

        appendText("Tabloul generat:\n");
        for (int i = 0; i < tablou.length; i++) {
            if (i == 50) {
                appendText("\n");
            }
            appendText(tablou[i] + " ");
        }
        for (int i : tablou)
            appendText(i + " ");
        appendText("\n\n");

        Th1 th1 = new Th1("Th1", tablou, this);
        Th1 th2 = new Th1("Th2", tablou, this);
        Th2 th3 = new Th2("Th3", end - 1, start - 1, 1, tablou, this);
        Th2 th4 = new Th2("Th4", end - 1, start - 1, 1, tablou, this);

        new Thread(th1).start();
        new Thread(th2).start();
        new Thread(th3).start();
        new Thread(th4).start();

        new Thread(() -> {
            String text = "Zaharenco Mihail\n";
            String text2 = "Pavalache Victor";
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < text.length(); i++) {
                appendText(String.valueOf(text.charAt(i)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < text2.length(); i++) {
                appendText(String.valueOf(text2.charAt(i)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void appendText(String text) {
        Platform.runLater(() -> outputArea.appendText(text));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
