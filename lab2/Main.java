package lab2;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {
    private static JTextArea textArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createUI);
    }

    private static void createUI() {
        JFrame frame = new JFrame("Lab2 - Threads Th1 și Th2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);

        ruleazaThreaduri();
    }

    private static void ruleazaThreaduri() {
        
        int[] mas = new int[100];
        Random random = new Random();
        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
        }

        
        for (int num : mas) {
            textArea.append(num + " ");
        }
        textArea.append("\n\n");

        
        Th1 th1Runnable = new Th1(mas, textArea);
        Thread th1 = new Thread(th1Runnable);

        Th2 th2Thread = new Th2(mas, textArea);

        
        th1.start();
        th2Thread.start();

        
        new Thread(() -> {
            try {
                th1.join();
                th2Thread.join();
                afiseazaMesajFinal("Lucrarea a fost efectuată de Pricop Alexandru si Burlea Vlad.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void afiseazaMesajFinal(String text) {
        try {
            for (char c : text.toCharArray()) {
                SwingUtilities.invokeLater(() -> textArea.append(String.valueOf(c)));
                Thread.sleep(100);
            }
            SwingUtilities.invokeLater(() -> textArea.append("\n"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
