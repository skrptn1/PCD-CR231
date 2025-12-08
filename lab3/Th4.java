package lab3;

import javax.swing.*;

public class Th4 extends Thread {
    private int[] mas;
    private JTextArea ta;
    private Thread t3;

    public Th4(int[] mas, JTextArea ta, Thread t3) {
        super("Th4");
        this.mas = mas;
        this.ta = ta;
        this.t3 = t3;
    }

    @Override
    public void run() {
        try {
            t3.join();
        } catch (InterruptedException e) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Th4 – Parcurgere de la sfârșit interval [213, 899]:\n");

        int count = 0;
        for (int i = mas.length - 1; i >= 0; i--) {
            int v = mas[i];
            if (v >= 213 && v <= 899) {
                sb.append(String.format("%-6d", v));
                count++;
                if (count % 50 == 0) sb.append("\n");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
                Thread.yield();
            }
        }
        if (count % 10 != 0) sb.append("\n");

        ta.append(sb.toString());
    }

    public void afiseazaGrupa(JTextArea ta) {
        String text = "Grupa: CR231\n";
        for (char c : text.toCharArray()) {
            ta.append(String.valueOf(c));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
