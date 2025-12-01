package lab3;

public class Th1 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 1 (sume impare de la inceput)");

        for (int i = 0; i < lab3.counter; i += 4) {

            try { Thread.sleep(100); } catch (InterruptedException e) {}

            if (i + 4 >= lab3.counter) continue;

            lab3.pairone =lab3.a[i] +lab3.a[i + 1];
        lab3.pairtwo =lab3.a[i + 2] +lab3.a[i + 3];

            int result =lab3.pairone +lab3.pairtwo;

            System.out.println("Th1: " +lab3.pairone + " + " +lab3.pairtwo + " = " + result);
        }

        while (lab3.fourth.isAlive()) {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }

    lab3.slowPrint("1: Vlad,Maxim");
    }
}
