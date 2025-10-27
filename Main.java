
public class Main {
    public static void main(String[] args) {

        Counter1 cnt1, cnt2, cnt3, cnt4;
        NameThread nameThread;

        int Tablou[] = new int[101];
        for (int i = 0; i < 100; i++) {
            Tablou[i] = (int) (Math.random() * 99);
            System.out.print(" " + Tablou[i]);
        }
        System.out.println("");

        cnt1 = new Counter1 (0, 99, 1, Tablou);
        cnt2 = new Counter1 (99, 0, 1, Tablou);
        cnt3 = new Counter1 (0, 99, 1, Tablou);
        cnt4 = new Counter1 (99, 0, 1, Tablou);
        nameThread = new NameThread("Ungureanu Vlad, Munteanu Maxim -Grupul 3");

        cnt1.start();
        cnt1.setName("Unu");
        cnt2.start();
        cnt2.setName("Doi");
        cnt3.start();
        cnt3.setName("Trei");
        cnt4.start();
        cnt4.setName("Patru");
         try {
            cnt1.join();
            cnt2.join();
            cnt3.join();
            cnt4.join();
        } catch (Exception e) {
        }
        nameThread.start();
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
        int suma = 0;
        int counter = 0;

        if (from < to) {
            int i = from;
            while (i <= to) {
                if (Tablou[i] % 2 != 0) {
                    suma += Tablou[i];
                    counter++;
                    if (counter == 4) { 
                    System.out.println(Thread.currentThread().getName() + " Suma (2+2 impare): " + suma);
                        suma = 0;
                        counter = 0;
                    }
                }
                i = i + step;
            }
        } 

        else {
            int i = from;
            while (i >= to) {
                if (Tablou[i] % 2 != 0) {
                    suma += Tablou[i];
                    counter++;
                    if (counter == 4) { 
                    System.out.println(Thread.currentThread().getName() + " Suma (2+2 impare): " + suma);
                        suma = 0;
                        counter = 0;
                    }
                }
                i = i - step;
            }
        }

    }
}
class NameThread extends Thread {
    String name;

    public NameThread(String name) {
        this.name = name;

    }

    @Override
    public void run() {
       for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
}