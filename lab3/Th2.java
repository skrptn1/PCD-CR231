package lab3;

class Th2 extends Thread {

    private int start = 120, stop = 690;
    String name = "Th2";
    private App app;

    public Th2(String name, App app) {
        this.name = name;
        this.app = app;
    }

    public void run() {
        for (int i = start; i <= stop; i++) {
            app.appendText("Victor " + name + " " + i + "\n");
        }

    }
}