package lab2;

import javax.swing.*;

class Th3 extends Thread {
    private final int[] mas;
    private final JTextArea textArea;

    public Th3(int[] mas, JTextArea textArea) {
        this.mas = mas;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        int suma = 0;
        int perechi = 0;

        int i = 0;
        while (i < mas.length && mas[i] % 2 == 0) i++;

        while (i < mas.length - 1) {
            int j = i + 1;
            while (j < mas.length && mas[j] % 2 == 0) j++;

            if (j < mas.length) {
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
