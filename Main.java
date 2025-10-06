import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Counter1 cnt1, cnt2;

        int Tablou[] = new int[101];
        for (int i = 0; i < 100; i++) {
            Tablou[i] = (int) (Math.random() * 99);
            System.out.print(" " + Tablou[i]);
        }
        System.out.println("");

        cnt1 = new Counter1 (0, 99, 1, Tablou);
        cnt2 = new Counter1 (99, 0, 1, Tablou);

        cnt1.start();
        cnt1.setName("Unu");
        cnt2.start();
        cnt2.setName("Doi");
    } 
}
class Counter1 extends Thread {
    int from;
    int to;
    int Tablou[];
    int step;


public Counter1(int from, int to, int step, int Tablou[]) {
        this.from = from;
        this.to = to;
        this.Tablou = Tablou;
        this.step=step;
    }
    @Override
    public void run() {
        int suma = 1;
        int counter = 0;
        int i = to; 

    while (i != from) {
        if (Tablou[i] % 2 != 0) { 
            suma += Tablou[i];
            counter++;
            if (counter == 2) {
                System.out.println(Thread.currentThread().getName() + " Suma: " + suma);
                suma = 0;
                counter = 0;
            }
        } 
        i=i+step;
    }
 }
 
}


