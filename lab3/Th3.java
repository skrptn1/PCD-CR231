package lab3;

public class Th3 extends Thread {
    private Th4 th4;
    private Th1 th1;
    private Th2 th2;
    App app; 

    public Th3(Th4 th4, Th1 th1, Th2 th2, App app) {
        this.th4 = th4;
        this.th1 = th1;
        this.th2 = th2;
        this.app = app;
    }

    @Override
    public void run() {
        
        app.appendText("Pornire Th3 (interval crescător 120–690):\n");
        for (int i = 120; i <= 690; i++) {
            app.appendText("Th3: " + i + "\n");
            try {
                Thread.sleep(5); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\nTh3 a terminat intervalul.\n");

        
        try {
            th4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        
        while (th1.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (th2.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
