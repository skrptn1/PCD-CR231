import javax.swing.*;
import java.awt.*;

/*
 * X = 2 producatori
 * Y = 3 consumatori  
 * Z = 11 obiecte per consumator
 * D = 8 dimensiunea depozitului
 * F = 2 obiecte produse de fiecare data
 * Tip obiecte: Numere pare
 */
public class lab4 {
    public static void main(String[] args) {
        // Parametri
        final int X = 2;  // numar producatori
        final int Y = 3;  // numar consumatori
        final int Z = 11; // obiecte per consumator
        final int D = 8;  // dimensiunea depozitului
        final int F = 2;  // obiecte produse per iteratie
        
        // Cream interfata grafica
        InterfataGUI gui = new InterfataGUI();
        
        // Cream depozitul
        Depozit depozit = new Depozit(D, Y, Z, gui);
        
        // Cream si pornim thread-urile
        Producator p1 = new Producator(depozit, 1, F, gui);
        Producator p2 = new Producator(depozit, 2, F, gui);
        
        Consumator c1 = new Consumator(depozit, 1, Z, gui);
        Consumator c2 = new Consumator(depozit, 2, Z, gui);
        Consumator c3 = new Consumator(depozit, 3, Z, gui);
        
        p1.start();
        p2.start();
        c1.start();
        c2.start();
        c3.start();
    }
}

// Interfata grafica simpla
class InterfataGUI extends JFrame {
    private JTextArea outputArea;
    
    public InterfataGUI() {
        setTitle("Producer-Consumer Monitor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Titlu
        JLabel titlu = new JLabel("Producer-Consumer Simulation", SwingConstants.CENTER);
        titlu.setFont(new Font("Arial", Font.BOLD, 18));
        titlu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titlu, BorderLayout.NORTH);
        
        // Area pentru output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        infoPanel.add(new JLabel("X=2 Producatori"));
        infoPanel.add(new JLabel("Y=3 Consumatori"));
        infoPanel.add(new JLabel("Z=11 obiecte/cons"));
        infoPanel.add(new JLabel("D=8 dimensiune"));
        infoPanel.add(new JLabel("F=2 obiecte/prod"));
        add(infoPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void afiseaza(String mesaj) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(mesaj + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
        System.out.println(mesaj); // si in consola
    }
}

class Depozit {
    private int[] buffer;
    private int count = 0;
    private int totalConsumat = 0;
    private int totalNecesar;
    private InterfataGUI gui;
    
    public Depozit(int dimensiune, int numarConsumatori, int obiectePeConsumator, InterfataGUI gui) {
        this.buffer = new int[dimensiune];
        this.totalNecesar = numarConsumatori * obiectePeConsumator;
        this.gui = gui;
        gui.afiseaza("=== DEPOZIT CREAT: Dimensiune=" + dimensiune + ", Total necesar=" + totalNecesar + " ===\n");
    }
    
    synchronized void produce(int valoare) {
        while(count == buffer.length) {
            gui.afiseaza(">>> DEPOZITUL ESTE PLIN! Asteptare...");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        buffer[count] = valoare;
        count++;
        gui.afiseaza("    [Depozit: " + count + "/" + buffer.length + " elemente]");
        notifyAll();
    }
    
    synchronized int consuma() {
        while(count == 0) {
            if(totalConsumat >= totalNecesar) {
                return -1; // stop signal
            }
            gui.afiseaza(">>> DEPOZITUL ESTE GOL! Asteptare...");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        count--;
        int valoare = buffer[count];
        totalConsumat++;
        gui.afiseaza("    [Depozit: " + count + "/" + buffer.length + " elemente]");
        notifyAll();
        return valoare;
    }
    
    synchronized boolean esteComplet() {
        return totalConsumat >= totalNecesar;
    }
}

class Producator extends Thread {
    private Depozit depozit;
    private int id;
    private int obiectePeIteratie;
    private InterfataGUI gui;
    
    public Producator(Depozit depozit, int id, int obiectePeIteratie, InterfataGUI gui) {
        this.depozit = depozit;
        this.id = id;
        this.obiectePeIteratie = obiectePeIteratie;
        this.gui = gui;
    }

    @Override
    public void run() {
        gui.afiseaza("\n[PRODUCATOR " + id + "] Start productie (cate " + obiectePeIteratie + " obiecte/iteratie)");
        
        while(!depozit.esteComplet()) {
            // Produce F obiecte de fiecare data
            for(int i = 0; i < obiectePeIteratie; i++) {
                if(depozit.esteComplet()) {
                    break;
                }
                
                // Genereaza numar par aleatoriu (10-100)
                int numarPar = ((int)(Math.random() * 46) + 5) * 2;
                
                depozit.produce(numarPar);
                gui.afiseaza("[PRODUCATOR " + id + "] A produs: " + numarPar);
            }
            
            try {
                sleep((int)(Math.random() * 500) + 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        gui.afiseaza("[PRODUCATOR " + id + "] *** TERMINAT ***\n");
    }
}

class Consumator extends Thread {
    private Depozit depozit;
    private int id;
    private int obiectiveNecesare;
    private InterfataGUI gui;
    private int consumate = 0;

    public Consumator(Depozit depozit, int id, int obiectiveNecesare, InterfataGUI gui) {
        this.depozit = depozit;
        this.id = id;
        this.obiectiveNecesare = obiectiveNecesare;
        this.gui = gui;
    }

    @Override
    public void run() {
        gui.afiseaza("\n[CONSUMATOR " + id + "] Start consum (necesar: " + obiectiveNecesare + " obiecte)");
        
        while(consumate < obiectiveNecesare) {
            int valoare = depozit.consuma();
            
            if(valoare == -1) {
                break; // stop signal
            }
            
            consumate++;
            gui.afiseaza("[CONSUMATOR " + id + "] A consumat: " + valoare + " (total: " + 
                        consumate + "/" + obiectiveNecesare + ")");
            
            if(consumate >= obiectiveNecesare) {
                gui.afiseaza("[CONSUMATOR " + id + "] *** INDESTULAT cu " + consumate + " obiecte ***\n");
            }
            
            try {
                sleep((int)(Math.random() * 400) + 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
