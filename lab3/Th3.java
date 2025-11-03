package lab3;

public class Th3 extends Thread {
    private Th4 th4; 

    public Th3(Th4 th4) {
        this.th4 = th4;
    }

    @Override
    public void run() {
        System.out.println("Pornire Th3 (interval crescător 120–690):");
        for (int i = 120; i <= 690; i++) {
            System.out.print(i + " ");
            try {
                Thread.sleep(5); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nTh3 a terminat intervalul.");

        
        try {
            th4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        
        String disciplina = "Programarea Concurentă și Distribuită";
        for (char c : disciplina.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }
}
