package lab2;

import javax.swing.*;

class Th4 extends Thread {
    private int[] mas;
    private JTextArea out;

    public Th4(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 0;

        for (int i = mas.length - 1; i > 0; i--) {
            int a = mas[i];
            int b = mas[i - 1];

            if (a % 2 != 0 && b % 2 != 0) {
                int produs = a * b;
                suma += produs;

                int fa = a, fb = b, fp = produs;

                SwingUtilities.invokeLater(() ->
                        out.append("TH4 (C2): (" + fa + ", " + fb + ") -> produs = " + fp + "\n")
                );

                try {
                    Thread.sleep(150);
                } catch (Exception ignored) {
                }
            }
        }

        int rezultat = suma;
        SwingUtilities.invokeLater(() ->
                out.append("\nTH4: Suma totală = " + rezultat + "\n\n")
        );
    }
}
