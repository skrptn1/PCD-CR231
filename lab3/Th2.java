package lab3;
public class Th2 extends Thread {

    int[] a;
    Th1 th1;

    public Th2(int[] a, Th1 th1) {
        this.a = a;
        this.th1 = th1;
    }

    @Override
    public void run() {

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
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}