package lab2;

import javax.swing.*;

class Th1 extends Thread {
    private final int[] mas;
    private final JTextArea out;

    public Th1(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 1;

        for (int i = 0; i < mas.length - 1; i++) {
            int a = mas[i];
            int b = mas[i + 2];

            if (a % 2 != 0 && b % 2 != 0) {
                int produs = a * b;
                suma += produs;

                int fa = a, fb = b, fp = produs;

                SwingUtilities.invokeLater(() ->
                        out.append("TH1: Pereche (" + fa + ", " + fb +
                                ") -> produs = " + fp + "\n")
                );

                try {
                    Thread.sleep(150);
                } catch (Exception ignored) {
                }
            }
        }

        int rezultatFinal = suma;
        SwingUtilities.invokeLater(() ->
                out.append("\nTH1: Suma totală = " + rezultatFinal + "\n\n")
        );
    }
}
