import java.util.Random;

public class Lab3 {

    static final int ARRAY_SIZE = 100;
    static int[] b = new int[ARRAY_SIZE];
    static int[] oddPositions;
    static int oddCount = 0;

    
    static final String INFO_NUME = "Mihalachi , Malai";
    static final String INFO_PRENUME = "Cristian , Veaceslav";
    static final String INFO_DISCIPLINA = "Programarea Concurenta si Distribuita";
    static final String INFO_GRUPA = " CR-231";
    

    private static void printWithDelay(String text, String threadName) {
        System.out.print(threadName + " afiseaza: ");
        for (char c : text.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        System.out.println();
    }

    static class Th1 extends Thread {
        public Th1() { setName("Th1"); }

        @Override
        public void run() {
            System.out.println(getName() + " – Sarcina 1: sume poziții impare (de la început)");
            long total = 0;

            for (int i = 0; i < oddCount; i += 2) {
                if (i + 1 < oddCount) {
                    int sum = oddPositions[i] + oddPositions[i + 1];
                    total += sum;
                    System.out.print(sum + " ");
                }
            }
            System.out.println("\n" + getName() + " total = " + total);

            
        
        }
    }

    static class Th2 extends Thread {
        public Th2() { setName("Th2"); }

        @Override
        public void run() {
            System.out.println(getName() + " – Sarcina 2: sume poziții impare (de la sfârșit)");
            long total = 0;

            for (int i = oddCount - 1; i >= 0; i -= 2) {
                if (i - 1 >= 0) {
                    int sum = oddPositions[i] + oddPositions[i - 1];
                    total += sum;
                    System.out.print(sum + " ");
                }
            }
            System.out.println("\n" + getName() + " total = " + total);

            
            
        }
    }

    static class Th3 extends Thread {
        public Th3() { setName("Th3"); }

        @Override
        public void run() {
            System.out.println(getName() + " – Sarcina 3: parcurgere interval [234, 987]");
            for (int i = 234; i <= 987; i++) {
                System.out.print(i + " ");
            }
        }
    }

    static class Th4 extends Thread {
        public Th4() { setName("Th4"); }

        @Override
        public void run() {
            System.out.println(getName() + " – Sarcina 4: parcurgere interval [123, 890]");
            for (int i = 890; i >= 123; i--) {
                System.out.print(i + " ");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            b[i] = rand.nextInt(100) + 1;
            if (b[i] % 2 != 0) oddCount++;
        }

        oddPositions = new int[oddCount];
        int k = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            if (b[i] % 2 != 0) {
                oddPositions[k++] = i + 1;
            }
        }

        Th1 t1 = new Th1();
        Th2 t2 = new Th2();
        Th3 t3 = new Th3();
        Th4 t4 = new Th4();

        
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        t3.start();
        while (t3.isAlive()) Thread.sleep(50);

        t4.start();
        while (t4.isAlive()) Thread.sleep(50);

        System.out.println("\n---------------- TEXT FINAL ----------------");

       
        printWithDelay("Nume:" + INFO_NUME, "Th2");
        printWithDelay("Grupa:" + INFO_GRUPA, "Th4");
        printWithDelay("Prenume:" + INFO_PRENUME, "Th1");
        printWithDelay("Disciplina:" + INFO_DISCIPLINA, "Th3");

        System.out.println("\nProgramul s-a terminat.");
    }
}
