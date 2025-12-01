package lab3;

public class Th3 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 3");

        for (int i = 0; i <= 798; i++) {
            System.out.print(" " + getName() + " " + i);
        }
        System.out.println();

    
        while (lab3.first.isAlive()) {
            Thread.yield();
        }

        lab3.slowPrint("3: P C D");
    }
}
