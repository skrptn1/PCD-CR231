package laborator3;

public class Main {

    //Mihalachi
    public static int sumaDeLaInceput(int[] arr) {
        int suma = 0;
        int count = 0; 

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] % 2 != 0) { 
                if (count % 2 == 0 || count % 2 == 1) { 
                    suma += i;
                }
                count++;
            }
        }
        return suma;
    }

    public static int sumaDeLaInceputCorect(int[] arr) {
        int suma = 0;
        int pereche = 0; 

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] % 2 != 0) {
                suma += i;          
                pereche = (pereche + 1) % 2; 
                if (pereche == 0) {
                }
            }
        }
        return suma;
    }

    public static int sumaDeLaSfarsitCorect(int[] arr) {
        int suma = 0;
        int pereche = 0;

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] % 2 != 0) {
                suma += i;
                pereche = (pereche + 1) % 2;
            }
        }
        return suma;
    }
//Malai
    public static void parcurgeDeLaInceput() {
        System.out.println("=== Parcurgere de la început: [234, 987] ===\n");
        for (int i = 234; i <= 987; i++) {
            System.out.println(i);
        }
        System.out.println("\nGata! Interval [234, 987] parcurs.\n");
    }

    public static void parcurgeDeLaSfarsit() {
        System.out.println("=== Parcurgere de la sfârșit: [123, 890] ===\n");
        for (int i = 890; i >= 123; i--) {
            System.out.println(i);
        }
        System.out.println("\nGata! Interval [123, 890] parcurs de la sfârșit.\n");
    }

    public static void main(String[] args) {


        int[] sir = {3, 8, 5, 12, 7, 4, 9, 10, 11, 13, 15};

        System.out.print("Șirul dat: ");
        for (int x : sir) {
            System.out.print(x + " ");
        }
        System.out.println("\n");

        int sumaInceput = sumaDeLaInceputCorect(sir);
        int sumaSfarsit = sumaDeLaSfarsitCorect(sir);

        System.out.println("1. Suma pozițiilor impare (de la început, două câte două): " + sumaInceput);
        System.out.println("2. Suma pozițiilor impare (de la sfârșit, două câte două): " + sumaSfarsit);
        System.out.println();

        parcurgeDeLaInceput();
        parcurgeDeLaSfarsit();

    }
}