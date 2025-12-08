import javax.swing.*;

public class Lab31 {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Lab 3 - Sarcina 1, 2, 3 și 4");
        frame.setSize(900, 700);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
<<<<<<< HEAD
=======
        
>>>>>>> 0631134a8044d623e109f81ef59b51dd987a5f19

        int[] a = new int[100];
        for (int i = 0; i < a.length; i++) {
            a[i] = i + 1;
        }

        StringBuilder sb = new StringBuilder("Tablou a[100]:\n");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]).append(' ');
            if ((i + 1) % 30 == 0) {
                sb.append('\n');
            }
        }
        sb.append("\n\n");
        String head = sb.toString();
        System.out.print(head);
        SwingUtilities.invokeLater(() -> textArea.append(head));

        // Text cerut în enunț:
        String descriere = 
            "După finalizarea realizării sarcinilor firelor de execuţie, " +
            "thread-ul Th2 va afişa Numele studentului care a efectuat lucrarea dată de laborator, " +
            "Th4 va afișa grupa, Th1 va afișa Prenumele studentului, Th3 va afișa denumirea disciplinei (pe lung). " +
            "Literele textului vor apărea pe ecran cu un interval de 100 milisecunde.\n\n";
        System.out.print(descriere);
        SwingUtilities.invokeLater(() -> textArea.append(descriere));

        // --- Obiect comun pentru sincronizare (Th3 & Th4) ---
        Object sync = new Object();

        // Fire pentru Sarcina 1 și 2
        ThreadSarcina1 th1 = new ThreadSarcina1(a, "Maxim", textArea);
        ThreadSarcina2 th2 = new ThreadSarcina2(a, "UNTILA", textArea, th1);

        th1.setName("Th1");
        th2.setName("Th2");

        // Fire pentru Sarcina 3 și 4
        String disciplina = "Programarea Concurenta Distribuita";
        String grupa = "CR-231";

        ThreadInterval3 th3 = new ThreadInterval3(567, 1002, disciplina, textArea, sync);
        ThreadInterval4 th4 = new ThreadInterval4(567, 1100, grupa, textArea, sync);

        th3.setName("Th3");
        th4.setName("Th4");

        // Pornim toate firele
        th1.start();
        th2.start();
        th3.start();
        th4.start();
    }
}

// ==============================
//       SARCINA 1 – Th1
// ==============================
class ThreadSarcina1 extends Thread {
    private int[] data;
    private String prenume;
    private JTextArea textArea;

    public ThreadSarcina1(int[] data, String prenume, JTextArea textArea) {
        this.data = data;
        this.prenume = prenume;
        this.textArea = textArea;
    }

    @Override
    public void run() {

        long sumaProd = 0;
        int cntPerechi = 0;

        StringBuilder sbLoc = new StringBuilder();
        sbLoc.append(getName())
             .append(": Sarcina 1 - produse pe poziții pare (1-based), de la început:\n");

        for (int i = 0; i < data.length - 1; i++) {
            if (i % 2 == 1) { // poziții pare 1-based
                for (int j = i + 1; j < data.length; j++) {
                    if (j % 2 == 1) {
                        long prod = (long) data[i] * data[j];
                        sumaProd += prod;
                        cntPerechi++;

                        String line = getName()
                                + ": (poz " + (i + 1) + ", " + (j + 1) + ") : "
                                + data[i] + " * " + data[j] + " = " + prod;
                        sbLoc.append(line).append("\n");
                        break;
                    }
                }
            }
        }

        sbLoc.append(getName())
             .append(": Rezultat - suma produselor = ")
             .append(sumaProd)
             .append(" (")
             .append(cntPerechi)
             .append(" perechi)\n\n");

        String out = sbLoc.toString();
        System.out.print(out);
        SwingUtilities.invokeLater(() -> textArea.append(out));

        // Mică pauză înainte de afișarea prenumelui
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Afișăm PRENUMELE cu litere la 100ms, dar pe un singur rând
        String prefix = getName() + ": Prenume student: ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));

        for (char ch : prenume.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            SwingUtilities.invokeLater(() -> textArea.append(s));
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.print("\n");
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}

// ==============================
//       SARCINA 2 – Th2
// ==============================
class ThreadSarcina2 extends Thread {
    private int[] data;
    private String nume;
    private JTextArea textArea;
    private ThreadSarcina1 t1;

    public ThreadSarcina2(int[] data, String nume, JTextArea textArea, ThreadSarcina1 t1) {
        this.data = data;
        this.nume = nume;
        this.textArea = textArea;
        this.t1 = t1;
    }

    @Override
    public void run() {

        long sumaProd = 0;
        int cntPerechi = 0;

        StringBuilder sbLoc = new StringBuilder();

        sbLoc.append(getName())
             .append(": Sarcina 2 - produse pe poziții pare (1-based), de la sfârșit:\n");

        for (int i = data.length - 1; i >= 1; i--) {
            if (i % 2 == 1) {
                for (int j = i - 1; j >= 1; j--) {
                    if (j % 2 == 1) {
                        long prod = (long) data[i] * data[j];
                        sumaProd += prod;
                        cntPerechi++;

                        String line = getName()
                                + ": (poz " + (j + 1) + ", " + (i + 1) + ") : "
                                + data[j] + " * " + data[i] + " = " + prod;
                        sbLoc.append(line).append("\n");
                        break;
                    }
                }
            }
        }

        sbLoc.append(getName())
             .append(": Rezultat - suma produselor = ")
             .append(sumaProd)
             .append(" (")
             .append(cntPerechi)
             .append(" perechi)\n\n");

        String out = sbLoc.toString();
        System.out.print(out);
        SwingUtilities.invokeLater(() -> textArea.append(out));

        // Sincronizare cu join() — Th2 așteaptă Th1
        try {
            t1.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Afișăm NUMELE cu litere la 100ms, pe un singur rând
        String prefix = getName() + ": Nume student: ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));

        for (char ch : nume.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            SwingUtilities.invokeLater(() -> textArea.append(s));
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.print("\n");
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}

// ==============================
//       SARCINA 3 – Th3
// ==============================
class ThreadInterval3 extends Thread {

    private int start;
    private int end;
    private String disciplina;
    private JTextArea textArea;
    private final Object sync;

    public ThreadInterval3(int start, int end, String disciplina, JTextArea textArea, Object sync) {
        this.start = start;
        this.end = end;
        this.disciplina = disciplina;
        this.textArea = textArea;
        this.sync = sync;
    }

    @Override
    public void run() {

        // Un singur rând cu toate valorile
        StringBuilder oneLine = new StringBuilder(getName() + ": ");

        for (int i = start; i <= end; i++) {
            oneLine.append(i).append(" ");
            try { Thread.sleep(3); } catch (Exception e) {}
        }

        oneLine.append("\n");
        String out = oneLine.toString();
        System.out.print(out);
        SwingUtilities.invokeLater(() -> textArea.append(out));

        // Așteptăm notificarea de la Th4
        synchronized (sync) {
            try {
                sync.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Afișăm DISCIPLINA cu litere la 100ms, pe un singur rând
        String prefix = getName() + ": Disciplina (pe lung): ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));

        for (char ch : disciplina.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            SwingUtilities.invokeLater(() -> textArea.append(s));
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.print("\n");
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}

// ==============================
//       SARCINA 4 – Th4
// ==============================
class ThreadInterval4 extends Thread {

    private int start;
    private int end;
    private String grupa;
    private JTextArea textArea;
    private final Object sync;

    public ThreadInterval4(int start, int end, String grupa, JTextArea textArea, Object sync) {
        this.start = start;
        this.end = end;
        this.grupa = grupa;
        this.textArea = textArea;
        this.sync = sync;
    }

    @Override
    public void run() {

        // Un singur rând cu toate valorile descrescător
        StringBuilder oneLine = new StringBuilder(getName() + ": ");

        for (int i = end; i >= start; i--) {
            oneLine.append(i).append(" ");
            try { Thread.sleep(3); } catch (Exception e) {}
        }

        oneLine.append("\n");
        String out = oneLine.toString();
        System.out.print(out);
        SwingUtilities.invokeLater(() -> textArea.append(out));

        // Notificăm Th3
        synchronized (sync) {
            sync.notify();
        }

        // Afișăm GRUPA cu litere la 100ms, pe un singur rând
        String prefix = getName() + ": Grupa: ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));

        for (char ch : grupa.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            SwingUtilities.invokeLater(() -> textArea.append(s));
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.print("\n");
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}
