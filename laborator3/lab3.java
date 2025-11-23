import javax.swing.*;
import java.awt.*;

public class lab3 {
    public static void main(String[] args) {
        new InterfataLab3();
    }
}

class InterfataLab3 extends JFrame {
    private JTextArea textArea;

    public InterfataLab3() {
        setTitle("Laborator 3 ");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

        Thread2 t2 = new Thread2("Furtuna, Maletchi", textArea);
        Thread4 t4 = new Thread4("CR-231", textArea);
        Thread1 t1 = new Thread1("Radu, Mirela", textArea);
        Thread3 t3 = new Thread3("Programarea Concurenta si Distribuita", textArea);
        t1.setName("Th1");
        t2.setName("Th2");
        t3.setName("T3");
        t4.setName("T4");
        try {

            t1.start();
            t2.start();
            t1.join();
            t2.join();

           t3.start();
            t4.join(); 
            t4.start();
            t4.join();

            t2.afisareDate();
            t4.afisareDate();
            t1.afisareDate();
            t3.afisareDate();

        } catch (InterruptedException e) {
            append("\nExecutia a fost intrerupta!\n");
        }
    }

    private void append(String text) {
        SwingUtilities.invokeLater(() -> textArea.append(text));
    }
}

class Thread1 extends Thread {
    private final JTextArea output;
    private final String nume;

    public Thread1(String nume, JTextArea area) {
        this.output = area;
        this.nume = nume;
    }

    public void run() {
    int primulPar = 0;
    boolean gasit = false;
    int suma = 0;
    int count = 0;

    for (int i = 1; i <= 100; i++) {
        if (i % 2 == 0) {
            if (!gasit) {
                primulPar = i;
                gasit = true;
            } else {
                int produs = primulPar * i;
                append(getName() + ": " + primulPar + " * " + i + " = " + produs + "\n");
                suma += produs;
                count++;

                if (count == 2) {
                    append(getName() + " suma = " + suma + "\n\n");
                    suma = 0;
                    count = 0;
                }

                gasit = false;
                Thread.yield();
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
    }
}


    public void afisareDate() {
        append("\nTh1:");
        for (char c : nume.toCharArray()) {
            append(String.valueOf(c));
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
    
    }

    private void append(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}

class Thread2 extends Thread {
    private final JTextArea output;
    private final String nume;

    public Thread2(String nume, JTextArea area) {
        this.output = area;
        this.nume = nume;
    }

    public void run() {
    int primulPar = 0;
    boolean gasit = false;
    int suma = 0;
    int count = 0;

    for (int i = 100; i >= 1; i--) {
        if (i % 2 == 0) {
            if (!gasit) {
                primulPar = i;
                gasit = true;
            } else {
                int produs = primulPar * i;
                append(getName() + ": " + primulPar + " * " + i + " = " + produs + "\n");
                suma += produs;
                count++;

                if (count == 2) {
                    append(getName() + " suma = " + suma + "\n\n");
                    suma = 0;
                    count = 0;
                }

                gasit = false;
                Thread.yield();
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
    }
}


    public void afisareDate() {
        append("\nTh2: ");
        for (char c : nume.toCharArray()) {
            append(String.valueOf(c));
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }

    }

    private void append(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}


class Thread3 extends Thread {
    private final JTextArea output;
    private final String disciplina;

    public Thread3(String disciplina, JTextArea area) {
        this.output = area;
        this.disciplina = disciplina;
    }

    public void run() {
        append("\n Pornire Th3\n");
        for (int i = 234; i <= 1000; i++) {
            
            append(currentThread().getName() +" "+i + " ");
            try { Thread.sleep(2); } catch (InterruptedException ignored) {}
        }

    }

    public void afisareDate() {
        append("\nTh3: ");
        for (char c : disciplina.toCharArray()) {
            append(String.valueOf(c));
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
       
    }

    private void append(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}

class Thread4 extends Thread {
    private final JTextArea output;
    private final String grupa;

    public Thread4(String grupa, JTextArea area) {
        this.output = area;
        this.grupa = grupa;
    }

    public void run() {
        append("\n Pornire Th4 \n");
        for (int i = 1234; i >= 456; i--) {
            append(currentThread().getName() +" "+i + " ");
            if (i % 20 == 0) append("\n");
            try { Thread.sleep(2); } catch (InterruptedException ignored) {}
        }
    }

    public void afisareDate() {
        append("\nTh4: ");
        for (char c : grupa.toCharArray()) {
            append(String.valueOf(c));
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        
    }

    private void append(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}