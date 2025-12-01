package lab3;

public class Th4 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 4 (interval [2111..1456] invers)");

        for (int i = 2111; i >= 1456; i--) {
            System.out.print( " " + currentThread().getName() + " " + i );
        }
        System.out.println();

        // așteaptă Th2
        while (lab3.second.isAlive()) {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }

        lab3.slowPrint("4: CR-231");
    }
}
