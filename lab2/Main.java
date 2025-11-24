package lab2;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


public class Main {
    static void main(String[] args) {
        int[] mas = new int[100];
        Random rand = new Random();
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < mas.length; i++) {
            mas[i] = rand.nextInt(100) + 1;
            sb.append(mas[i]).append(" ");
            if (i == 49) {
                sb.append("\n");
            }
        }

        JFrame frame = new JFrame("Lab2 - Sume produse numere impare");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane);
        frame.setVisible(true);

        textArea.append(sb.toString() + "\n\n");

        Thread th1 = new Th1(mas, textArea);
        th1.setName("Th1");

        Thread th2 = new Th2(mas, textArea);
        th2.setName("Th2");

        Thread th3 = new Th3(mas, textArea);
        th3.setName("Th3");

        Thread th4 = new Th4(mas, textArea);
        th4.setName("Thread4");

        th1.start();
        th2.start();
        th3.start();
        th4.start();

        try {
            th1.join();
            th2.join();
            th3.join();
            th4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String info = "Lucrarea a executat-o Pricop Alexandru, Bulrlea Vlad\n";
        for (char c : info.toCharArray()) {
            SwingUtilities.invokeLater(() -> {
                textArea.append(String.valueOf(c));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
