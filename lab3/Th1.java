package lab3;

class Th1 extends Thread {

    String name = "Th1";
    int[] a = null;
    App app;
    Th2 th2;
    Th4 th4;

    public Th1(String name, int[] a, App app, Th2 th2, Th4 th4) {
        this.a = a;
        this.name = name;
        this.app = app;
    }

    public void run() {
        app.appendText(name + " waiting for other threads...\n");
        try {
            th2.join();
        } catch (InterruptedException e) {
            app.appendText(name + " interrupted while waiting.\n");
            return;
        }

        while (th4.isAlive()) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        int s = 0;
        int counter = 0;
        app.appendText("\n");
        for (int i = a.length - 1; i >= 0; i--) {
            if (a[i] % 2 == 0) {
                if (counter <= 1) {
                    s = s + a[i];
                    counter++;
                    if (counter == 2) {
                        app.appendText("Victor " + name + " " + s + "\n");
                        s = 0;
                        counter = 0;
                    }
                }
            }
        }

        String text = "Victor";
        for (char c : text.toCharArray()) {
            app.appendText(c + "");
            // System.out.print(c);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\n");

    }
}