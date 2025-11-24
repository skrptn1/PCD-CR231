package lab3;
public class Th3 extends Thread {
<<<<<<< HEAD
=======

    public Th3() {
        setName("Th3");
    }
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1

    @Override
    public void run() {

<<<<<<< HEAD
        System.out.println("Th3 - Interval [0, 798]:");

        for (int i = 0; i <= 798; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        
        Thread.yield();

        String disciplina = "Programarea Concurenta si Distribuita";
        for (char c : disciplina.toCharArray()) {
=======
        System.out.println("Th3 parcurge intervalul [1456..2111] înainte:");

        for (int i = 1456; i <= 2111; i++) {
            System.out.print(i + " ");
        }
        System.out.println("\n");

        slowPrint("Programare Concurenta si Distribuita");
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
