package lab3;

import javax.swing.*;

public class Th3 extends Thread {
    private int[] mas;
    private JTextArea ta;

    public Th3(int[] mas, JTextArea ta) {
        this.mas = mas;
        this.ta = ta;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("Th3 – Parcurgere de la început interval [126, 987]:\n");

        for (int v : mas) {
            if (v >= 126 && v <= 987) {
                sb.append(currentThread().getName()+getName()).append("\n");
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                Thread.yield();
            }
        }

        sb.append("Disciplina: Programarea Paralelă și Distribuită\n");
        sb.append("Th3 a terminat.\n\n");

        ta.append(sb.toString());
    }
}
