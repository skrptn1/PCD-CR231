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
        setSize(600, 500);
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

        Th1 fir1 = new Th1(textArea);
        Th2 fir2 = new Th2(textArea);
        Th3 fir3 = new Th3(textArea);
        Th4 fir4 = new Th4(textArea);

        fir1.start();
        fir2.start();

        new Thread(() -> {
            try {
                fir1.join();
                fir2.join();

                fir3.start();
                fir3.join();

                fir4.start();
                fir4.join();

                SwingUtilities.invokeLater(() -> status.setText("Executia s-a incheiat."));
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
                appendText("Th2 a fost intrerupt de Th1!\n");
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

class Th3 extends Thread {
    private JTextArea output;

    public Th3(JTextArea area) {
        this.output = area;
    }

    public void run() {
        appendText("\n=== Pornire Th3: Interval [234, 1000] ===\n");
        int suma = 0;
        int contor = 0;
        int a = 0, b = 0;
        for (int i = 234; i <= 1000; i++) {
            if (i % 2 == 0) {
                contor++;
                if (contor == 1) a = i;
                if (contor == 2) {
                    b = i;
                    int produs = a * b;
                    suma += produs;
                    appendText("Th3: " + a + " * " + b + " = " + produs + "\n");
                    contor = 0;
                    try { Thread.sleep(100); } catch (InterruptedException e) { }
                }
            }
        }
        appendText("Th3 a terminat intervalul [234, 1000].\nSuma totala: " + suma + "\n\n");
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}

class Th4 extends Thread {
    private JTextArea output;

    public Th4(JTextArea area) {
        this.output = area;
    }

    public void run() {
        appendText("=== Pornire Th4: Interval [456, 1234] ===\n");
        int suma = 0;
        int contor = 0;
        int a = 0, b = 0;
        for (int i = 1234; i >= 456; i--) {
            if (i % 2 == 0) {
                contor++;
                if (contor == 1) a = i;
                if (contor == 2) {
                    b = i;
                    int produs = a * b;
                    suma += produs;
                    appendText("Th4: " + a + " * " + b + " = " + produs + "\n");
                    contor = 0;
                    try { Thread.sleep(100); } catch (InterruptedException e) { }
                }
            }
        }
        appendText("Th4 a terminat intervalul [456, 1234].\nSuma totala: " + suma + "\n\n");
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}
