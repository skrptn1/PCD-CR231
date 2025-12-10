
public class mihalachi {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup systemGroup = Thread.currentThread().getThreadGroup();
        systemGroup.list();
        
        Thread currentThread = Thread.currentThread();
        int newPriority = currentThread.getPriority() - 2;
        currentThread.setPriority(newPriority);
		Thread T1 = new Thread("Th1");
		T1.start();
        Thread T2 = new Thread("Th2");
        T2.start();
        systemGroup.list();

        ThreadGroup GV = new ThreadGroup("GV");
        Thread ThA = new Thread(GV, "ThA");
        ThA.start();
        GV.list();
        
        ThreadGroup GO = new ThreadGroup("GO");
        ThreadGroup GZ = new ThreadGroup(GO, "GZ");
        
        Thread Tha = new Thread(GZ, "Tha");
        Tha.setPriority(1);
        Tha.start();
        Thread Thb = new Thread(GZ, "Thb");
        Thb.start();
        Thread Thc = new Thread(GZ, "Thc");
        Thc.start();
        Thread Thd = new Thread(GZ, "Thd");
        Thd.setPriority(7);
        Thd.start();
        GZ.list();
        
        ThreadGroup GF = new ThreadGroup("GF");
        Thread Th1gf = new Thread(GF, "Th1");
        Th1gf.setPriority(5);
        Th1gf.start();
        Thread Th2gf = new Thread(GF, "Th2");
        Th2gf.start();
        Thread Th3 = new Thread(GF, "Th3");
        Th3.setPriority(9);
        Th3.start();
        GF.list();
    }
}