package lab3;

public class lab3 {

    static int[] array = new int[100];

    public static void main(String[] args) {

        
        System.out.println("Tabloul generat:");
        for (int i = 0; i < 100; i++) {
            array[i] = (int)(Math.random() * 100) + 1;
            System.out.print(array[i] + " ");
        }
        System.out.println("\n");

        Th1 th1 = new Th1(array);
        Th2 th2 = new Th2(array, th1);
        Th3 th3 = new Th3();
        Th4 th4 = new Th4(th3);

       
        th1.start();
        th2.start();
        th3.start();
        th4.start();
    }
}
