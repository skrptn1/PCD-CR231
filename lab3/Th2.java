package lab3;

import javax.swing.*;

public class Th2 extends Thread {
    private int[] mas;
    private JTextArea ta;

    public Th2(int[] mas, JTextArea ta) {
        super("Th2");
        this.mas = mas;
        this.ta = ta;
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
                }
            }
        }

        appendText(sb.toString());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        afiseazaNumele();
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> ta.append(text));
    }

    private void afiseazaNumele() {
        String text = "Numele studentilor este: Pricop, Burlea\n";
        for (char c : text.toCharArray()) {
            appendText(String.valueOf(c));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
