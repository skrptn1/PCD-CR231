import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class SleepingBarber {

   
    static class BarberShop {

        private final int numarScaune;
        private int clientiAsteapta = 0;

        private final Semaphore clienti = new Semaphore(0);
        private final Semaphore frizeri;
        private final Object lock = new Object();

        private BarberShopGUI gui;

        public volatile boolean running = false;

        public BarberShop(int scaune, int nrFrizeri) {
            this.numarScaune = scaune;
            this.frizeri = new Semaphore(nrFrizeri);
        }

        public void setGUI(BarberShopGUI gui) {
            this.gui = gui;
        }

        public boolean vineClient(int id) {
            synchronized (lock) {
                if (!running) return false;

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
                if (!running) return;
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
                    if (shop.running)
                        shop.tundeClient(id);
                    else
                        Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

  
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

    
    static class BarberShopGUI extends JFrame {

        private JLabel[] frizeriStatus = new JLabel[2];
        private JLabel waitingLabel;
        private JTextArea logArea;

        private ImageIcon iconFree = new ImageIcon("free.png");
        private ImageIcon iconBusy = new ImageIcon("busy.png");

        private JButton startBtn;
        private JButton stopBtn;

        public BarberShopGUI(BarberShop shop) {

            setTitle("Sleeping Barber – Start/Stop + Iconițe");
            setSize(550, 450);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            
            JPanel top = new JPanel(new GridLayout(1, 2));

            frizeriStatus[0] = new JLabel("Frizer 1: liber", iconFree, SwingConstants.CENTER);
            frizeriStatus[1] = new JLabel("Frizer 2: liber", iconFree, SwingConstants.CENTER);

            frizeriStatus[0].setHorizontalTextPosition(SwingConstants.CENTER);
            frizeriStatus[0].setVerticalTextPosition(SwingConstants.BOTTOM);

            frizeriStatus[1].setHorizontalTextPosition(SwingConstants.CENTER);
            frizeriStatus[1].setVerticalTextPosition(SwingConstants.BOTTOM);

            top.add(frizeriStatus[0]);
            top.add(frizeriStatus[1]);

            
            waitingLabel = new JLabel("Clienți în așteptare: 0", SwingConstants.CENTER);

           
            logArea = new JTextArea();
            logArea.setEditable(false);

            
            JPanel controls = new JPanel();
            startBtn = new JButton("START");
            stopBtn = new JButton("STOP");

            controls.add(startBtn);
            controls.add(stopBtn);

            startBtn.addActionListener((ActionEvent e) -> {
                shop.running = true;
                log("Simularea a pornit");
            });

            stopBtn.addActionListener((ActionEvent e) -> {
                shop.running = false;
                log("Simularea a fost oprită");
            });

            add(top, BorderLayout.NORTH);
            add(waitingLabel, BorderLayout.CENTER);
            add(new JScrollPane(logArea), BorderLayout.SOUTH);
            add(controls, BorderLayout.WEST);
        }

        public void updateWaiting(int nr) {
            waitingLabel.setText("Clienți în așteptare: " + nr);
        }

        public void frizerStart(int id) {
            frizeriStatus[id - 1].setText("Frizer " + id + ": tunde...");
            frizeriStatus[id - 1].setIcon(iconBusy);
        }

        public void frizerEnd(int id) {
            frizeriStatus[id - 1].setText("Frizer " + id + ": liber");
            frizeriStatus[id - 1].setIcon(iconFree);
        }

        public void log(String msg) {
            logArea.append(msg + "\n");
        }
    }

    
    public static void main(String[] args) throws InterruptedException {

        BarberShop shop = new BarberShop(4, 2);

        BarberShopGUI gui = new BarberShopGUI(shop);
        shop.setGUI(gui);
        gui.setVisible(true);

        new Barber(1, shop).start();
        new Barber(2, shop).start();

        int maxClienti = 20;
        int creati = 0;

        while (true) {
            if (shop.running && creati < maxClienti) {
                new Client(shop).start();
                creati++;
                Thread.sleep((int)(Math.random() * 1200));
            } else {
                Thread.sleep(200);
            }
        }
    }
}
