package lab3;

import javax.swing.*;

public class Th4 extends Thread {
    private int[] mas;
    private JTextArea ta;
    private Thread t2;

    public Th4(int[] mas, JTextArea ta, Thread t2) {
        super("Th4");
        this.mas = mas;
        this.ta = ta;
        this.t2 = t2;
    }

    @Override
    public void run() {


        StringBuilder sb = new StringBuilder();
        sb.append("Th4 – Parcurgere de la sfârșit interval [213, 899]:\n");

        int count = 0;
        for (int i = mas.length - 1; i >= 0; i--) {
            int v = mas[i];
            if (v >= 213 && v <= 899) {
                sb.append(String.format("%-6d", v));
                count++;
                if (count % 50 == 0) sb.append("\n");
            }
        }
        if (count % 10 != 0) sb.append("\n");

        appendText(sb.toString());
        try {
            t2.join();
        } catch (InterruptedException ignored) {
        }
        afiseazaGrupa();
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> ta.append(text));
    }

    private void afiseazaGrupa() {
        String text = "Grupa: CR231";
        for (char c : text.toCharArray()) {
            appendText(String.valueOf(c));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
