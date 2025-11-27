package lab3;

public class Th3 extends Thread {

    public Th3() {
        setName("Th3");
    }

    @Override
    public void run() {

        System.out.println("Th3 parcurge intervalul [1456..2111] înainte:");

        for (int i = 1456; i <= 2111; i++) {
            System.out.print( " " + currentThread().getName() + " " + i );
        }
        System.out.println("\n");

        slowPrint("Programare Concurenta si Distribuita");
    }

    private void slowPrint(String t) {
        for (char c : t.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}