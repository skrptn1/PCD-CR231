package lab2;

import javax.swing.*;

class Th2 extends Thread {
    private final int[] mas;
    private final JTextArea textArea;

    public Th2(int[] mas, JTextArea textArea) {
        this.mas = mas;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        int suma = 0;
        int perechi = 0;

        int i = mas.length - 1;
        while (i >= 0 && mas[i] % 2 == 0) i--;

        while (i > 0) {
            int j = i - 1;
            while (j >= 0 && mas[j] % 2 == 0) j--;

            if (j >= 0) {
                int p = mas[i] * mas[j];
                suma += p;
                perechi++;
                appendText(getName() + ": " + mas[i] + " * " + mas[j] + " = " + p + "\n");

                if (perechi % 2 == 0) {
                    appendText(getName() + " Suma după 2 produse = " + suma + "\n\n");
                    suma = 0;
                }
            }

            i = j;
        }
    }

    private void appendText(String s) {
        SwingUtilities.invokeLater(() -> textArea.append(s));
    }
}

