package lab3;
public class lab3 {
    public static void main(String[] args) {

        // 1. Generăm 2112 numere aleatoare
        int[] Tablou = new int[2112];
        for (int i = 0; i < Tablou.length; i++) {
            Tablou[i] = (int) (Math.random() * 99);
        }

        // 2. Afișăm primele 100 pentru verificare
        System.out.println("TABLOU (primele 100 numere):");
        for (int i = 0; i < 100; i++) {
            System.out.print(Tablou[i] + " ");
        }
        System.out.println("\n");

        // 3. Cream thread-urile cu intervalele corecte
        Counter1 cnt1 = new Counter1(0, 399, 1, Tablou);
        Counter1 cnt2 = new Counter1(400, 798, 1, Tablou);
        Counter1 cnt3 = new Counter1(1784, 2111, 1, Tablou); // de la sfârșit
        Counter1 cnt4 = new Counter1(1456, 1783, 1, Tablou); // de la sfârșit

        cnt1.setName("vlad-Unu");
        cnt2.setName("vlad-Doi");
        cnt3.setName("MAXIM-Trei");
        cnt4.setName("MAXIM-Patru");

        // 4. Pornim thread-urile
        cnt1.start();
        cnt2.start();
        cnt3.start();
        cnt4.start();

        // 5. Așteptăm să termine
        try {
            cnt1.join();
            cnt2.join();
            cnt3.join();
            cnt4.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 6. Thread pentru afișarea numelui
        NameThread nameThread = new NameThread("Ungureanu Vlad, Munteanu Maxim - Grupul 3");
        nameThread.start();
    }
}

// Clasa pentru numărarea numerelor impare
class Counter1 extends Thread {
    int from, to, step;
    int[] Tablou;

    public Counter1(int from, int to, int step, int[] Tablou) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.Tablou = Tablou;
    }

    @Override
    public void run() {
        int count = 0;   // numără câte impare am procesat
        int suma1 = 0;   // primele 2 impare
        int suma2 = 0;   // următoarele 2 impare

        if (from <= to) { // parcurgere crescătoare
            for (int i = from; i <= to; i += step) {
                if (Tablou[i] % 2 != 0) {
                    if (count < 2) suma1 += Tablou[i];
                    else suma2 += Tablou[i];
                    count++;

                    if (count == 4) {
                        synchronized(System.out) {
                            System.out.println(getName() + " -> Suma primelor 2 impare: " + suma1);
                            System.out.println(getName() + " -> Suma următoarelor 2 impare: " + suma2);
                            System.out.println(getName() + " -> Suma totală a celor 4 impare: " + (suma1 + suma2));
                            System.out.println("---------------------------");
                        }
                        count = 0;
                        suma1 = 0;
                        suma2 = 0;
                    }
                }
            }
        } else { // parcurgere descrescătoare
            for (int i = from; i >= to; i -= step) {
                if (Tablou[i] % 2 != 0) {
                    if (count < 2) suma1 += Tablou[i];
                    else suma2 += Tablou[i];
                    count++;

                    if (count == 4) {
                        synchronized(System.out) {
                            System.out.println(getName() + " -> Suma primelor 2 impare: " + suma1);
                            System.out.println(getName() + " -> Suma următoarelor 2 impare: " + suma2);
                            System.out.println(getName() + " -> Suma totală a celor 4 impare: " + (suma1 + suma2));
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
}

// Clasa pentru afișarea numelui cu delay
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

