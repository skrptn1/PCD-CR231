public class laborator3bd {
    
}
public class ThreadProgram {
    // Date comune pentru thread-uri
    private static int[] data = new int[401]; // pentru intervalul [100, 500]
   
    // Flag-uri pentru coordonarea execuției
    private static volatile boolean task1Done = false;
    private static volatile boolean task3Done = false;
   
    // Rezultate
    private static long sumaPereche = 0;

    public static void main(String[] args) {
        // Inițializare date în intervalul [100, 500]
        for (int i = 0; i < data.length; i++) {
            data[i] = 100 + i;
        }
       
        System.out.println("=== START PROGRAM CU 4 FIRE DE EXECUTIE ===\n");
       
        // Crearea celor 4 fire de execuție
        Thread th1 = new Thread(new Task1(), "Th1");
        Thread th2 = new Thread(new Task2(), "Th2");
        Thread th3 = new Thread(new Task3(), "Th3");
        Thread th4 = new Thread(new Task4(), "Th4");
       
        // Pornirea firelor
        th1.start();
        th2.start();
        th3.start();
        th4.start();
       
        try {
            // Așteptăm finalizarea tuturor firelor
            th1.join();
            th2.join();
            th3.join();
            th4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       
        System.out.println("\n=== TOATE FIRELE AU TERMINAT EXECUTIA ===");
    }
   
    // Sarcina 1: Sumele numerelor pare două câte două începând de la primul element
    static class Task1 implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (ThreadProgram.class) {
                    System.out.println("[" + Thread.currentThread().getName() + "] Procesare Sarcina 1: Suma numerelor pare doua cate doua");
                   
                    sumaPereche = 0;
                    for (int i = 0; i < data.length - 1; i++) {
                        if (data[i] % 2 == 0) {
                            // Găsim următorul număr par
                            for (int j = i + 1; j < data.length; j++) {
                                if (data[j] % 2 == 0) {
                                    long suma = (long)data[i] + data[j];
                                    sumaPereche += suma;
                                    break; // doar două câte două
                                }
                            }
                        }
                    }
                   
                    System.out.println("[" + Thread.currentThread().getName() + "] Suma totală a perechilor de numere pare: " + sumaPereche);
                    task1Done = true;
                    ThreadProgram.class.notifyAll();
                }
               
                // Așteptăm ca toate sarcinile să fie finalizate
                waitForAllTasks();
               
                // Afișare prenume student cu interval de 100ms
                String prenume = " ";
                System.out.print("\n[" + Thread.currentThread().getName() + "] Prenume: ");
                afiseazaCuInterval(prenume);
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   
    static class Task3 implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (data) {
                    System.out.println("[" + Thread.currentThread().getName() + "] Procesare Sarcina 3: Parcurgere interval [100, 500]");
                   
                    System.out.print("[" + Thread.currentThread().getName() + "] Interval parcurs: [");
                    int count = 0;
                    for (int num : data) {
                        if (count < 10) {
                            System.out.print(num + " ");
                            count++;
                        }
                    }
                    System.out.println("... " + data[data.length - 1] + "]");
                    System.out.println("[" + Thread.currentThread().getName() + "] Total elemente parcurse: " + data.length);
                   
                    task3Done = true;
                    data.notifyAll();
                }
               
                // Așteptăm ca toate sarcinile să fie finalizate
                waitForAllTasks();
               
                // Afișare disciplină cu interval de 100ms
                String disciplina = "Programarea concurenta si distribuita";
                System.out.print("\n[" + Thread.currentThread().getName() + "] Disciplina: ");
                afiseazaCuInterval(disciplina);
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   
    // Metodă auxiliară pentru a aștepta finalizarea tuturor sarcinilor
    private static void waitForAllTasks() throws InterruptedException {
        while (!task1Done || !task3Done) {
            Thread.sleep(50);
        }
    }
   
    // Metodă pentru afișare text cu interval de 100ms între caractere
    private static void afiseazaCuInterval(String text) {
        try {
            for (char c : text.toCharArray()) {
                System.out.print(c);
                System.out.flush();
                Thread.sleep(100);
            }
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}