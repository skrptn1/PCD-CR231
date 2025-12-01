package lab3;

public class Th2 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 2 (sume impare de la sfarsit)");

        for (int i = lab3.counter - 1; i >= 0; i -= 4) {

            try { Thread.sleep(100); } catch (InterruptedException e) {}

            if ((i - 3) < 0) continue;

            lab3.pairone = lab3.a[i] + lab3.a[i - 1];
            lab3.pairtwo = lab3.a[i - 2] + lab3.a[i - 3];

            int result = lab3.pairone + lab3.pairtwo;

            System.out.println("Th2: " + lab3.pairone + " + " + lab3.pairtwo + " = " + result);
        }

        lab3.slowPrint("2: Ungureanu,Munteanu");
    }
}
