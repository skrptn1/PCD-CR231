package lab3;
public class Th2 extends Thread {

<<<<<<< HEAD
    int[] a;
    Th1 th1;

    public Th2(int[] a, Th1 th1) {
        this.a = a;
        this.th1 = th1;
=======
    int[] v;
    Thread t1;

    public Th2(int[] v, Thread t1) {
        this.v = v;
        this.t1 = t1;
        setName("Th2");
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
    }

    @Override
    public void run() {

<<<<<<< HEAD
        try { th1.join(); } catch (Exception e) {}

        System.out.println("Th2 - Sume impare de la sfarsit:");

        int[] impare = new int[a.length];
        int k = 0;

        for (int i = a.length - 1; i >= 0; i--) {
            if (a[i] % 2 != 0) {
                impare[k++] = a[i];
            }
        }

        for (int i = 0; i + 3 < k; i += 4) {
            int n1 = impare[i];
            int n2 = impare[i + 1];
            int n3 = impare[i + 2];
            int n4 = impare[i + 3];

            int s1 = n1 + n2;
            int s2 = n3 + n4;
            int rezultat = s1 + s2;

            System.out.println("Th2: (" + n1 + " + " + n2 + ") + (" + n3 + " + " + n4 + ") = " + rezultat);
        }

        String nume = "Ungureanu Munteanu";
        for (char c : nume.toCharArray()) {
=======
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
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}