<<<<<<< HEAD
public class ThreadGroup1 {
    public static void main(String[] args) {
        ThreadGroup systemGroup = Thread.currentThread().getThreadGroup();
        systemGroup.list();
        
        Thread currentThread = Thread.currentThread();
        int newPriority = currentThread.getPriority() - 2;
        currentThread.setPriority(newPriority);
		Thread T1 = new Thread("Th1");
		T1.start();
		Thread T2 = new Thread("Th2");
		T2.setPriority(5);
		T2.start();
        systemGroup.list();
        
        ThreadGroup groupGV = new ThreadGroup("GV");
        Thread ThA = new Thread(groupGV, "ThA");
        ThA.start();
        groupGV.list();
        
        ThreadGroup groupGO = new ThreadGroup("GO");
        ThreadGroup groupGZ = new ThreadGroup(groupGO, "GZ");
        
        Thread Tha = new Thread(groupGZ, "Tha");
        Tha.setPriority(1);
        Tha.start();
        Thread Thb = new Thread(groupGZ, "Thb");
        Thb.start();
        Thread Thc = new Thread(groupGZ, "Thc");
        Thc.start();
        Thread Thd = new Thread(groupGZ, "Thd");
        Thd.setPriority(7);
        Thd.start();
        groupGZ.list();
        
        ThreadGroup groupGF = new ThreadGroup("GF");
        Thread Th1gf = new Thread(groupGF, "Th1");
        Th1gf.setPriority(5);
        Th1gf.start();
        Thread Th2gf = new Thread(groupGF, "Th2");
        Th2gf.start();
        Thread Th3 = new Thread(groupGF, "Th3");
        Th3.setPriority(9);
        Th3.start();
        groupGF.list();
    }
}
=======
public class ThreadGroup1 {
    public static void main(String[] args) {
        ThreadGroup systemGroup = Thread.currentThread().getThreadGroup();
        systemGroup.list();
        
        Thread currentThread = Thread.currentThread();
        int newPriority = currentThread.getPriority() - 2;
        currentThread.setPriority(newPriority);
		Thread T1 = new Thread("Th1");
		T1.start();
        systemGroup.list();
        
        ThreadGroup groupGV = new ThreadGroup("GV");
        Thread ThA = new Thread(groupGV, "ThA");
        ThA.start();
        groupGV.list();
        
        ThreadGroup groupGO = new ThreadGroup("GO");
        ThreadGroup groupGZ = new ThreadGroup(groupGO, "GZ");
        
        Thread Tha = new Thread(groupGZ, "Tha");
        Tha.setPriority(1);
        Tha.start();
        Thread Thb = new Thread(groupGZ, "Thb");
        Thb.start();
        Thread Thc = new Thread(groupGZ, "Thc");
        Thc.start();
        Thread Thd = new Thread(groupGZ, "Thd");
        Thd.setPriority(7);
        Thd.start();
        groupGZ.list();
        
        ThreadGroup groupGF = new ThreadGroup("GF");
        Thread Th1gf = new Thread(groupGF, "Th1");
        Th1gf.setPriority(5);
        Th1gf.start();
        Thread Th2gf = new Thread(groupGF, "Th2");
        Th2gf.start();
        Thread Th3 = new Thread(groupGF, "Th3");
        Th3.setPriority(9);
        Th3.start();
        groupGF.list();
    }
}
>>>>>>> f009b4a9d58cc900ed326095c1980a479fb0a49a
