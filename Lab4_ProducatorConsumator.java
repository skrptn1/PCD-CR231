import java.util.ArrayList;

public class Lab4_ProducatorConsumator {

    public static void main(String[] args) throws InterruptedException {

        // Datele din enunț:
        final int X = 2;   // număr producători
        final int Y = 5;   // număr consumatori
        final int Z = 3;   // obiecte pentru fiecare consumator
        final int D = 12;  // capacitatea depozitului

        Store store = new Store(D);

        // Creăm producătorii (X = 2)
        Producer p1 = new Producer(store);
        p1.setDaemon(true);
        p1.setName("Producator #1");

        Producer p2 = new Producer(store);
        p2.setDaemon(true);
        p2.setName("Producator #2");

        // Creăm consumatorii (Y = 5), fiecare trebuie să ia Z = 3 obiecte
        Consumer c1 = new Consumer(store, Z);
        c1.setName("Consumator #1");

        Consumer c2 = new Consumer(store, Z);
        c2.setName("Consumator #2");

        Consumer c3 = new Consumer(store, Z);
        c3.setName("Consumator #3");

        Consumer c4 = new Consumer(store, Z);
        c4.setName("Consumator #4");

        Consumer c5 = new Consumer(store, Z);
        c5.setName("Consumator #5");

        // Pornim firele
        p1.start();
        p2.start();

        c1.start();
        c2.start();
        c3.start();
        c4.start();
        c5.start();

        // Așteptăm până termină toți consumatorii
        while (c1.isAlive() || c2.isAlive() || c3.isAlive() || c4.isAlive() || c5.isAlive()) {
            // buclă de așteptare
        }

        System.out.println("\n==============================");
        System.out.println("Toți consumatorii au fost îndestulați cu " + Z + " obiecte.");
        System.out.println("Programul se încheie.");
    }
}


// Clasa Store – depozitul partajat
class Store {

    private final ArrayList<Integer> stockList = new ArrayList<>();
    private final int capacity;  // D – capacitatea depozitului

    public Store(int capacity) {
        this.capacity = capacity;
    }

    // Consumatorul ia un obiect din depozit
    public synchronized int get(String consumerName) {
        // dacă depozitul e gol, consumatorul așteaptă
        while (stockList.size() < 1) {
            System.out.println(consumerName + ": depozitul este gol, aștept...");
            try {
                wait();
            } catch (InterruptedException e) {
                // ignorăm
            }
        }

        // luăm ultimul element
        int value = stockList.get(stockList.size() - 1);
        stockList.remove(stockList.size() - 1);

        System.out.println(consumerName + " a luat din depozit: " + value);

        if (stockList.size() > 0) {
            System.out.print("Depozitul are acum " + stockList.size() + " obiecte -> ");
            for (int v : stockList) {
                System.out.print(v + " ");
            }
            System.out.println();
        } else {
            System.out.println("Depozitul este gol după consum.");
        }

        // anunțăm producătorii/ceilalți consumatori
        notifyAll();

        return value;
    }

    // Producătorul pune un obiect în depozit
    public synchronized void put(String producerName, int value) {
        // dacă depozitul e plin, producătorul așteaptă
        while (stockList.size() >= capacity) {
            System.out.println(producerName + ": depozitul este plin (" + capacity + "), aștept...");
            try {
                wait();
            } catch (InterruptedException e) {
                // ignorăm
            }
        }

        // adăugăm obiectul
        stockList.add(value);
        System.out.println(producerName + " a pus în depozit numărul: " + value);

        if (stockList.size() == capacity) {
            System.out.print("Depozitul este plin! Conține " + stockList.size() + " obiecte -> ");
            for (int v : stockList) {
                System.out.print(v + " ");
            }
            System.out.println();
        } else {
            System.out.print("Depozitul are acum " + stockList.size() + " obiecte -> ");
            for (int v : stockList) {
                System.out.print(v + " ");
            }
            System.out.println();
        }

        // anunțăm consumatorii/ceilalți producători
        notifyAll();
    }
}


// Producător – generează NUMERE PARE și le pune în depozit
class Producer extends Thread {

    private final Store store;

    public Producer(Store store) {
        this.store = store;
    }

    @Override
    public void run() {
        // vector de numere pare (tipul obiectelor)
        int[] pare = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20,
                               22, 24, 26, 28, 30, 32, 34, 36, 38, 40};

        while (true) {
            int idx = (int) (Math.random() * pare.length);
            int value = pare[idx];

            store.put(getName(), value);

            try {
                // mică pauză, ca să se vadă mai clar alternanța
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignorăm
            }
        }
    }
}


// Consumător – ia Z obiecte și apoi se oprește
class Consumer extends Thread {

    private final Store store;
    private final int need;   // Z = câte obiecte trebuie să consume

    public Consumer(Store store, int need) {
        this.store = store;
        this.need = need;
    }

    @Override
    public void run() {
        int taken = 0;

        for (int i = 0; i < need; i++) {
            int value = store.get(getName());
            taken++;
            // aici ai putea stoca valorile consumate, dacă e nevoie
        }

        System.out.println(getName() + " a luat " + taken + " obiecte. Thread-ul a finalizat.");
    }
}
