import java.util.Random;

public class Laborator2 {
    private static int[] mas = new int[100];
    
    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
        }
        
        System.out.println("Tabloul generat:");
        for (int i = 0; i < mas.length; i++) {
            System.out.print(mas[i] + " ");
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n");
        
        Thread th1 = new Thread(new Th1());
        
        th1.start();
        
        try {
            th1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        afiseazaInformatiiStudenti();
    }
    
    static class Th1 implements Runnable {
        @Override
        public void run() {
            System.out.println("Th1 - Conditia 1: Sumele numerelor pare doua cate doua");
            
            int suma = 0;
            int contor = 0;
            
            for (int i = 0; i < mas.length; i++) {
                if (mas[i] % 2 == 0) {
                    suma += mas[i];
                    contor++;
                    
                    if (contor == 2) {
                        System.out.println("Th1 - Suma a doua numere pare: " + suma);
                        suma = 0;
                        contor = 0;
                    }
                }
            }
            
            if (contor == 1) {
                System.out.println("Th1 - Ultimul numar par ramas: " + suma);
            }
            
            System.out.println("Th1 - Terminat procesarea\n");
        }
    }
    
    private static void afiseazaInformatiiStudenti() {
        String mesaj = "Lucrarea de laborator a fost efectuata de: [Bivol Daniel]";
        
        System.out.println("\nAfisarea informatiilor despre studenti:");
        
        try {
            for (char c : mesaj.toCharArray()) {
                System.out.print(c);
                Thread.sleep(100);
            }
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
