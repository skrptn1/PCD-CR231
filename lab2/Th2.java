package lab2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class Th2 extends Thread {
    private int[] mas;
    private int sumResult;
    private JTextArea outputArea;

    public Th2(int[] mas, JTextArea outputArea) {
        this.mas = mas;
        this.outputArea = outputArea;
        this.sumResult = 0;
    }

    @Override
    public void run() {
        List<Integer> listaImpare = gasesteNumereImpareInvers();
        calculeazaProduseInvers(listaImpare);
        appendText("\nTh2: Suma produselor numerelor impare două câte două (de la ultimul) = " + sumResult + "\n");
    }

    
    private List<Integer> gasesteNumereImpareInvers() {
        List<Integer> listaImpare = new ArrayList<>();
        for (int i = mas.length - 1; i >= 0; i--) {
            if (mas[i] % 2 != 0) {
                listaImpare.add(mas[i]);
            }
        }
        appendText("Th2: Numere impare (de la final spre început): " + listaImpare + "\n");
        return listaImpare;
    }

    
    private void calculeazaProduseInvers(List<Integer> listaImpare) {
        for (int i = 0; i < listaImpare.size() - 1; i += 2) {
            int num1 = listaImpare.get(i);
            int num2 = listaImpare.get(i + 1);
            int produs = num1 * num2;
            sumResult += produs;

            appendText("Th2: Pereche (" + num1 + "," + num2 + ") -> produs: " + produs + "\n");

            try {
                Thread.sleep(80); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    
    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> outputArea.append(text));
    }
}
