package lab3;

public class Th1 extends Thread {

    int[] v;

    public Th1(int[] v) {
        this.v = v;
        setName("Th1");
    }

    @Override
    public void run() {

        // Afișare interval
        System.out.println("Th1 parcurge intervalul [0..798] în ordine crescătoare:");
        for (int i = 0; i <= 798; i++) {
            System.out.print( " " + currentThread().getName() + " " + i );
        }
        System.out.println("\n");

        int count = 0;
        int s1 = 0, s2 = 0;

        for (int i = 0; i <= 798; i++) {
            if (v[i] % 2 != 0) {

                if (count < 2) s1 += v[i];
                else s2 += v[i];
                count++;

                if (count == 4) {
                    System.out.println("Th1 → " + s1 + " + " + s2 + " = " + (s1+s2));
                    count = 0;
                    s1 = s2 = 0;
                }
            }
        }

        // Afișare finală
        slowPrint("Ungureanu , Munteanu ");
    }

    private void slowPrint(String t) {
        for (char c : t.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}