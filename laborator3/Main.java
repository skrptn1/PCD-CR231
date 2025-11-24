package laborator3;

public class Main {
//Mihalachi
    public static int sumaDeLaInceput(int[] arr) {
        int suma = 0;
        int count = 0; 

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] % 2 != 0) { 
                if (count % 2 == 0) {
                    suma += i; 
                } else {
                    suma += i; 
                }
                count++;
            }
        }
        return suma;
    }

    public static int sumaDeLaSfarsit(int[] arr) {
        int suma = 0;
        int count = 0;

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] % 2 != 0) {
                if (count % 2 == 0) {
                    suma += i;
                } else {
                    suma += i;
                }
                count++;
            }
        }
        return suma;
    }

    public static void main(String[] args) {
        int[] sir = {3, 8, 5, 12, 7, 4, 9, 10, 11, 13, 15};

       
        System.out.print("Șirul: ");
        for (int x : sir) {
            System.out.print(x + " ");
        }
        System.out.println("\n");

        int rezInceput = sumaDeLaInceput(sir);
        int rezSfarsit = sumaDeLaSfarsit(sir);

        System.out.println("1. Suma pozițiilor (de la început, două câte două): " + rezInceput);
        System.out.println("2. Suma pozițiilor (de la sfârșit, două câte două): " + rezSfarsit);
    }
}

//Malai
    public class laborator3{

    public static void parcurgeDeLaInceput() {
        System.out.println("=== Parcurgere de la început: intervalul [234, 987] ===\n");
        
        for (int i = 234; i <= 987; i++) {
            System.out.println("Număr: " + i);
        }
        
        System.out.println("\nGata! Am terminat parcurgerea de la început.\n");
    }

    public static void parcurgeDeLaSfarsit() {
        System.out.println("=== Parcurgere de la sfârșit: intervalul [123, 890] ===\n");
        
        for (int i = 890; i >= 123; i--) {
            System.out.println("Număr: " + i);
        }
        
        System.out.println("\nGata! Am terminat parcurgerea de la sfârșit.\n");
    }

    public static void main(String[] args) {
        
        parcurgeDeLaInceput();
        
        parcurgeDeLaSfarsit();
        
        System.out.println("Toate cele două parcurgeri au fost finalizate cu succes!");
    }
}