package lab3;
public class Th4 extends Thread {

    Th3 th3;

    public Th4(Th3 th3) {
        this.th3 = th3;
    }

    @Override
    public void run() {

        System.out.println("Th4 - Interval [2111, 1456] descrescator:");

        for (int i = 2111; i >= 1456; i--) {
            System.out.print(i + " ");
        }
        System.out.println();

        
        try { th3.join(); } catch (Exception e) {}

        String grupa = "Grupa: CR-231";
        for (char c : grupa.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}
