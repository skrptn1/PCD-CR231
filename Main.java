public class Main {

    public static void main(String[] args) {

        System.out.println("\n--- Date student ---\n");
        delayPrint("Nume: Cojocari\n");
        delayPrint("Prenume: Gabriel\n");
        delayPrint("Grupa: CR-231\n");
        delayPrint("Disciplina: Programare concurenta\n\n");

        int[] data = { 3, 5, 8, 2, 9, 4, 7, 6, 1, 10 }; // exemplu, pui vectorul tău

        Thread t1 = new ThreadS1(data);
        Thread t2 = new ThreadS2(data);

        Thread t3 = new ThreadS3();
        Thread t4 = new ThreadS4();

        // -----------------------------
        // PRIORITATI
        // -----------------------------
        t1.setPriority(2);
        t2.setPriority(4);
        t3.setPriority(1);
        t4.setPriority(3);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {}

        System.out.println("\nTOATE THREADURILE S-AU TERMINAT CORECT\n");
    }

    static void delayPrint(String s) {
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
    }
}

/////////////////////////////////////////////////////////

// TH1 – sumă produse poziții impare, începând cu primul element
// Sincronizare: join()
class ThreadS1 extends Thread {

    private int[] v;

    public ThreadS1(int[] v) { this.v = v; }

    public void run() {
        System.out.println("TH1 START");

        int suma = 0;
        for (int i = 1; i < v.length - 2; i += 2) {
            suma += v[i] * v[i + 2];
        }

        System.out.println("TH1 SUMA = " + suma);
        System.out.println("TH1 END");
    }
}

/////////////////////////////////////////////////////////

// TH2 – sumă produse poziții impare, începând cu ultimul element
// Sincronizare cu TH1: isAlive() + yield()
class ThreadS2 extends Thread {

    private int[] v;

    public ThreadS2(int[] v) { this.v = v; }

    public void run() {
        System.out.println("TH2 START");

        // Așteaptă TH1 prin metoda a doua: isAlive() + yield()
        while (ThreadS1StillRunning()) {
            Thread.yield();
        }

        int lastOdd = (v.length % 2 == 0 ? v.length - 1 : v.length - 2);

        int suma = 0;
        for (int i = lastOdd; i >= 3; i -= 2) {
            suma += v[i] * v[i - 2];
        }

        System.out.println("TH2 SUMA = " + suma);
        System.out.println("TH2 END");
    }

    private boolean ThreadS1StillRunning() {
        // Căutăm TH1 în lista de thread-uri active
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t instanceof ThreadS1 && t.isAlive())
                return true;
        }
        return false;
    }
}

/////////////////////////////////////////////////////////

// TH3 – intervalul [654, 1278] – crescător
// Sincronizare: sleep()
class ThreadS3 extends Thread {

    public void run() {
        System.out.println("TH3 START");

        for (int i = 654; i <= 1278; i++) {
            System.out.print(i + " ");
            try { Thread.sleep(1); } catch (Exception e) {}
        }

        System.out.println("\nTH3 END");
    }
}

/////////////////////////////////////////////////////////

// TH4 – intervalul [908, 123] – descrescător
// Sincronizare cu TH3: join()
class ThreadS4 extends Thread {

    public void run() {
        System.out.println("TH4 START");

        // Caut TH3 și îl aștept
        Thread t3 = findThreadS3();
        if (t3 != null) {
            try { t3.join(); } catch (Exception e) {}
        }

        for (int i = 908; i >= 123; i--) {
            System.out.print(i + " ");
            try { Thread.sleep(1); } catch (Exception e) {}
        }

        System.out.println("\nTH4 END");
    }

    private Thread findThreadS3() {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t instanceof ThreadS3)
                return t;
        }
        return null;
    }
}
