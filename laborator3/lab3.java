import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

public class lab3 {
    public static void main(String[] args) {
        new InterfataLab3();
    }
}

class InterfataLab3 extends JFrame {
    private JTextArea textArea;
    ExecutorService pool = Executors.newFixedThreadPool(4);

    public InterfataLab3() {
        setTitle("Laborator 3 Pool");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        setVisible(true);

        // ---------- TASK1 ----------
        Runnable t1 = () -> {
            int primulPar = 0;
            boolean gasit = false;
            int suma = 0;
            int count = 0;

            for (int i = 1; i <= 100; i++) {
                if (i % 2 == 0) {
                    if (!gasit) {
                        primulPar = i;
                        gasit = true;
                    } else {
                        int produs = primulPar * i;
                        append("T1: " + primulPar + " * " + i + " = " + produs + "\n");
                        suma += produs;
                        count++;

                        if (count == 2) {
                            append("T1 suma = " + suma + "\n\n");
                            suma = 0;
                            count = 0;
                        }

                        gasit = false;
                        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    }
                }
            }
        };

        // ---------- TASK2 ----------
        Runnable t2 = () -> {
            int primulPar = 0;
            boolean gasit = false;
            int suma = 0;
            int count = 0;

            for (int i = 100; i >= 1; i--) {
                if (i % 2 == 0) {
                    if (!gasit) {
                        primulPar = i;
                        gasit = true;
                    } else {
                        int produs = primulPar * i;
                        append("T2: " + primulPar + " * " + i + " = " + produs + "\n");
                        suma += produs;
                        count++;

                        if (count == 2) {
                            append("T2 suma = " + suma + "\n\n");
                            suma = 0;
                            count = 0;
                        }

                        gasit = false;
                        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    }
                }
            }
        };

        // ---------- TASK3 ----------
        Runnable t3 = () -> {
            append("\nPornire T3\n");
            for (int i = 234; i <= 1000; i++) {
                append("T3 " + i + " ");
                try { Thread.sleep(2); } catch (InterruptedException ignored) {}
            }
        };

        // ---------- TASK4 ----------
        Runnable t4 = () -> {
            append("\nPornire T4\n");
            for (int i = 1234; i >= 456; i--) {
                append("T4 " + i + " ");
                if (i % 20 == 0) append("\n");

                try { Thread.sleep(2); } catch (InterruptedException ignored) {}
            }
        };


        try {
            Future<?> f1 = pool.submit(t1);
            Future<?> f2 = pool.submit(t2);

            f1.get();
            f2.get();

            Future<?> f3 = pool.submit(t3);
            Future<?> f4 = pool.submit(t4);

            f4.get();

            append("\nT2: Furtuna Maletchi\n");
            append("\nT4: CR-231\n");
            append("\nT1: Radu Mirela\n");
            append("\nT3: Programarea Concurenta si Distribuita\n");

        } catch (Exception e) {
            append("Eroare execuție!");
        }

        pool.shutdown();
    }

    private void append(String text) {
        SwingUtilities.invokeLater(() -> textArea.append(text));
    }
}
