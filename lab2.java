public class lab2 {
    public static void main(String[] args) {

        int Tablou[] = new int[101];

        System.out.println("Tablou generat:");
        for (int i = 0; i < 100; i++) {
            Tablou[i] = (int) (Math.random() * 99);
            System.out.print(" " + Tablou[i]);
        }
        System.out.println("\n");

        // Threads
        CounterFirst cntStart = new CounterFirst(0, 99, 1, Tablou);
        cntStart.setName("Start-Impare");

        CounterLast cntEnd = new CounterLast(99, 0, 1, Tablou);
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



// THREAD 1: De la primul element → ultimul

    int from, to, step;
    int[] Tablou;

    public CounterFirst(int from, int to, int step, int[] Tablou) {
        this.from = from; this.to = to;
        this.step = step; this.Tablou = Tablou;
    }

    @Override
    public void run() {

        int sumaFinala = 0;
        int counter = 0;
        int produs = 1;

        for (int i = from; i <= to; i += step) {

            if (i % 2 == 1) { // doar poziții impare

                produs *= Tablou[i];
                counter++;

                if (counter == 2) {
                    System.out.println(Thread.currentThread().getName() +
                            " Produs (start): " + produs);
                    sumaFinala += produs;
                    produs = 1;
                    counter = 0;
                }
            }
        }

        System.out.println(Thread.currentThread().getName() +
                " Suma TOTALĂ (start): " + sumaFinala);
    }
}


// THREAD 2: De la ultimul element → primul
class CounterLast extends Thread {
    int from, to, step;
    int[] Tablou;

    public CounterLast(int from, int to, int step, int[] Tablou) {
        this.from = from; this.to = to;
        this.step = step; this.Tablou = Tablou;
    }

    @Override
    public void run() {

        int sumaFinala = 0;
        int counter = 0;
        int produs = 1;

        for (int i = from; i >= to; i -= step) {

            if (i % 2 == 1) { // doar poziții impare

                produs *= Tablou[i];
                counter++;

                if (counter == 2) {
                    System.out.println(Thread.currentThread().getName() +
                            " Produs (end): " + produs);
                    sumaFinala += produs;
                    produs = 1;
                    counter = 0;
                }
            }
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