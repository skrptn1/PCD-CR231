package lab4;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends JFrame {
    private JTextArea logArea;

    private static final int NR_PRODUCATORI = 4;
    private static final int NR_CONSUMATORI = 2;
    private static final int OBIECTE_PER_CONSUMATOR = 4;
    private static final int BUFFER_SIZE = 5;

    public static AtomicInteger consumatoriTerminati = new AtomicInteger(0);
    public static Producator[] producatori = new Producator[NR_PRODUCATORI];

    public Main() {
        super("Producător - Consumator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startSimulation();
    }

    private void startSimulation() {
        logArea.setText("");

        Depozit depozit = new Depozit(BUFFER_SIZE, this);

        for (int i = 1; i <= NR_CONSUMATORI; i++) {
            new Consumator(depozit, "Consumator " + i, OBIECTE_PER_CONSUMATOR, this).start();
        }

        for (int i = 1; i <= NR_PRODUCATORI; i++) {
            producatori[i - 1] = new Producator(depozit, "Producator " + i, this);
            producatori[i - 1].setDaemon(true);
            producatori[i - 1].start();
        }
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}


class Depozit {
    private LinkedList<Character> stockList = new LinkedList<>();
    private int capacity;
    private Main gui;

    private boolean depozitPlinAfisat = false;
    private boolean depozitGolAfisat = false;

    public Depozit(int capacity, Main gui) {
        this.capacity = capacity;
        this.gui = gui;
    }

    public synchronized void put(String nume, char c) {
        while (stockList.size() == capacity) {
            try {
                if (!depozitPlinAfisat) {
                    gui.log("Depozit PLIN (" + stockList.size() + "/" + capacity + ")");
                    depozitPlinAfisat = true;
                }
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }

        depozitPlinAfisat = false;

        stockList.add(c);

        gui.log(nume + " a produs: " + c +
                " | Depozit: " + stockList.size() + "/" + capacity);

        notifyAll();
    }

    public synchronized char get(String nume) {
        while (stockList.isEmpty()) {
            try {
                if (!depozitGolAfisat) {
                    gui.log("Depozit GOL (0/" + capacity + ")");
                    depozitGolAfisat = true;
                }
                wait();
            } catch (InterruptedException e) {
                return 0;
            }
        }

        depozitGolAfisat = false;

        char valoare = stockList.removeFirst();

        gui.log(nume + " a consumat: " + valoare +
                " | Depozit: " + stockList.size() + "/" + capacity);

        notifyAll();
        return valoare;
    }
}


class Producator extends Thread {
    private Depozit depozit;
    private Random rand = new Random();
    private final char[] consoane = "BCDFGHJKLMNPQRSTVWXYZ".toCharArray();
    private Main gui;

    public Producator(Depozit d, String name, Main gui) {
        super(name);
        this.depozit = d;
        this.gui = gui;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Thread.sleep(800 + rand.nextInt(1000));

                char c = consoane[rand.nextInt(consoane.length)];
                depozit.put(getName(), c);

            } catch (InterruptedException e) {
                break;
            }
        }

        gui.log(getName() + "s-a oprit.");
    }
}


class Consumator extends Thread {
    private Depozit depozit;
    private int limitaConsum;
    private int consumate = 0;
    private Main gui;

    public Consumator(Depozit d, String name, int limita, Main gui) {
        super(name);
        this.depozit = d;
        this.limitaConsum = limita;
        this.gui = gui;
    }

    @Override
    public void run() {
        while (consumate < limitaConsum) {
            try {
                Thread.sleep(1000 + (long) (Math.random() * 1000));
                depozit.get(getName());
                consumate++;
            } catch (InterruptedException e) {
                break;
            }
        }

        gui.log(getName() + "a terminat consumul (" + consumate + " obiecte).");

        if (Main.consumatoriTerminati.incrementAndGet() == 2) {
            for (Producator p : Main.producatori) {
                p.interrupt();
            }
        }
    }
}
