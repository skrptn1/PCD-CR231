class Fir extends Thread {
    String name;

    public Fir(String name) {
        this.name = name;
        // start();
    }

    public void run() {
        System.out.println("Salut de la firul " + name + " Prioritatea: " + currentThread().getPriority());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Lab1 {
    public static void main(String[] args) {

        ThreadGroup G2 = new ThreadGroup("G2");
        ThreadGroup G3 = new ThreadGroup("G3");
        Thread Th11 = new Thread(new Fir("Th11"));
        Th11.setPriority(3);
        Thread Th21 = new Thread(new Fir("Th21"));
        Th21.setPriority(6);

        // sub G2
        ThreadGroup G4 = new ThreadGroup("G4");
        Thread ThA = new Thread(G2, new Fir("ThA"));
        ThA.setPriority(1);

        // sub G4
        ThreadGroup G1 = new ThreadGroup(G4, "G1");
        Thread Tha = new Thread(G1, new Fir("Tha"));
        Tha.setPriority(1);
        Thread Thb = new Thread(G1, new Fir("Thb"));
        Thb.setPriority(3);
        Thread Thc = new Thread(G1, new Fir("Thc"));
        Thc.setPriority(8);
        Thread Thd = new Thread(G1, new Fir("Thd"));
        Thd.setPriority(3);

        // sub G3
        Thread Th1 = new Thread(G3, new Fir("Th1"));
        Th1.setPriority(4);
        Thread Th2 = new Thread(G3, new Fir("Th2"));
        Th2.setPriority(3);
        Thread Th3 = new Thread(G3, new Fir("Th3"));
        Th3.setPriority(5);

        Th11.start();
        Th21.start();
        ThA.start();
        Tha.start();
        Thb.start();
        Thc.start();
        Thd.start();
        Th1.start();
        Th2.start();
        Th3.start();

        System.out.println("Starting all threads:");

        G2.list();
        G3.list();
        G4.list();
        G1.list();

    }
}

