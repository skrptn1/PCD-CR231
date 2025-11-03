package lab3;

public class Th4 extends Thread {
    private Th3 th3;
    App app;

    public Th4(Th3 th3, App app) {
        this.th3 = th3;
        this.app = app;
    }

    @Override
    public void run() {
        System.out.println("Pornire Th4 (interval descrescător 1567–1000):");
        for (int i = 1567; i >= 1000; i--) {
            app.appendText(i + " ");
            // System.out.print(i + " ");
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\nTh3 a terminat intervalul.");
        // System.out.println("\nTh4 a terminat intervalul.");

        while (th3 != null && th3.isAlive()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String grupa = "Grupa: CR-231";
        for (char c : grupa.toCharArray()) {
            app.appendText(c + "");
            System.out.print(c);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\n");
        // System.out.println();
    }
}
