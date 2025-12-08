package lab3;

import javax.swing.*;

public class Th2 extends Thread {
    private int[] mas;
    private JTextArea ta;
    private Thread t4;

    public Th2(int[] mas, JTextArea ta, Thread t4) {
        super("Th2");
        this.mas = mas;
        this.ta = ta;
        this.t4 = t4;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("Th2 – Sarcina 2: Produsele numerelor impare două câte două (de la sfârșit)\n");

        for (int i = mas.length - 1; i > 0; i--) {
            if (mas[i] % 2 != 0) {
                int j = i - 1;
                while (j >= 0 && mas[j] % 2 == 0) j--;
                if (j >= 0) {
                    sb.append(currentThread().getName() + " " + mas[i] + " * " + mas[j] + " = " + (mas[i] * mas[j]) + "\n");
                    i = j;
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                    Thread.yield();
                }
            }
        }

        while (t4.isAlive()) {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }

        ta.append(sb.toString());
    }

    public void afiseazaNumele(JTextArea ta) {
        String text = "Numele studentilor este: Pricop, Burlea\n";
        for (char c : text.toCharArray()) {
            ta.append(String.valueOf(c));
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }

}
