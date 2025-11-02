package lab2;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


class Th2 extends Thread {
    int[] mas;
    JTextArea textArea;

    Th2(int[] mas, JTextArea textArea) {
        this.mas = mas;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        
        SwingUtilities.invokeLater(() ->
            textArea.append("Th2 - Condiția 2 (varianta 8):\n")
        );

        int sumaProduselor = 0;
        int count = 0;
        int produs = 1;

    
        for (int i = mas.length - 1; i >= 0; i--) {
            if (mas[i] % 2 != 0) {  
                produs *= mas[i];
                count++;
                if (count == 2) {  
                    sumaProduselor += produs;
                    produs = 1;
                    count = 0;
                }
            }
        }

        int rezultatFinal = sumaProduselor;

        
        SwingUtilities.invokeLater(() ->
            textArea.append("Suma produselor numerelor impare două câte două (de la ultimul): " + rezultatFinal + "\n\n")
        );
    }
}

