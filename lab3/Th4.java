package lab3;
public class Th4 extends Thread {

<<<<<<< HEAD
    Th3 th3;

    public Th4(Th3 th3) {
        this.th3 = th3;
=======
    Thread t3;

    public Th4(Thread t3) {
        this.t3 = t3;
        setName("Th4");
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
    }

    @Override
    public void run() {

<<<<<<< HEAD
        System.out.println("Th4 - Interval [2111, 1456] descrescator:");
=======
        System.out.println("Th4 parcurge intervalul [1456..2111] invers:");
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1

        for (int i = 2111; i >= 1456; i--) {
            System.out.print(i + " ");
        }
<<<<<<< HEAD
        System.out.println();

        
        try { th3.join(); } catch (Exception e) {}

        String grupa = "Grupa: CR-231";
        for (char c : grupa.toCharArray()) {
=======
        System.out.println("\n");

        try { t3.join(); } catch (Exception e) {}

        slowPrint("CR-231");
    }

    private void slowPrint(String t) {
        for (char c : t.toCharArray()) {
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}
<<<<<<< HEAD
=======



>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
