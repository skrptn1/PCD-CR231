package lab2;

import javax.swing.*;

class Th3 extends Thread {
    private int[] mas;
    private JTextArea out;

    public Th3(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 0;

        for (int i = 0; i < mas.length - 1; i++) {
            int a = mas[i];
            int b = mas[i + 1];

            if (a % 2 != 0 && b % 2 != 0) {
                int produs = a * b;
                suma += produs;

                int finalA = a, finalB = b, finalProd = produs;

                SwingUtilities.invokeLater(() ->
                        out.append("TH3: Pereche (" + finalA + "," + finalB +
                                ") -> produs: " + finalProd + "\n")
                );

                try {
                    Thread.sleep(150);
                } catch (Exception ignored) {
                }
            }
        }

        int rezultatFinal = suma;
        SwingUtilities.invokeLater(() ->
                out.append("\nTH3: Suma produselor = " + rezultatFinal + "\n\n")
        );
    }
}
