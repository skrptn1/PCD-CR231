import java.util.Random;

public class lab4 {
    public static void main(String[] args) {

        Depozit depozit = new Depozit(6, 3);   

        Producator p1 = new Producator(depozit, 1);
        Producator p2 = new Producator(depozit, 2);
        Producator p3 = new Producator(depozit, 3);

        p1.start();
        p2.start();
        p3.start();
    }
}


class Depozit {
    private final char[] buffer;
    private int count = 0;
    private int index = 0;

    private int consumatoriVii;

    public static final char[] VOCALE = {'A','E','I','O','U'};

    public Depozit(int size, int nrConsumatori) {
        buffer = new char[size];
        this.consumatoriVii = nrConsumatori;
    }

    public synchronized void consumatorOprit() {
        consumatoriVii--;
        if (consumatoriVii == 0) {
            notifyAll();
        }
    }

    public synchronized boolean produce(char obj, int producerId) {

        if (consumatoriVii == 0) return false;

        while (count == buffer.length && consumatoriVii > 0) {
            System.out.println("Depozitul este plin! Producatorul " + producerId + " asteapta...");
            try { wait(); } 
            catch (InterruptedException e) 
            { Thread.currentThread().interrupt(); }
        }

        if (consumatoriVii == 0) return false;

        buffer[index] = obj;
        index = (index + 1) % buffer.length;
        count++;

        System.out.println("Producatorul " + producerId + " a produs: " + obj + " (in depozit: " + count + ")");

        notifyAll();
        return true;
    }
}


class Producator extends Thread {

    private final Depozit depozit;
    private final int id;
    private final Random random = new Random();

    public Producator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
    public void run() {

        while (true) {
            char vocala = Depozit.VOCALE[random.nextInt(Depozit.VOCALE.length)];

            boolean ok = depozit.produce(vocala, id);

            if (!ok) {
                System.out.println("Producatorul " + id + " se opreste (nu mai sunt consumatori).");
                return;
            }

            try {
                Thread.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
