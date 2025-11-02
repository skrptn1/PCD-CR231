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

public class MainSwing {
    public static void main(String[] args) {
        
        JFrame frame = new JFrame("Lucrare cu Threads și Swing - Condiția 2 (varianta 8)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane);
        frame.setVisible(true);

        
        int[] mas = new int[100];
        Random rnd = new Random();
        for (int i = 0; i < mas.length; i++) {
            mas[i] = rnd.nextInt(100) + 1;
        }

        
        Th2 t2 = new Th2(mas, textArea);
        t2.start();

        
        new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String info = "Lucrarea realizată de: Popescu Ion și Ionescu Maria";
            for (char c : info.toCharArray()) {
                SwingUtilities.invokeLater(() -> textArea.append(String.valueOf(c)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
