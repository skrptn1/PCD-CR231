package lab3;

public class Th2 extends Thread {
    int from, to, step;
    int[] Tablou;

    public Th2(int from, int to, int step, int[] Tablou) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.Tablou = Tablou;
    }

    @Override
    public void run() {
        int count = 0;
        int suma1 = 0;
        int suma2 = 0;

        for (int i = from; i >= to; i -= step) {
            if (Tablou[i] % 2 != 0) {
                if (count < 2) suma1 += Tablou[i];
                else suma2 += Tablou[i];
                count++;

                if (count == 4) {
                    synchronized(System.out) {
                        System.out.println(getName() + " -> Suma primelor 2 impare: " + suma1);
                        System.out.println(getName() + " -> Suma urmatoarelor 2 impare: " + suma2);
                        System.out.println(getName() + " -> Suma totala a celor 4 impare: " + (suma1 + suma2));
                        System.out.println("---------------------------");
                    }
                    count = 0;
                    suma1 = 0;
                    suma2 = 0;
                }
            }
        }
    }
}
