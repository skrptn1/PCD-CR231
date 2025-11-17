package laborator1;
import java.util.*;

class MihalachiThread extends Thread {
    private int from, to, step;
    private int[] mas;

    public MihalachiThread(int from, int to, int step, int[] mas) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.mas = mas;
    }

    @Override
    public void run() {
        System.out.println(getName() + " (Mihalachi) a inceput executia...");
        int i = from;
        while (i != to) {
            if (mas[i] % 2 != 0) {
                int s1 = i;
                i += step;
                while (i >= 0 && i < mas.length) {
                    if (mas[i] % 2 != 0) {
                        int s2 = i;
                        int suma = s1 + s2;
                        System.out.println(getName() + " (Mihalachi) -> pozitii: " + s1 + " si " + s2 + " | suma=" + suma);
                        break;
                    }
                    i += step;
                }
            }
            i += step;
        }
        System.out.println(getName() + " (Mihalachi) s-a terminat.\n");
    }
}

class MalaiThread extends Thread {
    private int from, to, step;
    private int[] mas;

    public MalaiThread(int from, int to, int step, int[] mas) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.mas = mas;
    }
   
    @Override
    public void run() {
        System.out.println(getName() + " (Malai) a inceput executia...");
        int i = from;
        while (i != to) {
            if (mas[i] % 2 != 0) {
                int s1 = i;
                i += step;
                while (i >= 0 && i < mas.length) {
                    if (mas[i] % 2 != 0) {
                        int s2 = i;
                        int suma = s1 + s2;
                        System.out.println(getName() + " (Malai) -> pozitii: " + s1 + " si " + s2 + " | suma=" + suma);
                        break;
                    }
                    i += step;
                }
            }
            i += step;
        }
        System.out.println(getName() + " (Malai) s-a terminat.\n");
    }
}

public class lab1 {
    public static void main(String[] args) {
        int[] mas = new int[100];
        Random r = new Random();

        for (int i = 0; i < mas.length; i++) {
            mas[i] = r.nextInt(100) + 1;
            System.out.print(mas[i] + " ");
        }
        System.out.println("\n------------------------------------------");

        MihalachiThread th1A = new MihalachiThread(0, 99, 1, mas);
        MihalachiThread th2A = new MihalachiThread(99, 0, -1, mas);
        th1A.setName("Th1A");
        th2A.setName("Th2A");

        MalaiThread th1B = new MalaiThread(0, 99, 1, mas);
        MalaiThread th2B = new MalaiThread(99, 0, -1, mas);
        th1B.setName("Th1B");
        th2B.setName("Th2B");
        th1A.setName("T1");
        th2A.setName("T2");
        
       
       

        th1A.start();
        th2A.start();
        th1B.start();
        th2B.start();

        try {
            th1A.join();
            th2A.join();
            th1B.join();
            th2B.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
