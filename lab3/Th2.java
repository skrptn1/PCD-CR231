package lab3;

import javax.swing.*;

class Th2 extends Thread {
    private int[] mas;
    private JTextArea taTh2;
    private boolean[] th1Flag;

    public Th2(int[] mas, JTextArea taTh2, boolean[] th1Flag) {
        this.mas = mas;
        this.taTh2 = taTh2;
        this.th1Flag = th1Flag;
    }

    @Override
    public void run() {
        // Așteaptă Th1
        while (!th1Flag[0]) {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        taTh2.append("Th2 – Sarcina 2: Produsele numerelor impare două câte două (de la sfârșit)\n");

        for (int i = mas.length - 1; i > 0; i--) {
            if (mas[i] % 2 != 0) {
                int j = i - 1;
                while (j >= 0 && mas[j] % 2 == 0) j--;
                if (j >= 0) {
                    int produs = mas[i] * mas[j];
                    taTh2.append(mas[i] + " * " + mas[j] + " = " + produs + "\n");
                    i = j; // sări peste elementul folosit
                }
            }
        }

        // Afișare nume student cu 100ms pauză între litere
        String nume = "Pricop";
        taTh2.append("Numele studentului este:\n");
        for (char c : nume.toCharArray()) {
            taTh2.append(String.valueOf(c));
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }

        taTh2.append("\nTh2 a terminat.\n");
    }
}
