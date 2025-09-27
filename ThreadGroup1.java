public class ThreadGroup1 {
public static void main(String[] args) {
    ThreadGroup sys =
        Thread.currentThread().getThreadGroup();
    Thread curr = Thread.currentThread();
    curr.setPriority(curr.getPriority() - 2);

Thread fir = new Thread("Th1");
fir.setPriority(3);
fir.start();
ThreadGroup g = new ThreadGroup("GV");
Thread ThA = new Thread(g, "ThA");
ThA.setPriority(3);
ThA.start();
g.list();
ThreadGroup o = new ThreadGroup("GO");
ThreadGroup z = new ThreadGroup(o, "GZ");
z.list();

}}