public class ThreadGroup {
    public static void main(String[] args){
        java.lang.ThreadGroup sys=
        Thread.currentThread() .getThreadGroup();
        sys.list();
        sys.setMaxPriority(Thread.MAX_PRIORITY - 2);

     ThreadGroup n = new ThreadGroup("GN");
     Thread ThA = new Thread (n, "ThA");
     ThA.setPriority(3);
     ThA.start();
     n.list();
    
     ThreadGroup h = new ThreadGroup(n,"GH");
     Thread Tha = new Thread (h, "Tha");
     Tha.setPriority(4);
     Tha.start();
     Thread Thb = new Thread (h, "Thb");
     Thb.setPriority(3);
     Thb.start();
     Thread Thc = new Thread (h, "Thc");
     Thc.setPriority(6);
     Thc.start();
     Thread Thd = new Thread (h, "thd");
     Thd.setPriority(3);
     Thc.start();
     h.list();

     ThreadGroup m = newThreadGroup ("GM");
     Thread 

    }
}