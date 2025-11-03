public class lab1 {
    public static void main(String[] args) {
        ThreadGroup sys = Thread.currentThread().getThreadGroup();

        Thread curr = Thread.currentThread();
        curr.setPriority(curr.getPriority()-2);

        Thread Th1 = new Thread(curr,"Th1");
        Th1.setPriority(7);
        Thread Th2 = new Thread(curr,"Th2");
        Th2.setPriority(7);
        Thread ThA = new Thread(curr,"ThA");
        ThA.setPriority(3);
        Th1.start();
        Th2.start();
        ThA.start();
        sys.list();

        ThreadGroup g1 = new ThreadGroup("G1");
        g1.setMaxPriority(Thread.MAX_PRIORITY);
        ThreadGroup g3 = new ThreadGroup(g1,"G3");
        g3.setMaxPriority(Thread.MAX_PRIORITY);
        Thread Tha = new Thread(g3, "Tha");
        Tha.start();
        Tha.setPriority(3);
        Thread Thb = new Thread(g3, "Thb");
        Thb.start();
        Thb.setPriority(3);
        Thread Thc = new Thread(g3, "Thc");
        Thc.start();
        Thc.setPriority(3);
        Thread Thd = new Thread(g3, "Thd");
        Thd.start();
        Thd.setPriority(3);
        g3.list();

        ThreadGroup g2 = new ThreadGroup("G2");
        g2.setMaxPriority(Thread.MAX_PRIORITY);
        Thread Th11 = new Thread(g2, "Th1");
        Th11.start();
        Th11.setPriority(4);
        Thread Th22 = new Thread(g2, "Th2");
        Th22.start();
        Th22.setPriority(5);
        Thread Th3 = new Thread(g2, "Th3");
        Th3.start();
        Th3.setPriority(5);
        g2.list();

    }
}