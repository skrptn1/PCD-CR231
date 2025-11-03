package lab3;

class Th1 extends Thread {

    String name = "Th1";
    int[] a = null;
    App app;

    public Th1(String name, int[] a, App app) {
        this.a = a;
        this.name = name;
        this.app = app;
    }

    public void run() {
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
    }
}