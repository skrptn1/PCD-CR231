package lab3;

public class Th4 extends Thread {

    App app;

    public Th4(App app) {
        this.app = app;
    }

    @Override
    public void run() {

        app.appendText("Pornire Th4 (interval descrescător 1567–1000):\n");
        for (int i = 1567; i >= 1000; i--) {
            app.appendText("Th4: " + i + "\n");
        }
        app.appendText("\nTh4 a terminat intervalul.\n");

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
        }

        String grupa = "Grupa: CR-231";
        for (char c : grupa.toCharArray()) {
            app.appendText(c + "");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\n");
    }
}
