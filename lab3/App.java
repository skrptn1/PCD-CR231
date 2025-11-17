package lab3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    private TextArea outputArea = new TextArea();
    private int[] tablou;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15;");
        root.getChildren().addAll(outputArea);
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("Lab 2");
        primaryStage.setScene(scene);
        primaryStage.show();
        runThreads();
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
        Th2 th2 = null;
        Th4 th4 = null;
        Th1 th1 = null;
        Th3 th3 = null;
        th4 = new Th4(this);
        th2 = new Th2("Th2", tablou, this, th4);
        th1 = new Th1("Th1", tablou, this, th4);
        th3 = new Th3(th4, th1, this);
        th2.start();
        th4.start();
        th1.start();
        th3.start();
    }

    public void appendText(String text) {
        Platform.runLater(() -> outputArea.appendText(text));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
