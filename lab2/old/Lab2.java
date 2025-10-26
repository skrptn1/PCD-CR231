package lab2.old;

class Array {

    int[] a;
    int currentIndex = 0;

    public Array(int size) {
        a = new int[size];
    }

    public void add(int value) {
        a[currentIndex] = value;
        if (currentIndex < a.length) {
            currentIndex++;
        }
    }

    public void print() {
        for (int elem : a) {
            // System.out.println(elem);
            if (elem != 0) {
                System.out.print(elem + " ");
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

    public Th1(String name, int[] a) {
        this.a = a;
        this.name = name;
    }

    public void run() {
        Array sumArray = new Array(a.length / 2);
        int s = 0;
        int counter = 0;
        System.out.println("\n");
        for (int i = 0; i < a.length; i++) {
            if (a[i] % 2 == 0) {
                if (counter <= 1) {
                    s = s + i;
                    counter++;
                    if (counter == 2) {
                        System.out.println("Victor " + this.name + " " + s);
                        sumArray.add(s);
                        s = 0;
                        counter = 0;
                    }
                }
                // System.out.println(counter);
            }
        }

        System.out.println("\n");
        // sumArray.print();
    }
}

// Mihai cod
class Th2 implements Runnable {

    private int from, to, step;
    private int[] tablou;
    String name = "Th2";

    public Th2(String name, int from, int to, int step, int[] tablou) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
        this.name = name;
    }

    public void run() {
        int s1 = -1, s2 = -1, s;
        int i = from;
        while (i >= to) {
            if (tablou[i] % 2 == 0) {
                if (s1 == -1) {
                    s1 = i;
                } else {
                    s2 = i;
                    s = s1 + s2;
                    System.out.println("Mihai " + this.name + " poz " + s1 + " + " + s2 + " = " +
                            s
                            + " val " + tablou[s1] + " " + tablou[s2]);

                    s1 = -1;
                    s2 = -1;
                }
            }
            i -= step;
        }
    }
}

public class Lab2 {
    public static void main(String[] args) {
        int tablou[] = new int[100];
        int start = 1;
        int end = 100;
        for (int i = 0; i < tablou.length; i++) {
            tablou[i] = (int) (Math.random() * (end - start)) + start;
            System.out.print(tablou[i] + " ");
        }
        Th1 th1 = new Th1("Th1", tablou);
        Th1 th2 = new Th1("Th2", tablou);
        Th2 th3 = new Th2("Th3", end - 1, start - 1, 1, tablou);
        Th2 th4 = new Th2("Th4", end - 1, start - 1, 1, tablou);

        Thread t1 = new Thread(th1);
        Thread t2 = new Thread(th2);
        Thread t3 = new Thread(th3);
        Thread t4 = new Thread(th4);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        System.out.println();
        String text = "Zaharenco Mihail\n";
        String text2 = "Pavalache Victor";
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < text2.length(); i++) {
            System.out.print(text2.charAt(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
