import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Lab4 extends JFrame {

    private JTextArea textArea;
    private Depozit depozit;

    public Lab4() {
        setTitle("Producer - Consumer");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");

        panel.add(start);
        panel.add(stop);
        add(panel, BorderLayout.SOUTH);

        start.addActionListener(e -> porneste());
        stop.addActionListener(e -> depozit.inchide());
    }

    private void porneste() {
        depozit = new Depozit(textArea);

        new Producator(depozit, 1).start();
        new Producator(depozit, 2).start();
        new Producator(depozit, 3).start();
        new Producator(depozit, 4).start();

        try { Thread.sleep(600);

        new Consumator(depozit, 1).start();
        new Consumator(depozit, 2).start();
        new Consumator(depozit, 3).start();
        } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab4().setVisible(true));
    }
}

/* ===================== DEPOZIT ===================== */
class Depozit {

    private char[] buffer = new char[10];
    private int count = 0;
    public volatile boolean inchis = false;
    private JTextArea log;

    public Depozit(JTextArea log) {
        this.log = log;
    }

    public synchronized void produce(char c1, char c2) throws InterruptedException {
        while (count + 2 > buffer.length && !inchis) {
            log("⚠ DEPOZIT PLIN – producatorul asteapta");
            wait();
        }
        if (inchis) return;

        buffer[count++] = c1;
        buffer[count++] = c2;
        notifyAll();
    }

    public synchronized char consume() throws InterruptedException {
        while (count == 0 && !inchis) {
            wait();
        }
        if (count == 0 && inchis) return 0;

        char c = buffer[--count];
        notifyAll();
        return c;
    }

    public synchronized void inchide() {
        inchis = true;
        notifyAll();
        log("=== DEPOZIT INCHIS ===");
    }

    public void log(String mesaj) {
        SwingUtilities.invokeLater(() ->
                log.append(mesaj + "\n")
        );
    }
}

/* ===================== PRODUCATOR ===================== */
class Producator extends Thread {

    private Depozit depozit;
    private int id;
    private Random r = new Random();
    private char[] vocale = {'A','E','I','O','U'};

    public Producator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!depozit.inchis) {
                char v1 = vocale[r.nextInt(vocale.length)];
                char v2 = vocale[r.nextInt(vocale.length)];

                depozit.produce(v1, v2);
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

    public Consumator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }

    @Override
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
