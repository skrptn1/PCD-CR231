class Fir extends Thread {
    public Fir(ThreadGroup group, String name, int priority) {
        super(group, name);
        setPriority(priority);
    }

    @Override
    public void run() {
        try {
            System.out.println("Pornit: " + getName() + 
                               "  Grup: " + getThreadGroup().getName() +
                               "  Prioritate: " + getPriority());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}

public class FirDemo {
    public static void main(String[] args) {
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        ThreadGroup GK = new ThreadGroup(mainGroup, "GK");
        Thread Th1_GK = new Fir(GK, "Th1", 3);
        Thread Th2_GK = new Fir(GK, "Th2", 6);
        Thread Th3_GK = new Fir(GK, "Th3", 3);
        Th1_GK.start();
        Th2_GK.start();
        Th3_GK.start();
       

        Thread Th2_Nou = new Fir(mainGroup, "Th2-Nou", 7);
        Thread Th1_Nou = new Fir(mainGroup, "Th1-Nou", 3);
        Th2_Nou.start();
        Th1_Nou.start();

        ThreadGroup GE = new ThreadGroup(mainGroup, "GE");
        Thread ThA3_Nou = new Fir(GE, "ThA3-Nou", 3);
        ThA3_Nou.start();
      

        ThreadGroup GH = new ThreadGroup(GE, "GH");
        Thread Tha = new Fir(GH, "Tha", 4);
        Thread Thb = new Fir(GH, "Thb", 3);
        Thread Thc = new Fir(GH, "Thc", 2);
        Thread Thd = new Fir(GH, "Thd", 1);
        Tha.start();
        Thb.start();
        Thc.start();
        Thd.start();
     
                mainGroup.list();

    }
}
