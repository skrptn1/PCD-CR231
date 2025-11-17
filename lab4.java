import javax.swing.*;
import java.util.Random;

public class lab4 {

    public static final int X = 2;
    public static final int Y = 3;
    public static final int Z = 11;
    public static final int D = 8;
    public static final int F = 2;

    static JTextArea sharedTextArea;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Producător-Consumator - Varianta 1");
            frame.setSize(900, 600);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            sharedTextArea = textArea;

            log("=== Pornire simulare producător-consumator (varianta 1) ===\n");

            new Thread(lab4::runSimulation).start();
        });
    }

    private static void runSimulation() {
        int totalDeProduse = Y * Z;

        Depozit depozit = new Depozit(D, totalDeProduse);

        Producator[] producatori = new Producator[X];
        Consumator[] consumatori = new Consumator[Y];

        for (int i = 0; i < X; i++) {
            producatori[i] = new Producator(depozit, i + 1, F);
            producatori[i].setName("Prod-" + (i + 1));
        }

        for (int i = 0; i < Y; i++) {
            consumatori[i] = new Consumator(depozit, i + 1, Z);
            consumatori[i].setName("Cons-" + (i + 1));
        }

        for (Producator p : producatori) p.start();
        for (Consumator c : consumatori) c.start();

        try {
            for (Producator p : producatori) p.join();
            for (Consumator c : consumatori) c.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log("Toate obiectele au fost produse și consumate. Program terminat.\n");
    }

    public static void log(String msg) {
        System.out.print(msg);
        JTextArea ta = sharedTextArea;
        if (ta != null) {
            synchronized (ta) {
                String s = msg;
                SwingUtilities.invokeLater(() -> ta.append(s));
            }
        }
    }
}

class Depozit {
    private final int[] buffer;
    private int count = 0;

    private final int totalDeProduse;
    private int produse = 0;
    private int consumate = 0;
    private boolean terminat = false;

    public Depozit(int dimensiune, int totalDeProduse) {
        this.buffer = new int[dimensiune];
        this.totalDeProduse = totalDeProduse;
    }

    public synchronized boolean produce(int valoare, int idProducator) {
        if (produse >= totalDeProduse) return false;

        while (count == buffer.length) {
            lab4.log(">>> Depozitul este PLIN. Producatorul " + idProducator + " asteapta...\n");
            try { wait(); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            if (produse >= totalDeProduse) return false;
        }

        buffer[count] = valoare;
        count++;
        produse++;

        lab4.log("Producatorul " + idProducator + " a produs: " + valoare +
                " (in depozit: " + count + ", produse total: " + produse + ")\n");

        if (produse == totalDeProduse) terminat = true;

        notifyAll();
        return true;
    }

    public synchronized Integer consuma(int idConsumator) {
        while (count == 0 && !terminat) {
            lab4.log("<<< Depozitul este GOL. Consumatorul " + idConsumator + " asteapta...\n");
            try { wait(); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        if (count == 0 && terminat) return null;

        count--;
        int valoare = buffer[count];
        consumate++;

        lab4.log("Consumatorul " + idConsumator + " a consumat: " + valoare +
                " (in depozit: " + count + ", consumate total: " + consumate + ")\n");

        notifyAll();
        return valoare;
    }
}

class Producator extends Thread {
    private final Depozit depozit;
    private final int id;
    private final int batchSize;
    private final Random random = new Random();

    public Producator(Depozit d, int id, int batchSize) {
        this.depozit = d;
        this.id = id;
        this.batchSize = batchSize;
    }

    private int genereazaNumarPar() {
        int n = 10 + random.nextInt(61) * 2;
        return n;
    }

    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < batchSize; i++) {
                int val = genereazaNumarPar();
                boolean ok = depozit.produce(val, id);
                if (!ok) {
                    lab4.log("Producatorul " + id + " se opreste (nu mai sunt necesare obiecte).\n");
                    return;
                }
                try { Thread.sleep(random.nextInt(400)); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}

class Consumator extends Thread {
    private final Depozit depozit;
    private final int id;
    private final int deConsum;
    private final Random random = new Random();

    public Consumator(Depozit d, int id, int deConsum) {
        this.depozit = d;
        this.id = id;
        this.deConsum = deConsum;
    }

    @Override
    public void run() {
        int consumateLocal = 0;

        while (consumateLocal < deConsum) {
            Integer val = depozit.consuma(id);
            if (val == null) break;

            consumateLocal++;

            try { Thread.sleep(random.nextInt(500)); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        lab4.log("Consumatorul " + id +
                " s-a indestulat cu " + consumateLocal + " obiecte si se opreste.\n");
    }
}
