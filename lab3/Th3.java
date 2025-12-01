package lab3;

public class Th3 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 3 (interval [0..798])");

        for (int i = 0; i <= 798; i++) {
            System.out.print( " " + currentThread().getName() + " " + i );
        }
        System.out.println();

        // așteaptă Th1
        while (lab3.first.isAlive()) {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }

        lab3.slowPrint("3: P C D");
    }
}
