import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.*;


public class MasaFilozofilor {
    private static final int NR_FILOZOFILOR = 5;

    public static void main(String[] args) {

        MasaLogGUI gui = new MasaLogGUI();

        Furculita[] furculite = new Furculita[NR_FILOZOFILOR];
        Thread[] fireExecutie = new Thread[NR_FILOZOFILOR];

        for (int i = 0; i < NR_FILOZOFILOR; i++) {
            furculite[i] = new Furculita(i);
        }

        for (int i = 0; i < NR_FILOZOFILOR; i++) {
            Furculita furculitaStanga = furculite[i];
            Furculita furculitaDreapta = furculite[(i + 1) % NR_FILOZOFILOR];

            Filozof filozof = new Filozof(i, furculitaStanga, furculitaDreapta, gui);
            gui.log("Filozoful " + i + " folosește " + furculitaStanga +
                    " (stânga) și " + furculitaDreapta + " (dreapta).");

            fireExecutie[i] = new Thread(filozof, "Filozof-Fir-" + i);
        }

        gui.log("\n--- Masa Filozofilor Începe Rularea ---");

        for (Thread fir : fireExecutie) {
            fir.start();
        }
    }
}


class Furculita {
    private final int id;
    private final Lock lock = new ReentrantLock();
    private final Condition libera = lock.newCondition();
    private boolean esteOcupata = false;

    public Furculita(int id) {
        this.id = id;
    }

    public int obtineId() {
        return id;
    }

    public void ridica(int filozofId) throws InterruptedException {
        lock.lock();
        try {
            while (esteOcupata) {
                libera.await();
            }
            esteOcupata = true;
        } finally {
            lock.unlock();
        }
    }

    public void lasaJos() {
        lock.lock();
        try {
            esteOcupata = false;
            libera.signal(); 
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Furculița " + id;
    }
}


class Filozof implements Runnable {

    private final int id;
    private final Furculita furculitaStanga;
    private final Furculita furculitaDreapta;
    private final MasaLogGUI gui;

    private int oriMancat = 0;
    private static final int MAX_MANCARE = 5;

    public Filozof(int id, Furculita furculitaStanga,
                   Furculita furculitaDreapta, MasaLogGUI gui) {
        this.id = id;
        this.furculitaStanga = furculitaStanga;
        this.furculitaDreapta = furculitaDreapta;
        this.gui = gui;
    }

    private void gandeste() throws InterruptedException {
        gui.log("Filozoful " + id + " GÂNDEȘTE.");
        Thread.sleep((long) (Math.random() * 1000 + 500));
    }

    private void mananca() throws InterruptedException {
        gui.log("Filozoful " + id + " MĂNÂNCĂ (masa " + (oriMancat + 1) + ").");
        Thread.sleep((long) (Math.random() * 1000 + 1000));
        oriMancat++;
    }

    private void ridicaFurculitele() throws InterruptedException {
        Furculita prima, aDoua;

        if (furculitaStanga.obtineId() < furculitaDreapta.obtineId()) {
            prima = furculitaStanga;
            aDoua = furculitaDreapta;
        } else {
            prima = furculitaDreapta;
            aDoua = furculitaStanga;
        }

        gui.log("Filozoful " + id + " așteaptă " + prima);
        prima.ridica(id);

        gui.log("Filozoful " + id + " așteaptă " + aDoua);
        aDoua.ridica(id);
    }

    private void elibereazaFurculitele() {
        furculitaDreapta.lasaJos();
        furculitaStanga.lasaJos();
        gui.log("Filozoful " + id + " a eliberat furculițele.");
    }

    @Override
    public void run() {
        try {
            while (oriMancat < MAX_MANCARE) {
                gandeste();
                ridicaFurculitele();
                mananca();
                elibereazaFurculitele();
            }
            gui.log("Filozoful " + id + " S-A SĂTURAT (total " + oriMancat + ").");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            gui.log("Filozoful " + id + " a fost întrerupt.");
        }
    }
}

class MasaLogGUI extends JFrame {

    private final JTextArea logArea;

    public MasaLogGUI() {
        setTitle("Problema Filozofilor ce Mănâncă (Condition)");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logArea = new JTextArea();
        logArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane);

        setVisible(true);
    }

    public void log(String mesaj) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(mesaj + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
