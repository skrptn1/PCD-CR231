package lab3;

public class Th4 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 4");

        for (int i = 2111; i >= 1456; i--) {
            System.out.print(" " + getName() + " " + i);
        }
        System.out.println();


        while (!isInterrupted()) {
        
        }

        lab3.slowPrint("4: CR-231");
    }
}
