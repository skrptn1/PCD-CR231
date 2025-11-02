package lab2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class Th1 implements Runnable {
    private int[] mas;
    private int sumResult;
    private JTextArea outputArea;

    public Th1(int[] mas, JTextArea outputArea) {
        this.mas = mas;
        this.outputArea = outputArea;
        this.sumResult = 0;
    }


    @Override
    public void run() {
        List<Integer> listaImpare = gasesteNumereImpare();
        calculeazaProduse(listaImpare);
        appendText("\nTh1: Suma produselor numerelor impare două câte două = " + sumResult + "\n");
    }

    private List<Integer> gasesteNumereImpare() {
        List<Integer> listaImpare = new ArrayList<>();
        for (int num : mas) {
            if (num % 2 != 0) {
                listaImpare.add(num);
            }
        }
        appendText("Th1: Numere impare găsite: " + listaImpare + "\n");
        return listaImpare;
    }

    private void calculeazaProduse(List<Integer> listaImpare) {
        for (int i = 0; i < listaImpare.size() - 1; i += 2) {
            int num1 = listaImpare.get(i);
            int num2 = listaImpare.get(i + 1);
            int produs = num1 * num2;
            sumResult += produs;

            appendText("Th1: Pereche (" + num1 + "," + num2 + ") -> produs: " + produs + "\n");

            try {
                Thread.sleep(80); // doar pentru vizualizare treptată
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> outputArea.append(text));
    }
}
