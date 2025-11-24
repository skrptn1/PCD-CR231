package lab3;

public class Th4 extends Thread {

    Thread t3;

    public Th4(Thread t3) {
        this.t3 = t3;
        setName("Th4");
    }

    @Override
    public void run() {

        System.out.println("Th4 parcurge intervalul [1456..2111] invers:");

        for (int i = 2111; i >= 1456; i--) {
            System.out.print(i + " ");
        }
        System.out.println("\n");

        try { t3.join(); } catch (Exception e) {}

        slowPrint("CR-231");
    }

    private void slowPrint(String t) {
        for (char c : t.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}



