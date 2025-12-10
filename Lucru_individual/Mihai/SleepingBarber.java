package Lucru_individual.Mihai;

import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class SleepingBarber {

    // ================================
    //          BARBER SHOP
    // ================================
    static class BarberShop {

        private final int numarScaune;
        private int clientiAsteapta = 0;

        private final Semaphore clienti = new Semaphore(0);
        private final Semaphore frizeri;
        private final Object lock = new Object();

        private BarberShopGUI gui;

        public BarberShop(int scaune, int nrFrizeri) {
            this.numarScaune = scaune;
            this.frizeri = new Semaphore(nrFrizeri);
        }

        public void setGUI(BarberShopGUI gui) {
            this.gui = gui;
        }

        public boolean vineClient(int id) {
            synchronized (lock) {
                if (clientiAsteapta == numarScaune) {
                    gui.log("Clientul " + id + " a plecat – sala plină!");
                    return false;
                }
                clientiAsteapta++;
                gui.updateWaiting(clientiAsteapta);
                gui.log("Clientul " + id + " a intrat în sala de așteptare.");
            }

            clienti.release();
            return true;
        }

        public void tundeClient(int idFrizer) throws InterruptedException {
            clienti.acquire();
            frizeri.acquire();

            synchronized (lock) {
                clientiAsteapta--;
                gui.updateWaiting(clientiAsteapta);
                gui.frizerStart(idFrizer);
                gui.log("Frizerul " + idFrizer + " ia un client.");
            }

            Thread.sleep(2000);

            gui.frizerEnd(idFrizer);
            gui.log("Frizerul " + idFrizer + " a terminat de tuns.");

            frizeri.release();
        }
    }

    // ================================
    //             BARBER
    // ================================
    static class Barber extends Thread {

        private final BarberShop shop;
        private final int id;

        public Barber(int id, BarberShop shop) {
            this.id = id;
            this.shop = shop;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    shop.tundeClient(id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ================================
    //             CLIENT
    // ================================
    static class Client extends Thread {

        private static final AtomicInteger counter = new AtomicInteger(1);
        private final BarberShop shop;
        private final int id;

        public Client(BarberShop shop) {
            this.shop = shop;
            this.id = counter.getAndIncrement();
        }

        @Override
        public void run() {
            shop.vineClient(id);
        }
    }

    // ================================
    //            GUI
    // ================================
    static class BarberShopGUI extends JFrame {

        private JLabel[] frizeriStatus = new JLabel[2];
        private JLabel waitingLabel;
        private JTextArea logArea;

        public BarberShopGUI() {
            setTitle("Sleeping Barber – 2 Frizeri, 4 Locuri, Max 8 Clienți");
            setSize(500, 400);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel top = new JPanel();
            top.setLayout(new GridLayout(1, 2));

            frizeriStatus[0] = new JLabel("Frizer 1: liber", SwingConstants.CENTER);
            frizeriStatus[1] = new JLabel("Frizer 2: liber", SwingConstants.CENTER);

            top.add(frizeriStatus[0]);
            top.add(frizeriStatus[1]);

            waitingLabel = new JLabel("Clienți în așteptare: 0", SwingConstants.CENTER);

            logArea = new JTextArea();
            logArea.setEditable(false);

            add(top, BorderLayout.NORTH);
            add(waitingLabel, BorderLayout.CENTER);
            add(new JScrollPane(logArea), BorderLayout.SOUTH);
        }

        public void updateWaiting(int nr) {
            waitingLabel.setText("Clienți în așteptare: " + nr);
        }

        public void frizerStart(int id) {
            frizeriStatus[id - 1].setText("Frizer " + id + ": tunde...");
            frizeriStatus[id - 1].setForeground(Color.RED);
        }

        public void frizerEnd(int id) {
            frizeriStatus[id - 1].setText("Frizer " + id + ": liber");
            frizeriStatus[id - 1].setForeground(Color.GREEN);
        }

        public void log(String msg) {
            logArea.append(msg + "\n");
        }
    }

    // ================================
    //            MAIN
    // ================================
    public static void main(String[] args) throws InterruptedException {

        BarberShop shop = new BarberShop(4, 2); // 4 locuri, 2 frizeri

        BarberShopGUI gui = new BarberShopGUI();
        shop.setGUI(gui);
        gui.setVisible(true);

        new Barber(1, shop).start();
        new Barber(2, shop).start();

        int maxClienti = 8;
        int creati = 0;

        while (creati < maxClienti) {
            new Client(shop).start();
            creati++;
            Thread.sleep((int)(Math.random() * 1500));
        }

        gui.log("S-au generat toți cei 8 clienți.");
    }
}
