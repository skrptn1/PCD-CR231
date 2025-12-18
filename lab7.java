import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class lab7 {

    private static final int FILOSOFI = 5;
    private static final int RUNDURI = 6;

    private static final Color CULOARE_INFOMETAT = new Color(255, 179, 71);
    private static final Color CULOARE_MANANCA = new Color(113, 201, 136);
    private static final Color CULOARE_MEDITARE = new Color(137, 196, 244);

    private static JLabel[] eticheteStare;
    private static JTextArea zonaLog;

    private static ReentrantLock[] furculite;
    private static Semaphore majordom;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(lab7::creeazaUI);
    }

    private static void creeazaUI() {
        JFrame frame = new JFrame("Problema Filozofilor - 3 stari");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 650);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel titlu = new JLabel("Filozofi: Infometat -> Mananca -> Mediteaza", SwingConstants.CENTER);
        titlu.setFont(new Font("SansSerif", Font.BOLD, 20));
        frame.add(titlu, BorderLayout.NORTH);

        JPanel panouFilozofi = new JPanel(new GridLayout(1, FILOSOFI, 10, 10));
        eticheteStare = new JLabel[FILOSOFI];

        for (int i = 0; i < FILOSOFI; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(new LineBorder(Color.DARK_GRAY, 2, true));

            JLabel nume = new JLabel("Filozof " + (i + 1), SwingConstants.CENTER);
            nume.setFont(new Font("SansSerif", Font.BOLD, 16));
            card.add(nume, BorderLayout.NORTH);

            JLabel stare = new JLabel("Mediteaza", SwingConstants.CENTER);
            stare.setOpaque(true);
            stare.setFont(new Font("Monospaced", Font.PLAIN, 14));
            stare.setBackground(CULOARE_MEDITARE);
            stare.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            eticheteStare[i] = stare;

            card.add(stare, BorderLayout.CENTER);
            panouFilozofi.add(card);
        }

        zonaLog = new JTextArea();
        zonaLog.setEditable(false);
        zonaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(zonaLog);

        frame.add(panouFilozofi, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth(), 250));

        JButton start = new JButton("Start simulare");
        start.addActionListener(e -> {
            start.setEnabled(false);
            pornescSimularea();
        });
        JPanel control = new JPanel();
        control.add(start);
        frame.add(control, BorderLayout.WEST);

        frame.setVisible(true);
    }

    private static void pornescSimularea() {
        log("\n=== Pornire simulare: Problema Filozofilor ===\n");

        furculite = new ReentrantLock[FILOSOFI];
        for (int i = 0; i < FILOSOFI; i++) {
            furculite[i] = new ReentrantLock(true);
        }

        majordom = new Semaphore(FILOSOFI - 1); // previne deadlock-ul clasic

        for (int i = 0; i < FILOSOFI; i++) {
            int stanga = i;
            int dreapta = (i + 1) % FILOSOFI;
            new Thread(new Filozof(i, furculite[stanga], furculite[dreapta], majordom), "Filozof-" + (i + 1)).start();
        }
    }

    private static void seteazaStare(int id, Stare stare) {
        JLabel et = eticheteStare[id];
        if (et == null) return;

        String text;
        Color culoare;
        switch (stare) {
            case INFOMETAT:
                text = "Infometat";
                culoare = CULOARE_INFOMETAT;
                break;
            case MANANCA:
                text = "Mananca";
                culoare = CULOARE_MANANCA;
                break;
            default:
                text = "Mediteaza";
                culoare = CULOARE_MEDITARE;
        }

        SwingUtilities.invokeLater(() -> {
            et.setText(text);
            et.setBackground(culoare);
        });
    }

    private static void log(String msg) {
        System.out.print(msg);
        SwingUtilities.invokeLater(() -> {
            zonaLog.append(msg);
            zonaLog.setCaretPosition(zonaLog.getDocument().getLength());
        });
    }

    enum Stare {
        INFOMETAT, MANANCA, MEDITARE
    }

    static class Filozof implements Runnable {
        private final int id;
        private final ReentrantLock furculitaStanga;
        private final ReentrantLock furculitaDreapta;
        private final Semaphore majordom;
        private final Random random = new Random();

        Filozof(int id, ReentrantLock furculitaStanga, ReentrantLock furculitaDreapta, Semaphore majordom) {
            this.id = id;
            this.furculitaStanga = furculitaStanga;
            this.furculitaDreapta = furculitaDreapta;
            this.majordom = majordom;
        }

        @Override
        public void run() {
            for (int runda = 1; runda <= RUNDURI; runda++) {
                mediteaza();
                infometeaza();
                mananca(runda);
            }
            seteazaStare(id, Stare.MEDITARE);
            log("Filozoful " + (id + 1) + " a terminat ciclurile si mediteaza linistit.\n");
        }

        private void mediteaza() {
            seteazaStare(id, Stare.MEDITARE);
            pauzaAleatoare(600, 1400);
        }

        private void infometeaza() {
            seteazaStare(id, Stare.INFOMETAT);
            log("Filozoful " + (id + 1) + " este INFOMETAT si asteapta furculitele.\n");
        }

        private void mananca(int runda) {
            try {
                majordom.acquire();
                furculitaStanga.lock();
                furculitaDreapta.lock();

                seteazaStare(id, Stare.MANANCA);
                log("Filozoful " + (id + 1) + " MANANCA (runda " + runda + ").\n");
                pauzaAleatoare(500, 1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (furculitaDreapta.isHeldByCurrentThread()) {
                    furculitaDreapta.unlock();
                }
                if (furculitaStanga.isHeldByCurrentThread()) {
                    furculitaStanga.unlock();
                }
                majordom.release();
                log("Filozoful " + (id + 1) + " a pus furculitele jos.\n");
            }
        }

        private void pauzaAleatoare(int minMs, int maxMs) {
            int delay = minMs + random.nextInt(Math.max(1, maxMs - minMs));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
