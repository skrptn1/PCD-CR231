import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class lab5 {
    public static void main(String[] args) {

        Depozit depozit = new Depozit(6, 3);

        JFrame frame = new JFrame("Producător - Consumator cu Thread Pool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 450);
        frame.setLayout(new BorderLayout());

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
        depozit.setLogArea(logArea);

        ExecutorService executor = Executors.newFixedThreadPool(6); 

        // Adăugăm producători
        for (int i = 1; i <= 3; i++) {
            executor.submit(new Producator(depozit, i, logArea));
        }

        // Adăugăm consumatori
        for (int i = 1; i <= 3; i++) {
            executor.submit(new Consumator(depozit, i, logArea));
        }

        executor.shutdown();
    }
}

class Depozit {
    private final char[] buffer;
    private int putIndex = 0;
    private int getIndex = 0;
    private int count = 0;
    private JTextArea log;

    private int consumatoriSatisfacuti = 0;
    private final int totalConsumatori;

    public static final char[] VOCALE = {'A','E','I','O','U'};

    public Depozit(int size, int totalConsumatori) {
        buffer = new char[size];
        this.totalConsumatori = totalConsumatori;
    }

    public void setLogArea(JTextArea logArea) {
        this.log = logArea;
    }

    public synchronized boolean produce(char obj1,char obj2, int id) {
        if (consumatoriSatisfacuti == totalConsumatori)
            return false;

        while (count >= buffer.length) {
            if (consumatoriSatisfacuti == totalConsumatori) return false;
            if (log != null)
                SwingUtilities.invokeLater(() -> log.append("Depozit plin\n"));
            try { wait(); } catch (InterruptedException ignored) {}
        }

        buffer[putIndex] = obj1;
        putIndex = (putIndex + 1) % buffer.length;
        count++;

        buffer[putIndex] = obj2;
        putIndex = (putIndex + 1) % buffer.length;
        count++;

        notifyAll();
        return true;
    }

    public synchronized char consuma() {
        while (count == 0) {
            if (consumatoriSatisfacuti == totalConsumatori) return '\0';
            if (log != null)
                SwingUtilities.invokeLater(() -> log.append("Depozit gol\n"));
            try { wait(); } catch (InterruptedException ignored) {}
        }

        char obj = buffer[getIndex];
        getIndex = (getIndex + 1) % buffer.length;
        count--;

        notifyAll();
        return obj;
    }

    public synchronized void consumatorSatisfacut() {
        consumatoriSatisfacuti++;
        notifyAll();
    }

    public synchronized int getCount() { return count; }
    public int getCapacity() { return buffer.length; }
}

class Producator implements Runnable {
    private final Depozit depozit;
    private final int id;
    private final JTextArea log;
    private final Random random = new Random();

    public Producator(Depozit d, int id, JTextArea log) {
        depozit = d;
        this.id = id;
        this.log = log;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> log.append(msg + "\n"));
    }

    @Override
    public void run() {
        while (true) {
            char vocal1 = Depozit.VOCALE[random.nextInt(Depozit.VOCALE.length)];
            char vocal2 = Depozit.VOCALE[random.nextInt(Depozit.VOCALE.length)];

            if (!depozit.produce(vocal1, vocal2, id)) {
                log("Producător " + id + " s-a oprit.");
                return;
            }

            log(" Producător " + id + " a produs: " + vocal1 + "," + vocal2);


            try { Thread.sleep(random.nextInt(400)); } 
            catch (InterruptedException ignored) {}
        }
    }
}

class Consumator implements Runnable {
    private final Depozit depozit;  
    private final int id;
    private int consumate = 0;
    private final JTextArea log;

    public Consumator(Depozit d, int id, JTextArea log) {
        depozit = d;
        this.id = id;
        this.log = log;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> log.append(msg + "\n"));
    }

    @Override
    public void run() {
        while (consumate < 5) {
            char obj = depozit.consuma();
            if (obj == '\0') break;
            consumate++;
            log("Consumator " + id + " a consumat: " + obj);

            try { Thread.sleep((int)(Math.random()*300)); } 
            catch (InterruptedException ignored) {}
        }

        depozit.consumatorSatisfacut();
        log(" Consumator " + id + " a terminat consumul (5 obiecte).");

    }
}
