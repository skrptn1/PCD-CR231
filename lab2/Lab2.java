package lab2;

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

class Th1 extends Thread {

    String name = "Th1";
    int[] a = null;

    public Th1(int[] a) {
        this.a = a;
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
                        System.out.println("Victor " + s);
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

public class Lab2 {

    public static void main(String[] args) {
        int tablou[] = new int[100];
        int start = 1;
        int end = 100;
        for (int i = 0; i < tablou.length; i++) {
            tablou[i] = (int) (Math.random() * (end - start)) + start;
            System.out.print(tablou[i] + " ");
        }
        Th1 th1 = new Th1(tablou);
        Th2 th2 = new Th2(end, start, -1, tablou);

        th1.start();
        th2.start();

        String text = "Student Zaharenco Mihail Grupa CR-231";
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.err.println();

    }
}

//Mihai cod 
class Th2 extends Thread {

    private int from, to, step;
    private int[] tablou;

    public Th2(int from, int to, int step, int[] tablou) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
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
                    System.out.println("poz " + s1 + " + " + s2 + " = " + s
                            + " val " + tablou[s1] + " " + tablou[s2]);

                    s1 = -1;
                    s2 = -1;
                }
            }
            i -= step;
        }
    }
}


    

