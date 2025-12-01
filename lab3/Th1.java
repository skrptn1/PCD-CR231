package lab3;

import javax.swing.*;

public class Th1 extends Thread {
    private int[] mas;
    private JTextArea ta;

    public Th1(int[] mas, JTextArea ta) {
        this.mas = mas;
        this.ta = ta;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("Th1 – Sarcina 1: Produsele numerelor impare două câte două (de la început)\n");

        for (int i = 0; i < mas.length - 1; i++) {
            if (mas[i] % 2 != 0) {
                int j = i + 1;
                while (j < mas.length && mas[j] % 2 == 0) j++;
                if (j < mas.length) {
                    sb.append(mas[i] + " * " + mas[j] + " = " + (mas[i]*mas[j]) + "\n");
                    i = j;
                    try { Thread.sleep(1); } catch (InterruptedException e) {
                        sb.append("Th1 întrerupt!\n");
                        return;
                    }
                    Thread.yield();
                }
            }
        }

        sb.append("Prenumele studentului este: Alexandru, Vlad\n");
        sb.append("Th1 a terminat.\n\n");

        ta.append(sb.toString());
    }
}
