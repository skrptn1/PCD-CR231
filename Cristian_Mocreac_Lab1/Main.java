import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int[] mas = generateArray(100);

        Runnable r1 = new Conditie1Task(mas);
        Runnable r2 = new Conditie1Task(mas);

        Thread th1 = new Thread(r1, "Th1");
        Thread th2 = new Thread(r2, "Th2");

        th1.start();
        th2.start();

        th1.join();
        th2.join();

        String text = "Studentii care au efectuat lucrarea de laborator: Mocreac Cristian, "
                + "Untila Maxim.";
        printSlow(text, 100);
    }

    private static int[] generateArray(int n) {
        int[] a = new int[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            a[i] = rand.nextInt(100) + 1; 
        }
        return a;
    }

    private static void printSlow(String s, int delayMs) {
        for (int i = 0; i < s.length(); i++) {
            System.out.print(s.charAt(i));
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
            }
        }
        System.out.println();
    }
}

class Conditie1Task implements Runnable {
    private final int[] mas;

    public Conditie1Task(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        int suma = calculeazaSumaCond1(mas);
        System.out.println(Thread.currentThread().getName()
                + " -> Suma produselor (de la inceput, pozitii pare): " + suma);
    }

    private int calculeazaSumaCond1(int[] a) {
        int suma = 0;
        for (int i = 0; i + 2 < a.length; i += 4) {
            int x = a[i];
            int y = a[i + 2];
            suma += x * y;
        }
        return suma;
    }
}