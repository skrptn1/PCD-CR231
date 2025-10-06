public class ThreadGroup {
    public static void main(String[] args){
        java.lang.ThreadGroup sys=
        Thread.currentThread() .getThreadGroup();
        sys.list();
        sys.setMaxPriority(Thread.MAX_PRIORITY-2);
        Thread current=Thread.currentThread();
        current.setPriority(current.getPriority());
        sys.list();
        java.lang.ThreadGroup gn=new java.lang.ThreadGroup("GV");
        java.lang.ThreadGroup gh=new java.lang.ThreadGroup("gn","GH"); 
        Thread Tha=new Thread(gn,"THA");
        Tha.setPriority(4);
        Thread Thb=new Thread(gh,"THB");
        Thb.setPriority(3);
        Thread Thc=new Thread(gh,"THC");
        Thc.setPriority(6);
        Thread Thd=new Thread(gn,"THD");
        Thd.setPriority(3);
        gn.list();
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

    void list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}