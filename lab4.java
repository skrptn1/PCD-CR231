import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Lab4 extends JFrame {

    private JTextArea textArea;
    private JButton startBtn;
    private Depozit depozit;

    public Lab4() {
        setTitle("Producer - Consumer");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        startBtn = new JButton("Porneste");
        add(startBtn, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> porneste());

        setVisible(true);
    }

    void porneste() {
        startBtn.setEnabled(false);
        textArea.setText("");

        depozit = new Depozit(textArea);

        new Producator(depozit, 1).start();
        new Producator(depozit, 2).start();
        new Producator(depozit, 3).start();
        new Producator(depozit, 4).start();

        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            new Consumator(depozit, 1).start();
            new Consumator(depozit, 2).start();
            new Consumator(depozit, 3).start();
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Lab4::new);
    }
}


class Depozit {

    private char[] buffer = new char[10];
    private int count = 0;
    volatile boolean inchis = false;

    private boolean mesajPlinAfisat = false;
    private JTextArea out;

    Depozit(JTextArea out) {
        this.out = out;
    }

    
    synchronized int produce(char c1, char c2, char[] rezultat)
            throws InterruptedException {

        while (count == buffer.length && !inchis) {
            if (!mesajPlinAfisat) {
                log("[DEPOZIT PLIN] Productia este oprita");
                mesajPlinAfisat = true;
            }
            wait();
        }

        if (inchis) return 0;

     
        if (count <= buffer.length - 2) {
            buffer[count++] = c1;
            buffer[count++] = c2;
            rezultat[0] = c1;
            rezultat[1] = c2;
            mesajPlinAfisat = false;
            notifyAll();
            return 2;
        }

        
        buffer[count++] = c1;
        rezultat[0] = c1;
        mesajPlinAfisat = false;
        notifyAll();
        return 1;
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
        SwingUtilities.invokeLater(() -> out.append(msg + "\n"));
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

                char[] rezultat = new char[2];
                int produse = depozit.produce(v1, v2, rezultat);

                if (depozit.inchis || produse == 0) break;

                if (produse == 2) {
                    depozit.log("Producator " + id +
                            " a produs: " + rezultat[0] + " " + rezultat[1]);
                } else {
                    depozit.log("Producator " + id +
                            " a produs: " + rezultat[0] +
                            " (vocală pierdută: " + v2 + ")");
                }

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

                depozit.log("Consumator " + id +
                        " a consumat: " + v);

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
