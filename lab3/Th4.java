package lab3;

public class Th4 extends Thread {
    private Th3 th3;
    private Th1 th1;
    private Th2 th2;
    App app;

    public Th4(Th3 th3, Th1 th1, Th2 th2, App app) {
        this.th3 = th3;
        this.th1 = th1;
        this.th2 = th2;
        this.app = app;
    }

    @Override
    public void run() {
        
        app.appendText("Pornire Th4 (interval descrescător 1567–1000):\n");
        for (int i = 1567; i >= 1000; i--) {
            app.appendText(i + " ");
            try {
                Thread.sleep(5); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\nTh4 a terminat intervalul.\n");

        
        while (th3.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        
        try {
            th1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
