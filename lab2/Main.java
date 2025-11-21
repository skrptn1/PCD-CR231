package lab2;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main extends JFrame {

    private JTextArea output;

    public Main() {
        setTitle("Lucrare Laborator Thread-uri");
        setSize(650, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 15));

        JScrollPane scroll = new JScrollPane(output);
        add(scroll, BorderLayout.CENTER);

        // PORNEȘTE AUTOMAT
        pornesteFire();
    }

    private void pornesteFire() {
        output.setText("");

        int[] mas = new int[100];
        Random r = new Random();

        StringBuilder lista = new StringBuilder("Lista de numere generate:\n");
        for (int i = 0; i < mas.length; i++) {
            mas[i] = r.nextInt(100) + 1;
            lista.append(mas[i]).append(" ");
        }

        output.setText(lista.toString());

        Th1 t1 = new Th1(mas, output);
        Th2 t2 = new Th2(mas, output);
        Th3 t3 = new Th3(mas, output);
        Th4 t4 = new Th4(mas, output);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        new Thread(() -> {
            try {
                t1.join();
                t2.join();
                t3.join();
                t4.join();
            } catch (InterruptedException ignored) {
            }

            String text = "\nLucrarea a fost executată de Pricop Alexandru și Burlea Vlad";

            for (char c : text.toCharArray()) {
                SwingUtilities.invokeLater(() -> output.append(String.valueOf(c)));
                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
            }

        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
