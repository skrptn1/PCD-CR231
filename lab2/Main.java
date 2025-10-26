import java.util.Random;



public class Main {
    public static void main(String[] args) {
        int[] mas = new int[100];
        Random rnd = new Random();

        // generare valori între 1 și 100
        for (int i = 0; i < mas.length; i++) {
            mas[i] = rnd.nextInt(100) + 1;
        }

        Th2 t2 = new Th2(mas);
        t2.start();

        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // După terminarea firului, afișăm textul cu pauză de 100ms
        String info = "Lucrarea realizata de: Popescu Ion si Ionescu Maria";
        for (char c : info.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }
}
