import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main extends JFrame {

    private JTextArea output;
    private JButton startBtn;

    public Main() {
        setTitle("Lucrare Laborator Thread-uri");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 16));

        startBtn = new JButton("Start Fire");
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));

        JScrollPane scroll = new JScrollPane(output);

        add(scroll, BorderLayout.CENTER);
        add(startBtn, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> pornesteFire());
    }

    private void pornesteFire() {
        output.setText("");

        int[] mas = new int[100];
        Random r = new Random();

        for (int i = 0; i < mas.length; i++) {
            mas[i] = r.nextInt(100) + 1;
        }

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
                } catch (InterruptedException ignored) {
                }
            }

        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

class Th1 extends Thread {
    private int[] mas;
    private JTextArea out;

    public Th1(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 0;

        for (int i = 0; i < mas.length - 1; i++) {
            if (mas[i] % 2 != 0 && mas[i + 1] % 2 != 0) {
                suma += mas[i] * mas[i + 1];
            }
        }

        int rezultat = suma;
        SwingUtilities.invokeLater(() ->
                out.append("TH1 (Condiția 1) = " + rezultat + "\n")
        );
    }
}

class Th2 extends Thread {
    private int[] mas;
    private JTextArea out;

    public Th2(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 0;

        for (int i = mas.length - 1; i > 0; i--) {
            if (mas[i] % 2 != 0 && mas[i - 1] % 2 != 0) {
                suma += mas[i] * mas[i - 1];
            }
        }

        int rezultat = suma;
        SwingUtilities.invokeLater(() ->
                out.append("TH2 (Condiția 2) = " + rezultat + "\n")
        );
    }
}

class Th3 extends Thread {
    private int[] mas;
    private JTextArea out;

    public Th3(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 0;

        for (int i = 0; i < mas.length - 1; i++) {
            if (mas[i] % 2 != 0 && mas[i + 1] % 2 != 0) {
                suma += mas[i] * mas[i + 1];
            }
        }

        int rezultat = suma;
        SwingUtilities.invokeLater(() ->
                out.append("TH3 (Condiția 1) = " + rezultat + "\n")
        );
    }
}

class Th4 extends Thread {
    private int[] mas;
    private JTextArea out;

    public Th4(int[] mas, JTextArea out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int suma = 0;

        for (int i = mas.length - 1; i > 0; i--) {
            if (mas[i] % 2 != 0 && mas[i - 1] % 2 != 0) {
                suma += mas[i] * mas[i - 1];
            }
        }

        int rezultat = suma;
        SwingUtilities.invokeLater(() ->
                out.append("TH4 (Condiția 2) = " + rezultat + "\n")
        );
    }
}

