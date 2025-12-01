
import java.util.Random;

public class lab4 {
    public static void main(String[] args) {

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

        c1.start();
        c2.start();
        c3.start();
    }
}

class Depozit {
    char[] buffer = new char[10];
    int count = 0;

    synchronized void produce(char c) {
        while (count == buffer.length) {
            System.out.println("[DEPOZIT PLIN] Producatorul asteapta...");
            try { wait(); } catch (InterruptedException e) {}
        }
        buffer[count] = c;
        count++;
        notifyAll();
    }

    synchronized char consume() {
        while (count == 0) {
            System.out.println("[DEPOZIT GOL] Consumatorul asteapta...");
            try { wait(); } catch (InterruptedException e) {}
        }
        char c = buffer[count - 1];
        count--;
        notifyAll();
        return c;
    }
}

class Producator extends Thread {
    Depozit depozit;
    int id;
    Random r = new Random();
    char[] vowels = {'A', 'E', 'I', 'O', 'U'};

    public Producator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char v = vowels[r.nextInt(vowels.length)];
                depozit.produce(v);
                System.out.println("Producator " + id + " a produs " + v);
                sleep(200);
            }
        } catch (InterruptedException e) {}
    }
}

class Consumator extends Thread {
    Depozit depozit;
    int id;
    int necesita = 3;

    public Consumator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (necesita > 0) {
                char v = depozit.consume();
                System.out.println("Consumator " + id + " a consumat " + v);
                necesita--;
                sleep(300);
            }
            System.out.println("Consumator " + id + " a terminat cele 3 obiecte.");
        } catch (InterruptedException e) {}
    }
}
