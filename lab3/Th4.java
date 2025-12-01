package lab3;

import javax.swing.*;

public class Th4 extends Thread {

    private int[] mas;
    private JTextArea taTh4;
    private Th3 th3; 

    public Th4(int[] mas, JTextArea taTh4, Th3 th3) {
        this.mas = mas;
        this.taTh4 = taTh4;
        this.th3 = th3;
    }

    @Override
    public void run() {

        
        if (th3 != null && th3.isAlive()) {
            try {
                th3.join();  
            } catch (Exception ignored) {}
        }

        taTh4.append("Th4 – Parcurgere de la sfârșit interval [213, 899]:\n");

        
        for (int i = mas.length - 1; i >= 0; i--) {
            int v = mas[i];
            if (v >= 213 && v <= 899) {
                taTh4.append(v + "\n");
            }
        }

        
        taTh4.append("\nSarcina 4 realizată.\n");

        
        String grupa = "Grupa 2131";

        taTh4.append("\nGrupa:\n");

        for (char c : grupa.toCharArray()) {
            taTh4.append(String.valueOf(c));
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }

        taTh4.append("\nTh4 a terminat.\n");
    }
}


