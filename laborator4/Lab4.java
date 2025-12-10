/* 
    X=Producatori: 3
    Y=Consumatori: 2
    D=Depozit: 11
    Toate operaţiile se efectuează până când fiecare consumator
este îndestulat cu 12 obiecte.
F - fiecare producător produce câte 2 obiecte de fiecare
dată
Produc consoane
*/



import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Lab4 {
    public static void main(String[] args) throws InterruptedException {

        Depozit depozit = new Depozit(11, 3);

        Thread p1 = new Thread(new Producator(depozit, 1));
        Thread p2 = new Thread(new Producator(depozit, 2));
        Thread p3 = new Thread(new Producator(depozit, 3));

        Thread c1 = new Thread(new Consumator(depozit, 1));
        Thread c2 = new Thread(new Consumator(depozit, 2));

        p1.start();
        p2.start();
        p3.start();
        c1.start();
        c2.start();


        c1.join();
        c2.join();
        p1.interrupt();
        p2.interrupt();
        p3.interrupt();
    }

    static class Producator implements Runnable {
        private final Depozit depozit;
        private final int id;

        public Producator(Depozit depozit, int id) {
            this.depozit = depozit;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    depozit.produce(id);
                }
            } catch (InterruptedException e) {

            }
        }
    }

    static class Consumator implements Runnable {
        private final Depozit depozit;
        private final int id;

        public Consumator(Depozit depozit, int id) {
            this.depozit = depozit;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 12; i++) {
                    char item = depozit.consume();
                    System.out.println("Consumator " + id +
                            " a consumat: " + item +
                            " total consumat de el: " + (i + 1));
                }
            } catch (InterruptedException e) {
            }
        }
    }

    static class Depozit {
        private final int capacity;
        private final Queue<Character> items = new LinkedList<>();
        private final Random rand = new Random();
        private int producatoriRamas;

        private static final char[] CONSOANE =
                "BCDFGHJKLMNPQRSTVWXYZ".toCharArray();

        public Depozit(int capacity, int nrProducatori) {
            this.capacity = capacity;
            this.producatoriRamas = nrProducatori;
        }

        public synchronized void produce(int producerId) throws InterruptedException {
            while (items.size() + 2 > capacity) {
                wait();
            }

            char c1 = CONSOANE[rand.nextInt(CONSOANE.length)];
            char c2 = CONSOANE[rand.nextInt(CONSOANE.length)];
            items.add(c1);
            items.add(c2);

            System.out.println("Producator " + producerId +
                    " a produs: " + c1 + ", " + c2 +
                    " total în depozit: " + items.size());

            notifyAll();
        }

        public synchronized char consume() throws InterruptedException {
            while (items.isEmpty() && producatoriRamas > 0) {
                wait();
            }

            if (items.isEmpty() && producatoriRamas == 0) return 0;

            char item = items.remove();
            notifyAll();
            return item;
        }

        public synchronized void anuntaTerminareProductie() {
            producatoriRamas--;
            notifyAll();
        }
    }
}