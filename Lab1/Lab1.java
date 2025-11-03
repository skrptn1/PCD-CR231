import java.util.Random;

public class Lab1 {
    public static void main(String[] args) throws InterruptedException {
        int[] mas = generateArray(100);

        // Creăm două fire care aplică Condiția 2
        Runnable r1 = new Conditie2Task(mas);
        Runnable r2 = new Conditie2Task(mas);

        Thread th1 = new Thread(r1, "Th1");
        Thread th2 = new Thread(r2, "Th2");

        th1.start();
        th2.start();

        th1.join();
        th2.join();
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
                // ignorăm
            }
        }
        System.out.println();
    }
}

// clasa asta NU mai e publică, ca să poată sta în același fișier cu Lab1
class Conditie2Task implements Runnable {
    private final int[] mas;

    public Conditie2Task(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        int suma = calculeazaSumaCond2(mas);
        System.out.println(Thread.currentThread().getName()
                + " -> Suma produselor (de la sfarsit, pozitii pare): " + suma);
    }

    private int calculeazaSumaCond2(int[] a) {
        int suma = 0;
        // găsim ultimul indice par
        int i = a.length - 1;
        if (i % 2 != 0) i--; // facem să fie par

        // perechi (i, i-2), apoi (i-4, i-6), ...
        for (int idx = i; idx - 2 >= 0; idx -= 4) {
            int x = a[idx];
            int y = a[idx - 2];
            suma += x * y;
        }
        return suma;
    }
}
