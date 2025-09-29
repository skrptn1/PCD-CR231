public class ThreadGroup2 {
    public static void main(String[] args){
        java.lang.ThreadGroup sys=
        Thread.currentThread() .getThreadGroup();
        Thread current = Thread.currentThread();
        current.setPriority(current.getPriority());

    
     java.lang.ThreadGroup n = new java.lang.ThreadGroup("GN");
     Thread ThA = new Thread (n, "ThA");
     ThA.setPriority(3);
     ThA.start();
     n.list();
    
     java.lang.ThreadGroup h = new java.lang.ThreadGroup(n,"GH");
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
     Thd.start();
     h.list();

     java.lang.ThreadGroup m = new java.lang.ThreadGroup ("GM");
     Thread Th1 = new Thread (m, "Th1");
     Th1.setPriority(2);
     Th1.start();
     Thread Th2 = new Thread (m, "Th2");
     Th2.setPriority(3);
     Th2.start();
     Thread Th3 = new Thread (m, "Th3");
     Th3.setPriority(3);
     Th3.start();
     m.list();
    
     Thread fir1 = new Thread ("fir1");
     fir1.setPriority(8);
    
     
     Thread fir2 = new Thread ("fir2");
     fir2.setPriority(3);
     fir2.start();
     fir1.start();
        sys.list();

     System.out.println("Programul s-a finisat");
     

     


    }
}