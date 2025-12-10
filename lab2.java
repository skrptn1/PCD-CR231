public class lab2 {
    public static void main(String[] args) {

        int[] Tablou = new int[101];

        System.out.println("Tablou generat:");
        for (int i = 1; i <= 100; i++) {
            Tablou[i] = (int)(Math.random() * 99);
            System.out.print(" " + Tablou[i]);
        }
        System.out.println("\n");

        // Creăm firele
        CounterFirst cntStart = new CounterFirst(Tablou);
        cntStart.setName("Start-Thread");

        CounterLast cntEnd = new CounterLast(Tablou);
        cntEnd.setName("End-Thread");

        // Pornim firele
        cntStart.start();
        cntEnd.start();

        // Așteptăm terminarea lor
        try {
            cntStart.join();
            cntEnd.join();
        } 
        catch (Exception e) {}

        // După terminarea ambelor fire → afișăm numele
        NameThread nameThread = new NameThread(
                "Cojocari Gabriel, Turcan Victor - Grupa 6"
        );
        nameThread.start();
    }
}



// ------------------------------------------------------------------
// THREAD 1: De la primul element → ultimul
// ------------------------------------------------------------------
class CounterFirst extends Thread {
    int[] Tablou;

    public CounterFirst(int[] Tablou) {
        this.Tablou = Tablou;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 99 - 6; i += 8) {

            int p1 = Tablou[i] * Tablou[i + 2];
            int p2 = Tablou[i + 4] * Tablou[i + 6];
            int suma = p1 + p2;

            System.out.println("\n" + getName() + " START");
            System.out.println("Grupa: [" + i + ", " + (i+2) + ", " + (i+4) + ", " + (i+6) + "]");
            System.out.println("Produs 1 = " + p1);
            System.out.println("Produs 2 = " + p2);
            System.out.println("Suma = " + suma);
            System.out.println(getName() + " END\n");
        }
    }
}





// ------------------------------------------------------------------
// THREAD 2: De la ultimul element → primul
// ------------------------------------------------------------------
class CounterLast extends Thread {
    int[] Tablou;

    public CounterLast(int[] Tablou) {
        this.Tablou = Tablou;
    }

    @Override
    public void run() {
        for (int i = 99; i >= 1 + 6; i -= 8) {

            int p1 = Tablou[i] * Tablou[i - 2];
            int p2 = Tablou[i - 4] * Tablou[i - 6];
            int suma = p1 + p2;

            System.out.println("\n" + getName() + " START");
            System.out.println("Grupa: [" + i + ", " + (i-2) + ", " + (i-4) + ", " + (i-6) + "]");
            System.out.println("Produs 1 = " + p1);
            System.out.println("Produs 2 = " + p2);
            System.out.println("Suma = " + suma);
            System.out.println(getName() + " END\n");
        }
    }
}



class NameThread extends Thread {
    String name;

    public NameThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try { Thread.sleep(100); } 
            catch (InterruptedException e) {}
        }
        System.out.println();
    }
}
