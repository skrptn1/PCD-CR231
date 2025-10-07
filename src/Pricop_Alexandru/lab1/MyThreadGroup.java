package src.Pricop_Alexandru.lab1;

import java.lang.*;

class Fir extends Thread {
    public Fir(ThreadGroup group, String name, int priority) {
        super(group, name);
        setPriority(priority);
    }

    @Override
    public void run() {
//        System.out.println("Firul " + getName() +
//                " | Grup: " + getThreadGroup().getName() +
//                " | Prioritate: " + getPriority());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class MyThreadGroup {
    public static void main(String[] args) {
        ThreadGroup sys = Thread.currentThread().getThreadGroup();
        sys.list();
        ThreadGroup g4 = new ThreadGroup("G4");
        ThreadGroup g3 = new ThreadGroup(g4, "G3");
        Thread tha = new Fir(g3, "Tha", 2);
        Thread thb = new Fir(g3, "Thb", 8);
        Thread thc = new Fir(g3, "Thc", 3);
        Thread thd = new Fir(g3, "Thd", 3);
        ThreadGroup g2 = new ThreadGroup(g4, "G2");
        Thread th1 = new Fir(g2, "Th1", 3);
        Thread th2 = new Fir(g2, "Th2", 3);
        Thread th3 = new Fir(g2, "Th3", 3);
        Thread th4 = new Fir(g2, "Th4", 3);
        Thread th1_g3 = new Fir(g3, "Th1", 8);
        Thread th2_g3 = new Fir(g3, "Th2", 3);
        tha.start();
        thb.start();
        thc.start();
        thd.start();
        th1.start();
        th2.start();
        th3.start();
        th4.start();
        th1_g3.start();
        th2_g3.start();
        g4.list();
        g3.list();
        g2.list();
    }
}
