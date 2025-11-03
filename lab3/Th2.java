package lab3;

class Th2 extends Thread {

    String name = "Th2";
    int[] a = null;
    App app;

    public Th2(String name, int[] a, App app) {
        this.a = a;
        this.name = name;
        this.app = app;
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
        String text = "Pavalache";
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