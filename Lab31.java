import javax.swing.*;

public class Lab31 {
    private static final String SURNAME    = "Untila,Mocreac";
    private static final String GROUP      = "CR-231";
    private static final String FIRST_NAME = "Maxim,Cristian";
    private static final String DISCIPLINA = "Programarea concurenta si distributiva";

    private static JTextArea area;

    private static void append(String s) {
        if (SwingUtilities.isEventDispatchThread()) {
            area.append(s);
            area.setCaretPosition(area.getDocument().getLength());
        } else {
            SwingUtilities.invokeLater(() -> {
                area.append(s);
                area.setCaretPosition(area.getDocument().getLength());
            });
        }
    }

    private static void printWithDelay(String prefix, String text) {
        append(prefix);
        for (char ch : text.toCharArray()) {
            append(String.valueOf(ch));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        append("\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lab 3 - Sincronizare cu metodele clasei Thread (GUI)");
            frame.setSize(1100, 780);

            area = new JTextArea();
            area.setEditable(false);
            area.setLineWrap(false);

            JScrollPane sp = new JScrollPane(area);
            sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            frame.setContentPane(sp);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            new Thread(Lab31::runLab, "Controller").start();
        });
    }

    private static void runLab() {
        int[] a = new int[100];
        for (int i = 0; i < a.length; i++) a[i] = i + 1;

        append("Lucrarea de laborator nr. 3\n");
        append("Tema: Sincronizarea firelor de executie utilizand metodele clasei Thread\n\n");

        append("Tablou a[100]:\n");
        for (int i = 0; i < a.length; i++) {
            append(a[i] + " ");
            if ((i + 1) % 30 == 0) append("\n");
        }
        append("\n\n");

        ThreadSarcina1 th1 = new ThreadSarcina1(a);
        ThreadSarcina2 th2 = new ThreadSarcina2(a, th1);

        ThreadInterval4 th4 = new ThreadInterval4(567, 1100);
        ThreadInterval3 th3 = new ThreadInterval3(567, 1002, th4);

        th1.setName("Th1");
        th2.setName("Th2");
        th3.setName("Th3");
        th4.setName("Th4");

        append("Starting Thread 1\n");
        th1.start();

        append("Starting Thread 2\n");
        th2.start();

        try {
            th2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        append("Starting Thread 3\n");
        th3.start();

        append("Starting Thread 4\n");
        th4.start();

        try {
            th3.join();
            th4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        append("\n");

        printWithDelay("Th2: ", SURNAME);
        printWithDelay("Th4: ", GROUP);
        printWithDelay("Th1: ", FIRST_NAME);
        printWithDelay("Th3: ", DISCIPLINA);
    }

    static class ThreadSarcina1 extends Thread {
        private final int[] data;

        ThreadSarcina1(int[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            append(getName() + ": Sarcina 1 - produse pe pozitii pare (1-based), de la inceput:\n");

            long suma = 0;
            int cnt = 0;

            for (int pos = 2; pos <= 98; pos += 2) {
                int i = pos - 1;
                int j = pos + 1;

                long prod = (long) data[i] * data[j];
                suma += prod;
                cnt++;

                append(getName() + ": (poz " + pos + ", " + (pos + 2) + ") : "
                        + data[i] + " * " + data[j] + " = " + prod + "\n");
            }

            append(getName() + ": Rezultat - suma produselor = " + suma + " (" + cnt + " perechi)\n\n");
        }
    }

    static class ThreadSarcina2 extends Thread {
        private final int[] data;
        private final Thread th1;

        ThreadSarcina2(int[] data, Thread th1) {
            this.data = data;
            this.th1 = th1;
        }

        @Override
        public void run() {
            while (th1.isAlive()) {
                try {
                    th1.join(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            append(getName() + ": Sarcina 2 - produse pe pozitii pare (1-based), de la sfarsit:\n");

            long suma = 0;
            int cnt = 0;

            for (int pos = 100; pos >= 4; pos -= 2) {
                int i = pos - 1;
                int j = pos - 3;

                long prod = (long) data[j] * data[i];
                suma += prod;
                cnt++;

                append(getName() + ": (poz " + (pos - 2) + ", " + pos + ") : "
                        + data[j] + " * " + data[i] + " = " + prod + "\n");
            }

            append(getName() + ": Rezultat - suma produselor = " + suma + " (" + cnt + " perechi)\n\n");
        }
    }

    static class ThreadInterval3 extends Thread {
        private final int start, end;
        private final ThreadInterval4 th4;

        ThreadInterval3(int start, int end, ThreadInterval4 th4) {
            this.start = start;
            this.end = end;
            this.th4 = th4;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            sb.append(getName()).append(": ");
            for (int i = start; i <= end; i++) {
                sb.append(i).append(' ');
            }
            sb.append('\n');
            append(sb.toString());

            th4.interrupt();
        }
    }

    static class ThreadInterval4 extends Thread {
        private final int start, end;

        ThreadInterval4(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignored) {
            }

            StringBuilder sb = new StringBuilder();
            sb.append(getName()).append(": ");
            for (int i = end; i >= start; i--) {
                sb.append(i).append(' ');
            }
            sb.append('\n');
            append(sb.toString());
        }
    }
}
