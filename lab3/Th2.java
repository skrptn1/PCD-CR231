package lab3;

public class Th2 extends Thread {

    int[] v;
    Thread t1;

    public Th2(int[] v, Thread t1) {
        this.v = v;
        this.t1 = t1;
        setName("Th2");
    }

    @Override
    public void run() {

        System.out.println("Th2 parcurge intervalul [0..798] în ordine descrescătoare:");
        for (int i = 798; i >= 0; i--) {
            System.out.print(i + " ");
        }
        System.out.println("\n");

        int count = 0;
        int s1 = 0, s2 = 0;

        for (int i = 798; i >= 0; i--) {
            if (v[i] % 2 != 0) {

                if (count < 2) s1 += v[i];
                else s2 += v[i];
                count++;

                if (count == 4) {
                    System.out.println("Th2 → " + s1 + " + " + s2 + " = " + (s1+s2));
                    count = 0;
                    s1 = s2 = 0;
                }
            }
        }

        // Așteaptă Th1 pentru afișarea ordonată
        try { t1.join(); } catch (Exception e) {}

        slowPrint("Ungureanu");
    }

    private void slowPrint(String t) {
        for (char c : t.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}
