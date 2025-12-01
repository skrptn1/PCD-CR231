package lab3;

import javax.swing.*;

public class Th4 extends Thread {
    private int[] mas;
    private JTextArea ta;
    private Thread t3;

    public Th4(int[] mas, JTextArea ta, Thread t3) {
        this.mas = mas;
        this.ta = ta;
        this.t3 = t3;
    }

    @Override
    public void run() {
        try {
            t3.join();
        } catch (InterruptedException e) {
            ta.append("Th4 întrerupt!\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Th4 – Parcurgere de la sfârșit interval [213, 899]:\n");

        for (int i = mas.length - 1; i >= 0; i--) {
            int v = mas[i];
            if (v >= 213 && v <= 899) {
                sb.append(v).append("\n");
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                Thread.yield();
            }
        }

        sb.append("Grupa: CR231\n");
        sb.append("Th4 a terminat.\n\n");

        ta.append(sb.toString());
    }
}
