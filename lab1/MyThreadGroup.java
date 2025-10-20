package lab1;

public class MyThreadGroup {

    public static void main(String[] args) {
        ThreadGroup sys = Thread.currentThread().getThreadGroup();

        sys.setMaxPriority(Thread.MAX_PRIORITY);
        Thread curr = Thread.currentThread();
        curr.setPriority(curr.getPriority());

        ThreadGroup g2 = new ThreadGroup("g2");
        g2.setMaxPriority(Thread.MAX_PRIORITY);
        Thread t = new Thread(g2, new Fir("ThA"));
        t.start();
        t.setPriority(1);

        ThreadGroup g3 = new ThreadGroup("g3");
        g3.setMaxPriority(Thread.MAX_PRIORITY);
        t = new Thread(g3, new Fir("Th1"));
        t.start();
        t.setPriority(4);

        t = new Thread(g3, new Fir("Th2"));
        t.start();
        t.setPriority(3);

        t = new Thread(g3, new Fir("Th3"));
        t.start();
        t.setPriority(5);

        ThreadGroup g4 = new ThreadGroup(g2, "g4");

        g4.setMaxPriority(Thread.MAX_PRIORITY);

        ThreadGroup g1 = new ThreadGroup(g4, "g1");
        g1.setMaxPriority(Thread.MAX_PRIORITY);
        t = new Thread(g1, new Fir("Tha"));
        t.start();
        t.setPriority(1);

        t = new Thread(g1, new Fir("Thb"));
        t.start();
        t.setPriority(3);

        t = new Thread(g1, new Fir("Thc"));
        t.start();
        t.setPriority(8);

        t = new Thread(g1, new Fir("Thd"));
        t.start();
        t.setPriority(3);

        // (10)
        System.out.println("Starting all threads:");

        t = new Thread(sys, new Fir("Th1"));
        t.start();
        t.setPriority(3);

        t = new Thread(sys, new Fir("Th2"));
        t.start();
        t.setPriority(6);

        sys.list();
    }
}

/// :~

class Fir extends Thread {

    String name;

    public Fir(String name) {
        this.name = name;

    }

    public void run() {
        System.out.println("Salut de la firul " + name + " Am prioritatea " + currentThread().getPriority());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
