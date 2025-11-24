/*Sunt dați 3 producători care generează aleatoriu F (F - fiecare producător produce câte 2 obiecte) 
obiecte care sunt consumate de 3 consumatori. De afişat informaţia despre producerea şi consumarea 
obiectelor, mesajele despre cazurile când “depozitul e gol sau plin”. 
Toate operaţiile se efectuează până când fiecare consumator este îndestulat cu 5 obiecte.
Dimensiunea depozitului este 6.
Tip Obiecte: vocale*/


import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class lab4 {
    public static void main(String[] args) {

        Depozit depozit = new Depozit(6, 3);

        JFrame frame = new JFrame("Producător - Consumator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 450);
        frame.setLayout(new BorderLayout());

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
        depozit.setLogArea(logArea);

        Producator p1 = new Producator(depozit, 1, logArea);
        Producator p2 = new Producator(depozit, 2, logArea);
        Producator p3 = new Producator(depozit, 3, logArea);


        Consumator c1 = new Consumator(depozit, 1, logArea);
        Consumator c2 = new Consumator(depozit, 2, logArea);
        Consumator c3 = new Consumator(depozit, 3, logArea);

        p1.start();
        p2.start();
        p3.start();
        c1.start();
        c2.start();
        c3.start();
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

    public synchronized boolean produce(char obj, int id) {
    if (consumatoriSatisfacuti == totalConsumatori)
        return false;
    
    while (count == buffer.length) {
        if (consumatoriSatisfacuti == totalConsumatori) 
            return false;
        if (log != null)
            SwingUtilities.invokeLater(() -> log.append("Depozit plin\n"));
        try { wait(); } catch (InterruptedException ignored) {}
    }

    buffer[putIndex] = obj;
    putIndex = (putIndex + 1) % buffer.length;
    count++;
    notifyAll();
    return true;
}


    public synchronized char consuma() {

            while (count == 0) {
         if (consumatoriSatisfacuti == totalConsumatori)
        return '\0';
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

class Producator extends Thread {
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
            for (int i = 0; i < 2; i++) {
                char vocal = Depozit.VOCALE[random.nextInt(Depozit.VOCALE.length)];
                if (!depozit.produce(vocal, id)) {
                    log("Producător " + id + " s-a oprit.");
                    return;
                }
                log("Producător " + id + " a produs: " + vocal);
            }

            try { sleep(random.nextInt(400)); } 
            catch (InterruptedException ignored) {}
        }
    }
}

class Consumator extends Thread {
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
            if (obj == '\0')
                break;
            consumate++;
            log("Consumator " + id + " a consumat: " + obj);

            try { 
                sleep((int)(Math.random()*300)); 
            } 
            catch (InterruptedException ignored) {}
        }

        depozit.consumatorSatisfacut();
        log("Consumator " + id + " a terminat consumul (5 obiecte).");
    }
}
