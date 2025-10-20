
public class ThreadGroup1 extends Thread{
    public static void main(String[] args){
    java.lang.ThreadGroup sys=Thread.currentThread().getThreadGroup();
        Thread current=Thread.currentThread();
        current.setPriority(current.getPriority());
    java.lang.ThreadGroup gn=new java.lang.ThreadGroup("GN");
        Thread ThA=new Thread(gn,"THA");
        ThA.setPriority(3);
        ThA.start();
        gn.list();
    java.lang.ThreadGroup gm=new java.lang.ThreadGroup("GM");
        Thread Th1=new Thread(gm,"TH1");
        Th1.setPriority(2);
        Th1.start();
        Thread Th2=new Thread(gm,"TH2");
        Th2.setPriority(3);
        Th2.start();
        Thread Th3=new Thread(gm,"TH3");
        Th3.setPriority(3);
        Th3.start();
        gm.list();
        Thread Fir=new Thread("fir1");
        Fir.setPriority(8);
        Fir.start();
        Thread Fir2=new Thread("fir2");
        Fir2.setPriority(3);
        Fir2.start();
        sys.list();
    java.lang.ThreadGroup gh=new java.lang.ThreadGroup(gn,"GH"); 
        Thread Tha=new Thread(gh,"Tha");
        Tha.setPriority(4);
        Tha.start();
        Thread Thb=new Thread(gh,"Thb");
        Thb.setPriority(3);
        Thb.start();
        Thread Thc=new Thread(gh,"Thc");
        Thc.setPriority(6);
        Thc.start();
        Thread Thd=new Thread(gh,"Thd");
        Thd.setPriority(3);
        Thd.start();
        gh.list();
        System.out.println("main is over");
    }
    }