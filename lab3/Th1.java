package lab3;
public class Th1 extends Thread {

<<<<<<< HEAD
    int[] a;

    public Th1(int[] a) {
        this.a = a;
=======
    int[] v;

    public Th1(int[] v) {
        this.v = v;
        setName("Th1");
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1
    }

    @Override
    public void run() {

<<<<<<< HEAD
        System.out.println("Th1 - Sume impare de la inceput:");

         int[] impare = new int[a.length];
        int k = 0;

        for (int i = 0; i < a.length; i++) {
            if (a[i] % 2 != 0) {
                impare[k++] = a[i];
            }
        }
        for (int i = 0; i + 3 < k; i += 4) {
            int n1 = impare[i];
            int n2 = impare[i + 1];
            int n3 = impare[i + 2];
            int n4 = impare[i + 3];
=======
        // Afișare interval
        System.out.println("Th1 parcurge intervalul [0..798] în ordine crescătoare:");
        for (int i = 0; i <= 798; i++) {
            System.out.print(i + " ");
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
        slowPrint("Vlad");
    }

    private void slowPrint(String t) {
        for (char c : t.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}
>>>>>>> 62ede6cb9861d76a12af8e16a55d8f3dd5eebfc1

            int s1 = n1 + n2;
            int s2 = n3 + n4;
            int rezultat = s1 + s2;

            System.out.println("Th1: (" + n1 + " + " + n2 + ") + (" + n3 + " + " + n4 + ") = " + rezultat);
        }
          
        try { Thread.sleep(300); } catch (Exception e) {}

        
        String prenume = "Vlad Maxim";
        for (char c : prenume.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
    }
}