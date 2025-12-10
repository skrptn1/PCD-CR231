
public class Lab3 {
    
    static final int[] SIR = {3, 8, 5, 12, 7, 4, 9, 10, 11, 13, 15};
    
    public static void main(String[] args) {
       
        Th1 th1 = new Th1("Th1");
        Th2 th2 = new Th2("Th2", th1); 
        Th3 th3 = new Th3("Th3");
        Th4 th4 = new Th4("Th4", th3); 

   
        th1.start();
        th2.start();

  
        th3.start();
        th4.start();

      
        try {
            th1.join();
            th2.join();
            th3.join();
            th4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nToate firele s-au terminat. Program încheiat.");
    }
}


class Th1 extends Thread {
    private static final int[] SIR = Lab3.SIR;

    public Th1(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println(getName() + " începe Sarcina 1 (suma pozițiilor impare de la început).");

        // Găsim pozițiile numerelor impare de la început
        java.util.ArrayList<Integer> oddPositions = new java.util.ArrayList<>();
        for (int i = 0; i < SIR.length; i++) {
            if (SIR[i] % 2 != 0) {
                oddPositions.add(i);
            }
        }

        // Sumăm pozițiile două câte două
        int sumaTotala = 0;
        for (int i = 0; i + 1 < oddPositions.size(); i += 2) {
            sumaTotala += oddPositions.get(i) + oddPositions.get(i + 1);
        }

        System.out.println(getName() + " - Rezultat Sarcina 1: " + sumaTotala);

        // Așteptăm finalizarea tuturor sarcinilor
        try {
            Thread.sleep(100); // Sincronizare simplă
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Așteptăm Th2 să afișeze numele
        delayedPrintln("Prenume: Veaceslav", 100);
    }

    private void delayedPrintln(String s, int delayMs) {
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println();
    }
}


class Th2 extends Thread {
    private final int from = 123;
    private final int to = 890;
    private final Thread th1Ref;

    public Th2(String name, Thread th1Ref) {
        super(name);
        this.th1Ref = th1Ref;
    }

    @Override
    public void run() {
        System.out.println(getName() + " începe Sarcina 2 (de la " + to + " la " + from + " invers).");

        java.util.ArrayList<Integer> oddPositions = new java.util.ArrayList<>();
        int pos = 0; 
        for (int i = to; i >= from; i--) {
            pos++;
            if ((i % 2) != 0) {
                oddPositions.add(pos);
            }
         
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        java.util.List<Integer> pairSums = new java.util.ArrayList<>();
        for (int i = 0; i + 1 < oddPositions.size(); i += 2) {
            pairSums.add(oddPositions.get(i) + oddPositions.get(i + 1));
        }

        System.out.println(getName() + " - pozițiile impare (în ordinea inversă), count = " + oddPositions.size());
        System.out.println(getName() + " - sume pe perechi (count perechi = " + pairSums.size() + ").");

        
        try {
            if (th1Ref != null) {
                th1Ref.join(); 
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        
        delayedPrintln("Nume: Malai", 100);
    }

    private void delayedPrintln(String s, int delayMs) {
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println();
    }
}


class Th3 extends Thread {
    private final int from = 234;
    private final int to = 987;

    public Th3(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println(getName() + " începe Sarcina 3 (parcurgere de la început " + from + " .. " + to + ").");

        long count = 0;
        long sum = 0;
        for (int i = from; i <= to; i++) {
            count++;
            sum += i;
            
            if (count % 100 == 0) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        System.out.println(getName() + " a parcurs " + count + " numere. Suma = " + sum);

        
        delayedPrintln("Disciplina: Programarea Concurenta si Distributiva", 100);
    }

    private void delayedPrintln(String s, int delayMs) {
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println();
    }
}


class Th4 extends Thread {
    private final int from = 123;
    private final int to = 890;
    private final Thread th3Ref;

    public Th4(String name, Thread th3Ref) {
        super(name);
        this.th3Ref = th3Ref;
    }

    @Override
    public void run() {
        System.out.println(getName() + " începe Sarcina 4 (parcurgere de la " + to + " la " + from + " invers).");

        long count = 0;
        long sum = 0;
        for (int i = to; i >= from; i--) {
            count++;
            sum += i;
            if (count % 200 == 0) {
                
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        System.out.println(getName() + " a parcurs " + count + " numere (invers). Suma = " + sum);

        
        if (th3Ref != null) {
            System.out.println(getName() + " așteaptă terminarea " + th3Ref.getName() + " (verificare isAlive + sleep).");
            while (th3Ref.isAlive()) {
                try {
                    Thread.sleep(50); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        
        delayedPrintln("Grupa: CR-231", 100);
    }

    private void delayedPrintln(String s, int delayMs) {
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println();
    }
}
