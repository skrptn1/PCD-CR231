public class mihalachi {
public static void main(String[] args) {
    ThreadGroup sys =
        Thread.currentThread().getThreadGroup();
    sys.list();
    Thread curr = Thread.currentThread();
    curr.setPriority(curr.getPriority() - 2);
sys.list();
Thread Th1 = new Thread(sys,"Th1");
    Th1.start();
Thread Th2 = new Thread(sys,"Th2");
    Th2.start();
    sys.list();
ThreadGroup g = new ThreadGroup("GV");
Thread ThA = new Thread(g, "ThA");
    ThA.start();
    g.list();
ThreadGroup o = new ThreadGroup("GO");
ThreadGroup z = new ThreadGroup(o, "GZ");
Thread Tha = new Thread(z, "Tha");
    Tha.setPriority(1);
    Tha.start();
Thread Thb = new Thread(z, "Thb");
    Thb.start();
Thread Thc = new Thread(z, "Thc");
    Thc.start();
Thread Thd = new Thread(z, "Thd");
    Thd.setPriority(7);
    Thd.start();
    z.list();
ThreadGroup f = new ThreadGroup("GF");
Thread Th1gf = new Thread(f, "Th1");
    Th1gf.setPriority(5);
    Th1gf.start();
Thread Th2gf = new Thread(f, "Th2");
    Th2gf.start();
Thread Th3 = new Thread(f, "Th3");
    Th3.setPriority(9);
    Th3.start();
    f.list();
}}