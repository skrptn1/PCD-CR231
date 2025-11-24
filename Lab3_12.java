import javax.swing.*;

public class Lab3_12 {

    public static void main(String[] args) {

        // Fereastra cu JTextArea
        JFrame frame = new JFrame("Lab 3 - Sarcina 1 și 2");
        frame.setSize(800, 500);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Tablou a[100] cu valori 1..100
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

        // Creăm firele pentru sarcina 1 și 2
        ThreadSarcina1 th1 = new ThreadSarcina1(a, "Maxim", textArea);
        ThreadSarcina2 th2 = new ThreadSarcina2(a, "UNTILA", textArea, th1);
        th1.setBuddy(th2);

        th1.setName("Th1");
        th2.setName("Th2");

        th1.start();
        th2.start();
    }
}

// Th1 – Sarcina 1:
// Sumele produselor numerelor de pe poziții pare (1-based)
// două câte două, începând căutarea de la primul element.
class ThreadSarcina1 extends Thread {
    private int[] data;
    private String prenume;
    private JTextArea textArea;
    private ThreadSarcina2 buddy;  // Th2 – pentru sincronizare

    public ThreadSarcina1(int[] data, String prenume, JTextArea textArea) {
        this.data = data;
        this.prenume = prenume;
        this.textArea = textArea;
    }

    public void setBuddy(ThreadSarcina2 buddy) {
        this.buddy = buddy;
    }

    @Override
    public void run() {
        long sumaProd = 0;
        int cntPerechi = 0;

        StringBuilder sbLoc = new StringBuilder();

        // Linie de titlu
        sbLoc.append(getName())
             .append(": Sarcina 1 - produse pe poziții pare (1-based), de la început:\n");

        // Poziții pare 1-based => index % 2 == 1 (index 0-based)
        for (int i = 0; i < data.length - 1; i++) {
            if (i % 2 == 1) {
                for (int j = i + 1; j < data.length; j++) {
                    if (j % 2 == 1) {
                        long prod = (long) data[i] * data[j];
                        sumaProd += prod;
                        cntPerechi++;

                        String line = getName()
                                + ": (poz " + (i + 1) + ", " + (j + 1) + ") : "
                                + data[i] + " * " + data[j] + " = " + prod;
                        sbLoc.append(line).append("\n");

                        break; // trecem la următoarea pereche
                    }
                }
            }
        }

        sbLoc.append(getName())
             .append(": Rezultat - suma produselor = ")
             .append(sumaProd)
             .append(" (")
             .append(cntPerechi)
             .append(" perechi)\n");

        String out = sbLoc.toString();
        System.out.print(out);
        String finalOut = out;
        SwingUtilities.invokeLater(() -> textArea.append(finalOut));

        // Sincronizare cu Th2:
        // așteptăm ca Th2 să-și termine tot (inclusiv afișarea numelui)
        if (buddy != null) {
            while (buddy.isAlive()) {
                // a doua metodă de sincronizare pentru 1–2
                Thread.onSpinWait();
            }
        }

        // după ce Th2 a terminat, afișăm Prenumele, literă cu literă
        String prefix = getName() + ": Prenume student: ";
        System.out.print(prefix + "\n");
        SwingUtilities.invokeLater(() -> textArea.append(prefix + "\n"));

        for (char ch : prenume.toCharArray()) {
            String line = getName() + ": " + ch;
            System.out.println(line);
            String l = line + "\n";
            SwingUtilities.invokeLater(() -> textArea.append(l));
            try {
                Thread.sleep(100); // 100 ms între litere
            } catch (InterruptedException e) {
                // ignorăm
            }
        }
    }
}

// Th2 – Sarcina 2:
// Sumele produselor numerelor de pe poziții pare (1-based)
// două câte două, începând căutarea de la ultimul element.
class ThreadSarcina2 extends Thread {
    private int[] data;
    private String nume;
    private JTextArea textArea;
    private ThreadSarcina1 t1;  // pentru join

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

        // Parcurgem de la sfârșit spre început
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
             .append(" perechi)\n");

        String out = sbLoc.toString();
        System.out.print(out);
        String finalOut = out;
        SwingUtilities.invokeLater(() -> textArea.append(finalOut));

        // Sincronizare: Th2 așteaptă ca Th1 să-și termine calculele
        // (metoda join – prima metodă de sincronizare pentru 1–2)
        if (t1 != null) {
            try {
                t1.join();
            } catch (InterruptedException e) {
                // ignorăm
            }
        }

        // după ce sarcinile sunt gata, afișăm NUMELE studentului
        String prefix = getName() + ": Nume student: ";
        System.out.print(prefix + "\n");
        SwingUtilities.invokeLater(() -> textArea.append(prefix + "\n"));

        for (char ch : nume.toCharArray()) {
            String line = getName() + ": " + ch;
            System.out.println(line);
            String l = line + "\n";
            SwingUtilities.invokeLater(() -> textArea.append(l));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignorăm
            }
        }
    }
}
