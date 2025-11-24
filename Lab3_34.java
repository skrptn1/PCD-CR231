import javax.swing.*;

public class Lab3_34 {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Lab 3 - Sarcina 3 și 4");
        frame.setSize(800, 600);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Date cerute în temă
        String disciplina = "Programarea Concurenta Distribuita";
        String grupa = "CR-231";

        // Thread-uri pentru sarcina 3 și 4
        ThreadInterval3 th3 = new ThreadInterval3(567, 1002, disciplina, textArea);
        ThreadInterval4 th4 = new ThreadInterval4(567, 1100, grupa, textArea, th3);

        th3.setBuddy(th4); // sincronizare

        th3.setName("Th3");
        th4.setName("Th4");

        th3.start();
        th4.start();
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
    private ThreadInterval4 buddy;

    public ThreadInterval3(int start, int end, String disciplina, JTextArea textArea) {
        this.start = start;
        this.end = end;
        this.disciplina = disciplina;
        this.textArea = textArea;
    }

    public void setBuddy(ThreadInterval4 buddy) {
        this.buddy = buddy;
    }

    @Override
    public void run() {

        String hdr = getName() + ": Sarcina 3 - Interval [" + start + " .. " + end + "] (înainte)\n";
        System.out.print(hdr);
        SwingUtilities.invokeLater(() -> textArea.append(hdr));

        // Afișare NUMĂR CU NUMĂR, fiecare pe linie nouă
        for (int i = start; i <= end; i++) {
            String tok = getName() + ": " + i + "\n";

            System.out.print(tok);
            String t = tok;

            SwingUtilities.invokeLater(() -> textArea.append(t));

            try { Thread.sleep(5); } catch (Exception e) {}
        }

        // Așteptăm Th4 să termine parcurgerea intervalului + afișarea grupei
        while (buddy.isAlive()) {
            Thread.yield();   // metoda Thread – sincronizare 3–4
        }

        // După ce Th4 termină → afișăm disciplina (pe lung), literă cu literă
        String title = getName() + ": Disciplina (pe lung):\n";
        System.out.print(title);
        SwingUtilities.invokeLater(() -> textArea.append(title));

        for (char ch : disciplina.toCharArray()) {
            String line = getName() + ": " + ch;
            System.out.println(line);
            String l = line + "\n";
            SwingUtilities.invokeLater(() -> textArea.append(l));

            try { Thread.sleep(100); } catch (Exception e) {}
        }
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
    private ThreadInterval3 t3;

    public ThreadInterval4(int start, int end, String grupa, JTextArea textArea, ThreadInterval3 t3) {
        this.start = start;
        this.end = end;
        this.grupa = grupa;
        this.textArea = textArea;
        this.t3 = t3;
    }

    @Override
    public void run() {

        String hdr = getName() + ": Sarcina 4 - Interval [" + start + " .. " + end + "] (înapoi)\n";
        System.out.print(hdr);
        SwingUtilities.invokeLater(() -> textArea.append(hdr));

        // Afișare înapoi, fiecare număr pe rând nou, prefixat cu Th4:
        for (int i = end; i >= start; i--) {

            String tok = getName() + ": " + i + "\n";

            System.out.print(tok);
            SwingUtilities.invokeLater(() -> textArea.append(tok));

            try { Thread.sleep(5); } catch (Exception e) {}
        }

        // Sincronizare cu Th3: așteptăm Th3 să termine intervalul
        try { t3.join(); } catch (Exception e) {}

        // După ce ambele au terminat intervalele → afișăm grupa
        String title = getName() + ": Grupa:\n";
        System.out.print(title);
        SwingUtilities.invokeLater(() -> textArea.append(title));

        for (char ch : grupa.toCharArray()) {
            String line = getName() + ": " + ch;
            System.out.println(line);
            String l = line + "\n";
            SwingUtilities.invokeLater(() -> textArea.append(l));

            try { Thread.sleep(100); } catch (Exception e) {}
        }
    }
}
