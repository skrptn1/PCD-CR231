package lab3;
public class Th3 extends Thread {

    @Override
    public void run() {

        System.out.println("Th3 - Interval [0, 798]:");

        for (int i = 0; i <= 798; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        
        Thread.yield();

        String disciplina = "Programarea Concurenta si Distribuita";
        for (char c : disciplina.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}
