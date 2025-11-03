package Cristian_Mocreac_Lab2;
public class ThreadGroup5 {
public static void main(String[] args) {
    ThreadGroup sys =
        Thread.currentThread().getThreadGroup();
    Thread curr = Thread.currentThread();
    curr.setPriority(curr.getPriority() - 2);
Thread Th1 = new Thread("Th1");
    Th1.start();
    Thread Th2 = new Thread("Th2");
    Th2.setPriority(7);
    Th2.start();
    sys.list();
ThreadGroup gk = new ThreadGroup("GK");
Thread Th1_gk = new Thread(gk, "Th1");
Th1_gk.start();
Thread Th3 = new Thread(gk, "Th3");
Th3.start();
Thread Th2_gk = new Thread(gk, "Th2");
Th2_gk.setPriority(6);
    Th2_gk.start();
    gk.list();
ThreadGroup ge = new ThreadGroup("GE");
Thread ThA = new Thread(ge, "ThA");
ThA.start();
ge.list();
ThreadGroup gh = new ThreadGroup(ge, "GH");
Thread Tha = new Thread(gh, "Tha");
Tha.setPriority(4);
Tha.start();
Thread Thb = new Thread(gh, "Thb");
Thb.start();
Thread Thc = new Thread(gh, "Thc");
Thc.setPriority(2);
Thc.start();
Thread Thd = new Thread(gh, "Thd");
Thd.setPriority(1);
Thd.start();
gh.list();
for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        

    }}