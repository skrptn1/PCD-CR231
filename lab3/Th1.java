package lab3;
public class Th1 extends Thread {

    int[] a;

    public Th1(int[] a) {
        this.a = a;
    }

    @Override
    public void run() {

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