package lab3;

public class lab3 {

<<<<<<< HEAD
    static int[] array = new int[100];

    public static void main(String[] args) {

        
        System.out.println("Tabloul generat:");
=======
    public static void main(String[] args) {

        int[] Tablou = new int[2112];

        // generăm vectorul
        for (int i = 0; i < 2112; i++) {
            Tablou[i] = (int)(Math.random() * 99);
        }

        // afișăm primele 100 numere pentru verificare
        System.out.println("TABLOU (primele 100 numere):");
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
        for (int i = 0; i < 100; i++) {
            array[i] = (int)(Math.random() * 100) + 1;
            System.out.print(array[i] + " ");
        }
        System.out.println("\n");

<<<<<<< HEAD
        Th1 th1 = new Th1(array);
        Th2 th2 = new Th2(array, th1);
        Th3 th3 = new Th3();
        Th4 th4 = new Th4(th3);

       
        th1.start();
        th2.start();
        th3.start();
        th4.start();
=======

        // ============================
        // 1) Th1 și Th2 → interval [0..798]
        // ============================

        Th1 t1 = new Th1(Tablou);
        Th2 t2 = new Th2(Tablou, t1);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {}


        System.out.println("\n=== Th1 & Th2 au terminat intervalul [0..798] ===\n");

        // =============================
        // 2) Th3 și Th4 → interval [1456..2111]
        // =============================

        Th3 t3 = new Th3();
        Th4 t4 = new Th4(t3);

        t3.start();
        t4.start();

        try {
            t3.join();
            t4.join();
        } catch (Exception e) {}

        System.out.println("\n=== Th3 & Th4 au terminat intervalul [1456..2111] ===\n");
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
    }
}
