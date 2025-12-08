import java.util.Random;

public class lab4 {
    public static void main(String[] args) throws InterruptedException {

        Depozit depozit = new Depozit();

        Producator p1 = new Producator(depozit, 1);
        Producator p2 = new Producator(depozit, 2);
        Producator p3 = new Producator(depozit, 3);
        Producator p4 = new Producator(depozit, 4);

        Consumator c1 = new Consumator(depozit, 1);
        Consumator c2 = new Consumator(depozit, 2);
        Consumator c3 = new Consumator(depozit, 3);

        p1.start();
        p2.start();
        p3.start();
        p4.start();

        Thread.sleep(500);

        c1.start();
        c2.start();
        c3.start();
    }
}

class Depozit {
    private char[] buffer = new char[10];
    private int count = 0;
    volatile boolean inchis = false;

    synchronized void produce(char c1, char c2) throws InterruptedException {
        // STOP producatori cand depozitul e plin (10)
        while (count == buffer.length && !inchis) {
            System.out.println("[DEPOZIT PLIN] Productia este oprita");
            wait();
        }
        if (inchis) return;

        // garantam ca exista loc pentru 2
        while (count + 2 > buffer.length && !inchis) {
            wait();
        }
        if (inchis) return;

        buffer[count++] = c1;
        buffer[count++] = c2;

        notifyAll();
    }

    synchronized char consume() throws InterruptedException {
        while (count == 0 && !inchis) {
            wait();
        }

        if (count == 0 && inchis)
            return 0;

        char c = buffer[--count];

        // DACA s-au consumat cel putin 2 (count <= 8), producatorii pot relua
        if (count <= 8) {
            notifyAll();
        }

        return c;
    }

    synchronized void inchide() {
        inchis = true;
        notifyAll();
    }
}

class Producator extends Thread {
    private Depozit depozit;
    private int id;
    private Random r = new Random();
    private char[] vocale = {'A', 'E', 'I', 'O', 'U'};

    public Producator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!depozit.inchis) {
                char v1 = vocale[r.nextInt(vocale.length)];
                char v2 = vocale[r.nextInt(vocale.length)];

                depozit.produce(v1, v2);

                synchronized (System.out) {
                    System.out.println("Producator " + id + " a produs: " + v1 + " " + v2);
                }

                sleep(200);
            }
        } catch (InterruptedException e) {}
    }
}

class Consumator extends Thread {
    private Depozit depozit;
    private int id;
    private int necesita = 3;
    private static int terminati = 0;

    public Consumator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (necesita > 0) {
                char v = depozit.consume();
                if (v == 0) break;

                synchronized (System.out) {
                    System.out.println("Consumator " + id + " a consumat: " + v);
                }

                necesita--;
                sleep(300);
            }

            synchronized (Consumator.class) {
                terminati++;
                if (terminati == 3) {
                    depozit.inchide();
                    synchronized (System.out) {
                        System.out.println("=== TOTI CONSUMATORII AU TERMINAT ===");
                    }
                }
            }

        } catch (InterruptedException e) {}
    }
}
