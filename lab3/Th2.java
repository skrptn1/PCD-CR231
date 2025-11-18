package lab3;

class Th2 extends Thread {

    String name = "Th2";
    int[] a = null;
    App app;
    Th4 th4;

    public Th2(String name, int[] a, App app, Th4 th4) {
        this.a = a;
        this.name = name;
        this.app = app;
        this.th4 = th4;
    }

    public void run() {
        int s = 0;
        int counter = 0;
        app.appendText("\n");
        for (int i = 0; i < a.length; i++) {
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

        try {
            // for the first letter to appear when needed
            sleep(100);
        } catch (Exception e) {
            // TODO: handle exception
        }

        String text = "Pavalache Zaharenco";
        for (char c : text.toCharArray()) {
            app.appendText(c + "");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        app.appendText("\n");
        th4.interrupt();
    }
}