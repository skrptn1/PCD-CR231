public class ThreadGroup8 {
public static void main(String[] args) {
    ThreadGroup sys =
        Thread.currentThread().getThreadGroup();
    sys.list();
    Thread curr = Thread.currentThread();

ThreadGroup grup4 = new ThreadGroup("G4");
grup4.list();

ThreadGroup grup3 = new ThreadGroup(grup4, "G3");
Thread Th = new Thread(grup3, "Th");
Th.setPriority(Thread.MAX_PRIORITY -8);
Th.start();
Thread Thb = new Thread(grup3, "Thb");
Thb.setPriority(Thread.MAX_PRIORITY -2);
Thb.start();
Thread Thc = new Thread(grup3, "Thc");
Thc.setPriority(Thread.MAX_PRIORITY -7);
Thc.start();
Thread Thd = new Thread(grup3, "Thd");
Thd.setPriority(Thread.MAX_PRIORITY -7);
Thd.start();
Thread Th1 = new Thread(grup3, "Th1");
Th1.setPriority(Thread.MAX_PRIORITY -2);
Th1.start();
Thread Th2 = new Thread(grup3, "Th2");
Th2.setPriority(Thread.MAX_PRIORITY -7);
Th2.start();
grup3.list();




ThreadGroup grup2 = new ThreadGroup(grup3,"G2");
Thread g3_Th1 = new Thread(grup2, "g3_Th1");
g3_Th1.setPriority(Thread.MAX_PRIORITY -7);
g3_Th1.start();
Thread g2_Th2 = new Thread(grup2, "Th2");
g2_Th2.setPriority(Thread.MAX_PRIORITY -7);
g2_Th2.start();


Thread Th3 = new Thread(grup2, "Th3");
Th3.setPriority(Thread.MAX_PRIORITY -7);
Th3.start();

Thread ThA = new Thread(grup2, "ThA");
ThA.setPriority(Thread.MAX_PRIORITY -7);
ThA.start();
grup2.list();
    }
}