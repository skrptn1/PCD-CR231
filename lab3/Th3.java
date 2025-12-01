package lab3;

import javax.swing.*;

public class Th3 extends Thread {
    private int[] mas;
    private JTextArea ta;

    public Th3(int[] mas, JTextArea ta) {
        super("Th3");
        this.mas = mas;
        this.ta = ta;
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
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                Thread.yield();
            }
        }
        if (count % 10 != 0) sb.append("\n");

        ta.append(sb.toString());
    }

    public void afiseazaDisciplina(JTextArea ta) {
        String text = "Disciplina: Programarea Paralelă și Distribuită\n";
        for (char c : text.toCharArray()) {
            ta.append(String.valueOf(c));
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }

}
