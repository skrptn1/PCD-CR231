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
        setTitle("Laborator 3 - Fire de executie");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JLabel status = new JLabel("Executia ruleaza...", JLabel.CENTER);
        add(status, BorderLayout.SOUTH);

        setVisible(true);

        // Creare fire
        Th1 fir1 = new Th1(textArea);
        Th2 fir2 = new Th2(textArea);
        Th3 t3 = new Th3(textArea);
        Th4 t4 = new Th4(textArea);

        // Pornire fire
        fir1.start();
        fir2.start();

        new Thread(() -> {
            try {
                fir1.join();
                fir2.join();

                t3.start();
                t3.join();

                t4.start();
                t4.join();

                // Afișare informații finale după toate firele
                SwingUtilities.invokeLater(() -> {
                    textArea.append("\n=== Informații finale ===\n");
                    textArea.append("Th3 → Disciplina: Programarea Concurentă și Distribuită\n");
                    textArea.append("Th4 → Grupa: CR-231\n");
                    textArea.append("===================================\n");
                    status.setText("Executia s-a incheiat.");
                });
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> status.setText("Executia a fost intrerupta!"));
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

class Th3 extends Thread {
    private JTextArea output;

    public Th3(JTextArea area) {
        this.output = area;
    }

    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Pornire Th3 ===\n");
        sb.append("De parcurs intervalul [234, 1000]:\n");

        int count = 0;
        for (int i = 234; i <= 1000; i++) {
            sb.append(i).append(" ");
            count++;
            if (count % 10 == 0) sb.append("\n"); // 10 numere pe rând
        }

        sb.append("\nTh3 a terminat parcurgerea intervalului [234, 1000].\n");

        SwingUtilities.invokeLater(() -> output.append(sb.toString()));
    }
}

class Th4 extends Thread {
    private JTextArea output;

    public Th4(JTextArea area) {
        this.output = area;
    }

    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Pornire Th4 ===\n");
        sb.append("De parcurs intervalul [456, 1234]:\n");

        int count = 0;
        for (int i = 1234; i >= 456; i--) {
            sb.append(i).append(" ");
            count++;
            if (count % 10 == 0) sb.append("\n"); // 10 numere pe rând
        }

        sb.append("\nTh4 a terminat parcurgerea intervalului [456, 1234].\n");

        SwingUtilities.invokeLater(() -> output.append(sb.toString()));
    }
}
