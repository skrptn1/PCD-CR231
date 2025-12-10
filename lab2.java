public class lab2 {
    public static void main(String[] args) {

        int Tablou[] = new int[100];

        System.out.println("Tablou generat:");
        for (int i = 0; i < 100; i++) {
            Tablou[i] = (int) (Math.random() * 99);
            System.out.print(" " + Tablou[i]);
        }
        System.out.println("\n");

        // Threads
        CounterFirst cntStart = new CounterFirst(0, 99, Tablou);
        cntStart.setName("Start-Impare");

        CounterLast cntEnd = new CounterLast(99, 0, Tablou);
        cntEnd.setName("End-Impare");

        NameThread nameThread =
                new NameThread("Cojocari Gabriel, Turcan Victor - Grupa 6");

        cntStart.start();
        cntEnd.start();

        try {
            cntStart.join();
            cntEnd.join();
        } catch (Exception e) {}

        nameThread.start();
    }
}


// ------------------------------------------------------------------
// THREAD 1: De la primul element → ultimul (1,3,5...)
// ------------------------------------------------------------------
class CounterFirst extends Thread {
    int from, to;
    int[] Tablou;

    public CounterFirst(int from, int to, int[] Tablou) {
        this.from = from; this.to = to;
        this.Tablou = Tablou;
    }

    @Override
    public void run() {

        int sumaFinala = 0;

        // Parcurgere DOAR a pozițiilor impare
        for (int i = 1; i + 2 <= to; i += 4) {  // i = 1, 5, 9 ...
            int a = Tablou[i];
            int b = Tablou[i + 2];   // perechea este (1,3), (5,7), (9,11)...

            int produs = a * b;
            sumaFinala += produs;

            System.out.println(Thread.currentThread().getName() +
                    " Produs (start) la indexurile [" + i + "," + (i + 2) + "]: "
                    + a + " * " + b + " = " + produs);
        }

        System.out.println(Thread.currentThread().getName() +
                " Suma TOTALĂ (start): " + sumaFinala);
    }
}



// ------------------------------------------------------------------
// THREAD 2: De la ultimul element → primul (99,97,95...)
// ------------------------------------------------------------------
class CounterLast extends Thread {
    int from, to;
    int[] Tablou;

    public CounterLast(int from, int to, int[] Tablou) {
        this.from = from; this.to = to;
        this.Tablou = Tablou;
    }

    @Override
    public void run() {

        int sumaFinala = 0;

        // Căutăm prima poziție impară validă dinspre dreapta
        int i = from;
        if (i % 2 == 0) i--;

        // Parcurgere DOAR a pozițiilor impare, înapoi
        for (; i - 2 >= to; i -= 4) {   // i = 99 sau 97, apoi 95, 93...
            int a = Tablou[i];
            int b = Tablou[i - 2];     // pereche (99,97), (95,93)...

            int produs = a * b;
            sumaFinala += produs;

            System.out.println(Thread.currentThread().getName() +
                    " Produs (end) la indexurile [" + i + "," + (i - 2) + "]: "
                    + a + " * " + b + " = " + produs);
        }

        System.out.println(Thread.currentThread().getName() +
                " Suma TOTALĂ (end): " + sumaFinala);
    }
}



// ------------------------------------------------------------------
// THREAD afișare nume
// ------------------------------------------------------------------
class NameThread extends Thread {
    String name;

    public NameThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }
}
