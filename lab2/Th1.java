package lab2;

class Array {

    int[] a;
    int currentIndex = 0;
    App app;

    public Array(int size, App app) {
        a = new int[size];
        this.app = app;
    }

    public void add(int value) {
        a[currentIndex] = value;
        if (currentIndex < a.length) {
            currentIndex++;
        }
    }

    public void print() {
        for (int elem : a) {
            if (elem != 0) {
                app.appendText(elem + " ");
                // System.out.print(elem + " ");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class Th1 implements Runnable {

    String name = "Th1";
    int[] a = null;
    App app;

    public Th1(String name, int[] a) {
        this.a = a;
        this.name = name;
    }

    public Th1(String name, int[] a, App app) {
        this.a = a;
        this.name = name;
        this.app = app;
    }

    public void run() {
        Array sumArray = new Array(a.length / 2, app);
        int s = 0;
        int counter = 0;
        app.appendText("\n");
        // System.out.println("\n");
        for (int i = 0; i < a.length; i++) {
            if (a[i] % 2 == 0) {
                if (counter <= 1) {
                    s = s + i;
                    counter++;
                    if (counter == 2) {
                        app.appendText("Victor " + name + " " + s + "\n");
                        System.out.println("Victor " + name + " " + s);
                        sumArray.add(s);
                        s = 0;
                        counter = 0;
                    }
                }
            }
        }
    }
}