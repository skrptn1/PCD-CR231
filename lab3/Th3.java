package lab3;
import javax.swing.*;

public class Th3 extends Thread {

    private int[] mas;
    private JTextArea taTh3;

    public Th3(int[] mas, JTextArea taTh3) {
        this.mas = mas;
        this.taTh3 = taTh3;
    }

    @Override
    public void run() {

        taTh3.append("Th3 – Parcurgere de la început interval [126, 987]:\n");

       
        for (int v : mas) {
            if (v >= 126 && v <= 987) {
                taTh3.append(v + "\n");
            }
        }

        
        taTh3.append("\nSarcina 3 realizată.\n");

        
        String disciplina = "Programarea Paralelă și Distribuită";

        taTh3.append("\nDisciplina:\n");

        for (char c : disciplina.toCharArray()) {
            taTh3.append(String.valueOf(c));
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }

        taTh3.append("\nTh3 a terminat.\n");
    }
}
