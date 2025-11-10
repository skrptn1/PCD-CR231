import java.util.*;

class Worker extends Thread {
    private int from, to, step;
    private int[] mas;

    public Worker(int from, int to, int step, int[] mas) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.mas = mas;
    }

    public void run() {
        int i = from;
        System.out.println(getName() + " a inceput executia...");

        int s1 = -1, s2 = -1, suma = 0;

        while (i != to) {
            // Cautam prima pozitie cu numar impar
            if (mas[i] % 2 != 0) {
                s1 = i;
                i += step;

                // Cautam urmatoarea pozitie cu numar impar
                while (i >= 0 && i < mas.length) {
                    if (mas[i] % 2 != 0) {
                        s2 = i;
                        suma = s1 + s2;
                        System.out.println(getName() + " -> pozitiile " + s1 + " si " + s2 +
                                " | suma=" + suma +
                                " | valori=" + mas[s1] + ", " + mas[s2]);
                        break;
                    }
                    i += step;
                }
            }
            i += step;
        }

        System.out.println(getName() + " s-a terminat.\n");
    }
}

public class Main {
    public static void main(String[] args) {
        int[] mas = new int[100];
        Random r = new Random();

        // Generare tabloul cu valori intre 1 si 100
        for (int i = 0; i < mas.length; i++) {
            mas[i] = r.nextInt(100) + 1;
            System.out.print(mas[i] + " ");
        }
        System.out.println("\n------------------------------------------");

        // Fir 1: de la inceput spre sfarsit
        Worker th1 = new Worker(0, 99, 1, mas);
        th1.setName("Th1 (de la inceput)");

        // Fir 2: de la sfarsit spre inceput
        Worker th2 = new Worker(99, 0, -1, mas);
        th2.setName("Th2 (de la sfarsit)");

        th1.start();
        th2.start();

        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Afișare informatii studenti literă cu literă
        String text = "Lucrarea a fost realizata de studentii: Mihalachi si Malai.";

        System.out.println("\n--- Informatii studenti ---");
        for (char c : text.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\n----------------------------");
    }
}
