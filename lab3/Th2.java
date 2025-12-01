package lab3;

import javax.swing.*;

public class Th2 extends Thread {
    private int[] mas;
    private JTextArea ta;
    private Thread t1;

    public Th2(int[] mas, JTextArea ta, Thread t1) {
        this.mas = mas;
        this.ta = ta;
        this.t1 = t1;
    }

    @Override
    public void run() {
        try {
            t1.join();
        } catch (InterruptedException e) {
            ta.append("Th2 întrerupt!\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Th2 – Sarcina 2: Produsele numerelor impare două câte două (de la sfârșit)\n");

        for (int i = mas.length - 1; i > 0; i--) {
            if (mas[i] % 2 != 0) {
                int j = i - 1;
                while (j >= 0 && mas[j] % 2 == 0) j--;
                if (j >= 0) {
                    sb.append(mas[i] + " * " + mas[j] + " = " + (mas[i]*mas[j]) + "\n");
                    i = j;
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                    Thread.yield();
                }
            }
        }

        sb.append("Numele studentului este: Pricop, Burlea\n");
        sb.append("Th2 a terminat.\n\n");

        ta.append(sb.toString());
    }
}
