package lab3;

public class lab3 {

    static int size = 100;
    static int counter = 0;
    static int[] b = new int[size];
    static int[] a;

    static Th1 first  = new Th1();
    static Th2 second = new Th2();
    static Th3 third  = new Th3();
    static Th4 fourth = new Th4();

    public static void main(String[] args) {

        System.out.println("Printing Array:");
        for (int i = 0; i < size; i++) {
            b[i] = (int) Math.round((Math.random() * 100) + 15);
            System.out.print(b[i] + " ");

            if (i == 50) System.out.println();
            if (b[i] % 2 != 0) counter++; 
        }
        System.out.println();

        a = new int[counter];
        int k = 0;

        for (int i = 0; i < size; i++) {
            if (b[i] % 2 != 0)
                a[k++] = b[i];
        }

        first.start();
        second.start();
        third.start();
        fourth.start();

        try {
            first.join();
            second.join();
            third.join();
            fourth.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void slowPrint(String text) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}
