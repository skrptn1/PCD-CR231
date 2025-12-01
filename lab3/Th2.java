package lab3;

public class Th2 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 2 (sume impare de la sfarsit)");

        for (int i = lab3.counter - 1; i >= 3; i -= 4) {

            int p1 = lab3.a[i] + lab3.a[i - 1];
            int p2 = lab3.a[i - 2] + lab3.a[i - 3];

            int result = p1 + p2;

            System.out.println("Th2: " + p1 + " + " + p2 + " = " + result);

        
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        lab3.slowPrint("2: Ungureanu,Munteanu");
        lab3.fourth.interrupt();
    }
}
