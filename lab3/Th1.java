package lab3;

public class Th1 extends Thread {

    @Override
    public void run() {

        System.out.println("Starting Thread 1");

        for (int i = 0; i < lab3.counter; i += 4) {

            if (i + 3 >= lab3.counter) break;

            int p1 = lab3.a[i] + lab3.a[i + 1];
            int p2 = lab3.a[i + 2] + lab3.a[i + 3];

            int result = p1 + p2;

            System.out.println("Th1: " + p1 + " + " + p2 + " = " + result);

            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }


        try { lab3.fourth.join(); } catch (InterruptedException e) {}

        lab3.slowPrint("1: Vlad,Maxim");
    }
}
