package lab3;

public class Th3 extends Thread {
    private Th1 th1;
    App app;

    public Th3(Th4 th4, Th1 th1, App app) {
        this.th1 = th1;
        this.app = app;
    }

    @Override
    public void run() {

        app.appendText("Pornire Th3 (interval crescător 120–690):\n");
        for (int i = 120; i <= 690; i++) {
            String name = Thread.currentThread().getName();
            app.appendText(name + ": " + i + "");
        }
        app.appendText("\nTh3 a terminat intervalul.\n");

        while (th1.isAlive()) {
            Thread.yield();
        }

        String disciplina = "Programarea Concurentă și Distribuită";
        for (char c : disciplina.toCharArray()) {
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
