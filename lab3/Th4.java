package lab3;

public class Th4 extends Thread {

    private Th2 th2;
    App app;

    public Th4(Th2 th2, App app) {
        this.th2 = th2;
        this.app = app;
    }

    @Override
    public void run() {
        while (th2.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        app.appendText("Pornire Th4 (interval descrescător 1567–1000):\n");
        for (int i = 1567; i >= 1000; i--) {
            app.appendText("Th4: " + i + "\n");
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\nTh4 a terminat intervalul.\n");

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
