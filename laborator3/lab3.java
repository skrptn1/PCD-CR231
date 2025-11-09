import javax.swing.*;
import java.awt.*;

public class lab3 {
    public static void main(String[] args) {
        new InterfataLab3();
    }
}

class InterfataLab3 extends JFrame {
    private JTextArea textArea;

    public InterfataLab3() {
        setTitle("Laborator 3");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JLabel status = new JLabel("Execuția rulează...", JLabel.CENTER);
        add(status, BorderLayout.SOUTH);

        setVisible(true);

        Th1 fir1 = new Th1(textArea);
        Th2 fir2 = new Th2(textArea);

        fir1.start();
        fir2.start();

        new Thread(() -> {
            try {
                fir1.join();
                fir2.join();
                SwingUtilities.invokeLater(() -> status.setText("Execuția s-a încheiat."));
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> status.setText("Execuția a fost întreruptă!"));
            }
        }).start();
    }
}


class Th1 extends Thread {
    private JTextArea output;

    public Th1(JTextArea area) {
        this.output = area;
    }

    public void run() {
        int primulPar = 0;
        boolean primulGasit = false;

        for (int i = 1; i <= 100; i++) {
            if (i % 2 == 0) {
                if (!primulGasit) {
                    primulPar = i;
                    primulGasit = true;
                } else {
                    int produs = primulPar * i;
                    appendText("Th1: " + primulPar + " * " + i + " = " + produs + "\n");
                    primulGasit = false;

                    Thread.yield();
                    try { Thread.sleep(150); } catch (InterruptedException e) { }
                }
            }
        }

        synchronized(System.out) {
            appendText("\nFir1: ");
            String name = "Rodion, Mirela";
            for (int i = 0; i < name.length(); i++) {
                appendText(String.valueOf(name.charAt(i)));
                try { Thread.sleep(100); } catch (InterruptedException e) { }
            }
            appendText("\n");
        }

        Thread.currentThread().interrupt();
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}

class Th2 extends Thread {
    private JTextArea output;

    public Th2(JTextArea area) {
        this.output = area;
    }

    public void run() {
        int primulPar = 0;
        boolean primulGasit = false;

        for (int i = 100; i >= 1; i--) {
            if (i % 2 == 0) {
                if (!primulGasit) {
                    primulPar = i;
                    primulGasit = true;
                } else {
                    int produs = primulPar * i;
                    appendText("Th2: " + primulPar + " * " + i + " = " + produs + "\n");
                    primulGasit = false;

                    Thread.yield();
                    try { Thread.sleep(150); } catch (InterruptedException e) { }
                }
            }

            if (Thread.interrupted()) {
                appendText("Th2 a fost întrerupt de Th1!\n");
                break;
            }
        }

        synchronized(System.out) {
             appendText("\nFir2: ");
            String name = "Furtuna, Maletchi";
            for (int i = 0; i < name.length(); i++) {
                appendText(String.valueOf(name.charAt(i)));
                try { Thread.sleep(100); } catch (InterruptedException e) { }
            }
            appendText("\n");
        }
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}
