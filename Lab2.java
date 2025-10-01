public class Lab2 {
public static void main(String[] args) {
    ThreadGroup sys =
Thread.currentThread().getThreadGroup();
    Thread curr = Thread.currentThread();
    curr.setPriority(curr.getPriority() - 2);
     Thread t = new Thread( "ThA");
 t.start();
   Thread t1 = new Thread( "Th11");
 t1.start();
 Thread t2 = new Thread( "Th22");
 t2.start();
sys.list();
ThreadGroup g2 = new ThreadGroup("G2");
 Thread t3 = new Thread(g2, "Th1");
 t3.start();
 t3.setPriority(Thread.MAX_PRIORITY-5);
 Thread t4 = new Thread(g2, "Th33");
 t4.start();
 t4.setPriority(Thread.MAX_PRIORITY-3);
 Thread t5 = new Thread(g2, "Th2");
 t5.start();
 g2.list();
 ThreadGroup g1 = new ThreadGroup("G1");
 ThreadGroup g3 = new ThreadGroup(g1, "G3");
Thread t6 = new Thread(g3,"Thaa");
 t6.setPriority(Thread.MAX_PRIORITY-8);
 t6.start();
 Thread t7 = new Thread(g3, "Thbb");
    t7.start();
     Thread t8 = new Thread(g3, "Thcc");
      t8.setPriority(Thread.MAX_PRIORITY-2);
    t8.start();
    Thread t9 = new Thread(g3, "Thdd");
      t9.setPriority(Thread.MAX_PRIORITY-7);
          t9.start();
 g3.list();
}}
