package lab3;
public class lab3 {
    public static void main(String[] args) {

        int[] Tablou = new int[2112];
        for (int i = 0; i < Tablou.length; i++) {
            Tablou[i] = (int) (Math.random() * 99);
        }

        System.out.println("TABLOU (primele 100 numere):");
        for (int i = 0; i < 100; i++) {
            System.out.print(Tablou[i] + " ");
        }
        System.out.println("\n");

        Th1 t1 = new Th1(0, 399, 1, Tablou);
        Th2 t2 = new Th2(400, 798, 1, Tablou);
        Th3 t3 = new Th3(1784, 2111, 1, Tablou);
        Th4 t4 = new Th4(1456, 1783, 1, Tablou);

        t1.setName("vlad-Unu");
        t2.setName("vlad-Doi");
        t3.setName("MAXIM-Trei");
        t4.setName("MAXIM-Patru");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NameThread nameThread = new NameThread("Ungureanu Vlad, Munteanu Maxim - Grupul 3");
        nameThread.start();
    }
}

