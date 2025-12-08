import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Lab4 extends JFrame {

    private JTextArea textArea;
    private JButton startBtn, stopBtn;
    private Depozit depozit;
    private boolean pornit = false;

    public Lab4() {
        setTitle("Producer - Consumer");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel jos = new JPanel();
        startBtn = new JButton("Porneste");
        stopBtn = new JButton("Opreste");

        jos.add(startBtn);
        jos.add(stopBtn);
        add(jos, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> porneste());
        stopBtn.addActionListener(e -> opreste());

        stopBtn.setEnabled(false);
    }

    private void porneste() {
        if (pornit) return;
        pornit = true;

        textArea.setText("");
        depozit = new Depozit(textArea);

        Producator p1 = new Producator(depozit, 1);
        Producator p2 = new Producator(depozit, 2);
        Producator p3 = new Producator(depozit, 3);
        Producator p4 = new Producator(depozit, 4);

        Consumator c1 = new Consumator(depozit, 1);
        Consumator c2 = new Consumator(depozit, 2);
        Consumator c3 = new Consumator(depozit, 3);

        p1.start();
        p2.start();
        p3.start();
        p4.start();

        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            c1.start();
            c2.start();
            c3.start();
        }).start();

        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void opreste() {
        if (!pornit) return;
        depozit.inchide();
        pornit = false;

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab4().setVisible(true));
    }
}

/* ===================== DEPOZIT ===================== */
class Depozit {

    private char[] buffer = new char[10];
    private int count = 0;
    volatile boolean inchis = false;

    private boolean mesajPlinAfisat = false;
    private JTextArea out;

    Depozit(JTextArea out) {
        this.out = out;
    }

    synchronized void produce(char c1, char c2) throws InterruptedException {
        while (count + 2 > buffer.length && !inchis) {
            if (!mesajPlinAfisat) {
                log("[DEPOZIT PLIN] Productia este oprita");
                mesajPlinAfisat = true;
            }
            wait();
        }
        if (inchis) return;

        buffer[count++] = c1;
        buffer[count++] = c2;
        mesajPlinAfisat = false;
        notifyAll();
    }

    synchronized char consume() throws InterruptedException {
        while (count == 0 && !inchis) {
            wait();
        }
        if (count == 0 && inchis) return 0;

        char c = buffer[--count];
        notifyAll();
        return c;
    }

    synchronized void inchide() {
        inchis = true;
        notifyAll();
    }

    void log(String msg) {
        SwingUtilities.invokeLater(() ->
                out.append(msg + "\n")
        );
    }
}

/* ===================== PRODUCATOR ===================== */
class Producator extends Thread {

    private Depozit depozit;
    private int id;
    private Random r = new Random();
    private char[] vocale = {'A','E','I','O','U'};

    Producator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    public void run() {
        try {
            while (!depozit.inchis) {
                char v1 = vocale[r.nextInt(vocale.length)];
                char v2 = vocale[r.nextInt(vocale.length)];

                depozit.produce(v1, v2);
                if (depozit.inchis) break;

                depozit.log("Producator " + id + " a produs: " + v1 + " " + v2);
                sleep(200);
            }
        } catch (InterruptedException ignored) {}
    }
}

/* ===================== CONSUMATOR ===================== */
class Consumator extends Thread {

    private Depozit depozit;
    private int id;
    private int necesita = 3;
    private static int terminati = 0;

    Consumator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    public void run() {
        try {
            while (necesita > 0) {
                char v = depozit.consume();
                if (v == 0) break;

                depozit.log("Consumator " + id + " a consumat: " + v);
                necesita--;
                sleep(300);
            }

            synchronized (Consumator.class) {
                terminati++;
                if (terminati == 3) {
                    depozit.inchide();
                    depozit.log("=== TOTI CONSUMATORII AU TERMINAT ===");
                }
            }
        } catch (InterruptedException ignored) {}
    }
}
