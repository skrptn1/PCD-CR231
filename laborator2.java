public class laborator2 {
public static void main(String[] args) {
    ThreadGroup sys =
        Thread.currentThread().getThreadGroup();
    sys.list();
    Thread curr = Thread.currentThread();
    curr.setPriority(curr.getPriority() - 2);

Thread ThA = new Thread("ThA");
ThA.start();
Thread ThA11 = new Thread("Th11");
ThA11.start();
Thread Th22 = new Thread("Th22");
Th22.start();
sys.list();



ThreadGroup grup2 = new ThreadGroup("G2");
Thread Th1 = new Thread(grup2, "Th1");
Th1.setPriority(Thread.MAX_PRIORITY -5);
Th1.start();

Thread Th2 = new Thread(grup2, "Th2");
Th2.setPriority(Thread.MAX_PRIORITY -7);
Th2.start();

Thread Th33 = new Thread(grup2, "Th33");
Th33.setPriority(Thread.MAX_PRIORITY -3);
Th33.start();
grup2.list();



ThreadGroup grup1 = new ThreadGroup("G1");
ThreadGroup grup3 = new ThreadGroup( grup1, "G3");
Thread Thaa = new Thread(grup3, "Thaa");
Thaa.setPriority(Thread.MAX_PRIORITY -8);
Thaa.start();

Thread Thbb = new Thread(grup3, "Thbb");
Thbb.setPriority(Thread.MAX_PRIORITY -7);
Thbb.start();

Thread Thcc = new Thread(grup3, "Thcc");
Thcc.setPriority(Thread.MAX_PRIORITY -2);
Thcc.start();

Thread Thdd = new Thread(grup3, "Thdd");
Thdd.setPriority(Thread.MAX_PRIORITY -7);
Thdd.start();
grup3.list();

    }
}