import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Lab31 {

    // Append în JTextArea în ordine (fără “amestecare”)
    static void appendArea(JTextArea area, String text) {
        if (area == null) return;
        if (SwingUtilities.isEventDispatchThread()) {
            area.append(text);
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> area.append(text));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> area.append(text));
            }
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Lab 3 - Sarcina 1, 2, 3 și 4");
        frame.setSize(900, 700);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        int[] a = new int[100];
        for (int i = 0; i < a.length; i++) {
            a[i] = i + 1;
        }

        StringBuilder sb = new StringBuilder("Tablou a[100]:\n");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]).append(' ');
            if ((i + 1) % 30 == 0) sb.append('\n');
        }
        sb.append("\n\n");

        String head = sb.toString();
        System.out.print(head);
        appendArea(textArea, head);

        String descriere =
                "După finalizarea realizării sarcinilor firelor de execuţie, " +
                "thread-ul Th2 va afişa Numele studentului care a efectuat lucrarea dată de laborator, " +
                "Th4 va afișa grupa, Th1 va afișa Prenumele studentului, Th3 va afișa denumirea disciplinei (pe lung). " +
                "Literele textului vor apărea pe ecran cu un interval de 100 milisecunde.\n\n";
        System.out.print(descriere);
        appendArea(textArea, descriere);

        // 1) Barieră: toate thread-urile termină “sarcinile” (output numeric) înainte de textele finale
        CountDownLatch tasksDone = new CountDownLatch(4);

        // 2) Ordine finală: Th2 -> Th4 -> Th1 -> Th3
        Semaphore goTh4 = new Semaphore(0);
        Semaphore goTh1 = new Semaphore(0);
        Semaphore goTh3 = new Semaphore(0);

        // Textele cerute (MODIFICATE conform cerinței tale)
        String disciplina = "Programarea Concurenta si Distributiva";
        String grupa = "CR-231";

        ThreadSarcina1 th1 = new ThreadSarcina1(a, "Maxim", textArea, tasksDone, goTh1, goTh3);
        ThreadSarcina2 th2 = new ThreadSarcina2(a, "UNTILA", textArea, tasksDone, goTh4);

        ThreadInterval3 th3 = new ThreadInterval3(567, 1002, disciplina, textArea, tasksDone, goTh3);
        ThreadInterval4 th4 = new ThreadInterval4(567, 1100, grupa, textArea, tasksDone, goTh4, goTh1);

        th1.setName("Th1");
        th2.setName("Th2");
        th3.setName("Th3");
        th4.setName("Th4");

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
    private final int[] data;
    private final String prenume;
    private final JTextArea textArea;

    private final CountDownLatch tasksDone;
    private final Semaphore myTurn;
    private final Semaphore nextTurn;

    public ThreadSarcina1(int[] data, String prenume, JTextArea textArea,
                          CountDownLatch tasksDone, Semaphore myTurn, Semaphore nextTurn) {
        this.data = data;
        this.prenume = prenume;
        this.textArea = textArea;
        this.tasksDone = tasksDone;
        this.myTurn = myTurn;
        this.nextTurn = nextTurn;
    }

    @Override
    public void run() {
        long sumaProd = 0;
        int cntPerechi = 0;

        StringBuilder sbLoc = new StringBuilder();
        sbLoc.append(getName())
                .append(": Sarcina 1 - produse pe poziții pare (1-based), de la început:\n");

        // perechi: (2,4), (4,6), ... (98,100)
        for (int i = 1; i <= 97; i += 2) {
            int j = i + 2;
            long prod = (long) data[i] * data[j];
            sumaProd += prod;
            cntPerechi++;

            sbLoc.append(getName())
                    .append(": (poz ").append(i + 1).append(", ").append(j + 1).append(") : ")
                    .append(data[i]).append(" * ").append(data[j]).append(" = ").append(prod)
                    .append("\n");
        }

        sbLoc.append(getName())
                .append(": Rezultat - suma produselor = ")
                .append(sumaProd)
                .append(" (").append(cntPerechi).append(" perechi)\n\n");

        String out = sbLoc.toString();
        System.out.print(out);
        Lab31.appendArea(textArea, out);

        tasksDone.countDown();
        try { tasksDone.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // așteaptă rândul: Th4 -> Th1
        try { myTurn.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // PRENUME cu 100ms/literă
        String prefix = getName() + ": Prenume student: ";
        System.out.print(prefix);
        Lab31.appendArea(textArea, prefix);

        for (char ch : prenume.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            Lab31.appendArea(textArea, s);
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }

        System.out.print("\n");
        Lab31.appendArea(textArea, "\n");

        // dă drumul la Th3
        nextTurn.release();
    }
}

// ==============================
//       SARCINA 2 – Th2
// ==============================
class ThreadSarcina2 extends Thread {
    private final int[] data;
    private final String nume;
    private final JTextArea textArea;

    private final CountDownLatch tasksDone;
    private final Semaphore nextTurn; // pornește Th4

    public ThreadSarcina2(int[] data, String nume, JTextArea textArea,
                          CountDownLatch tasksDone, Semaphore nextTurn) {
        this.data = data;
        this.nume = nume;
        this.textArea = textArea;
        this.tasksDone = tasksDone;
        this.nextTurn = nextTurn;
    }

    @Override
    public void run() {
        long sumaProd = 0;
        int cntPerechi = 0;

        StringBuilder sbLoc = new StringBuilder();
        sbLoc.append(getName())
                .append(": Sarcina 2 - produse pe poziții pare (1-based), de la sfârșit:\n");

        // perechi: (98,100), (96,98), ... (2,4)
        for (int i = 99; i >= 3; i -= 2) {
            int j = i - 2;
            long prod = (long) data[j] * data[i];
            sumaProd += prod;
            cntPerechi++;

            sbLoc.append(getName())
                    .append(": (poz ").append(j + 1).append(", ").append(i + 1).append(") : ")
                    .append(data[j]).append(" * ").append(data[i]).append(" = ").append(prod)
                    .append("\n");
        }

        sbLoc.append(getName())
                .append(": Rezultat - suma produselor = ")
                .append(sumaProd)
                .append(" (").append(cntPerechi).append(" perechi)\n\n");

        String out = sbLoc.toString();
        System.out.print(out);
        Lab31.appendArea(textArea, out);

        tasksDone.countDown();
        try { tasksDone.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // NUME cu 100ms/literă (primul în ordinea finală)
        String prefix = getName() + ": Nume student: ";
        System.out.print(prefix);
        Lab31.appendArea(textArea, prefix);

        for (char ch : nume.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            Lab31.appendArea(textArea, s);
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }

        System.out.print("\n");
        Lab31.appendArea(textArea, "\n");

        // dă drumul la Th4
        nextTurn.release();
    }
}

// ==============================
//       SARCINA 3 – Th3
// ==============================
class ThreadInterval3 extends Thread {
    private final int start;
    private final int end;
    private final String disciplina;
    private final JTextArea textArea;

    private final CountDownLatch tasksDone;
    private final Semaphore myTurn;

    public ThreadInterval3(int start, int end, String disciplina, JTextArea textArea,
                           CountDownLatch tasksDone, Semaphore myTurn) {
        this.start = start;
        this.end = end;
        this.disciplina = disciplina;
        this.textArea = textArea;
        this.tasksDone = tasksDone;
        this.myTurn = myTurn;
    }

    @Override
    public void run() {
        StringBuilder oneLine = new StringBuilder(getName() + ": ");
        for (int i = start; i <= end; i++) {
            oneLine.append(i).append(" ");
            try { Thread.sleep(3); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
        oneLine.append("\n");

        String out = oneLine.toString();
        System.out.print(out);
        Lab31.appendArea(textArea, out);

        tasksDone.countDown();
        try { tasksDone.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // așteaptă rândul: Th1 -> Th3 (ultimul)
        try { myTurn.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        String prefix = getName() + ": Disciplina (pe lung): ";
        System.out.print(prefix);
        Lab31.appendArea(textArea, prefix);

        for (char ch : disciplina.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            Lab31.appendArea(textArea, s);
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }

        System.out.print("\n");
        Lab31.appendArea(textArea, "\n");
    }
}

// ==============================
//       SARCINA 4 – Th4
// ==============================
class ThreadInterval4 extends Thread {
    private final int start;
    private final int end;
    private final String grupa;
    private final JTextArea textArea;

    private final CountDownLatch tasksDone;
    private final Semaphore myTurn;   // pornește după Th2
    private final Semaphore nextTurn; // pornește Th1

    public ThreadInterval4(int start, int end, String grupa, JTextArea textArea,
                           CountDownLatch tasksDone, Semaphore myTurn, Semaphore nextTurn) {
        this.start = start;
        this.end = end;
        this.grupa = grupa;
        this.textArea = textArea;
        this.tasksDone = tasksDone;
        this.myTurn = myTurn;
        this.nextTurn = nextTurn;
    }

    @Override
    public void run() {
        StringBuilder oneLine = new StringBuilder(getName() + ": ");
        for (int i = end; i >= start; i--) {
            oneLine.append(i).append(" ");
            try { Thread.sleep(3); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
        oneLine.append("\n");

        String out = oneLine.toString();
        System.out.print(out);
        Lab31.appendArea(textArea, out);

        tasksDone.countDown();
        try { tasksDone.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // așteaptă rândul: Th2 -> Th4
        try { myTurn.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // GRUPA (exact: "Grupa:CR-231")
        String prefix = getName() + ": Grupa:";
        System.out.print(prefix);
        Lab31.appendArea(textArea, prefix);

        for (char ch : grupa.toCharArray()) {
            String s = String.valueOf(ch);
            System.out.print(s);
            Lab31.appendArea(textArea, s);
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }

        System.out.print("\n");
        Lab31.appendArea(textArea, "\n");

        // dă drumul la Th1
        nextTurn.release();
    }
}
