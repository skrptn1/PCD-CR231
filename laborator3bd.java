public class laborator3bd {
    // Date comune pentru thread-uri
    private static int[] data = new int[401]; // pentru intervalul [100, 500]
   
    // Referințe către thread-uri pentru sincronizare
    private static Thread th1, th3;
   
    // Rezultate
    private static long sumaPereche = 0;

    public static void main(String[] args) {
        // Inițializare date în intervalul [100, 500]
        for (int i = 0; i < data.length; i++) {
            data[i] = 100 + i;
        }
       
        System.out.println("=== START PROGRAM CU 2 FIRE DE EXECUTIE ===\n");
       
        // Crearea celor 2 fire de execuție
        th1 = new Thread(new Task1(), "Th1");
        th3 = new Thread(new Task3(), "Th3");
       
        // Pornirea firelor
        th1.start();
        th3.start();
       
        try {
            // Așteptăm finalizarea tuturor firelor folosind join()
            th1.join();
            th3.join();
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
               
                // Afișare prenume student cu interval de 100ms
                String prenume = "Bogdan";
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
                System.out.println("[" + Thread.currentThread().getName() + "] Procesare Sarcina 3: Parcurgere interval [100, 500]");
               
                System.out.print("[" + Thread.currentThread().getName() + "] Interval parcurs: [");
                int count = 0;
                for (int num : data) {
                    if (count < 10) {
                        System.out.print(num + " ");
                        count++;
                    }
                    // Folosim yield() pentru a da șansa altor thread-uri
                    if (count % 50 == 0) {
                        Thread.yield();
                    }
                }
                System.out.println("... " + data[data.length - 1] + "]");
                System.out.println("[" + Thread.currentThread().getName() + "] Total elemente parcurse: " + data.length);
               
                // Afișare disciplină cu interval de 100ms
                String disciplina = "Programarea concurenta si distribuita";
                System.out.print("\n[" + Thread.currentThread().getName() + "] Disciplina: ");
                afiseazaCuInterval(disciplina);
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   
    // Metodă pentru afișare text cu interval de 100ms între caractere
    // Folosim Thread.sleep() - metodă a clasei Thread
    private static void afiseazaCuInterval(String text) {
        try {
            for (char c : text.toCharArray()) {
                System.out.print(c);
                System.out.flush();
                Thread.sleep(100); // Metodă a clasei Thread
            }
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}