package lab3;

import javax.swing.*;

class Th1 extends Thread {
    private int[] mas;
    private JTextArea taTh1;
    private boolean[] th1Flag;

    public Th1(int[] mas, JTextArea taTh1, boolean[] th1Flag) {
        this.mas = mas;
        this.taTh1 = taTh1;
        this.th1Flag = th1Flag;
    }

    @Override
    public void run() {
        taTh1.append("Th1 – Sarcina 1: Produsele numerelor impare două câte două (de la început)\n");

        for (int i = 0; i < mas.length - 1; i++) {
            if (mas[i] % 2 != 0) {
                int j = i + 1;
                while (j < mas.length && mas[j] % 2 == 0) j++;
                if (j < mas.length) {
                    int produs = mas[i] * mas[j];
                    taTh1.append(mas[i] + " * " + mas[j] + " = " + produs + "\n");
                    i = j;
                }
            }
        }

        th1Flag[0] = true;

        String prenume = "Alexandru";
        taTh1.append("Prenumele studentului este:\n");
        for (char c : prenume.toCharArray()) {
            taTh1.append(String.valueOf(c));
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }

        taTh1.append("\nTh1 a terminat.\n");
    }
}
