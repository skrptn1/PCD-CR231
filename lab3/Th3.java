package lab3;

import javax.swing.*;

public class Th3 extends Thread {
    private int[] mas;
    private JTextArea ta;
    private Thread t1;

    public Th3(int[] mas, JTextArea ta, Thread t1) {
        super("Th3");
        this.mas = mas;
        this.ta = ta;
        this.t1 = t1;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("Th3 – Parcurgere de la început interval [126, 987]:\n");

        int count = 0;
        for (int v : mas) {
            if (v >= 126 && v <= 987) {
                sb.append(String.format("%-6d", v));
                count++;
                if (count % 50 == 0) sb.append("\n");
            }
        }
        if (count % 10 != 0) sb.append("\n");

        appendText(sb.toString());

        try {
            t1.join();
        } catch (InterruptedException ignored) {
        }

        afiseazaDisciplina();
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> ta.append(text));
    }

    private void afiseazaDisciplina() {
        String text = "Disciplina: Programarea Paralelă și Distribuită\n";
        for (char c : text.toCharArray()) {
            appendText(String.valueOf(c));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
