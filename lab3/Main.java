package lab3;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Laborator 3 - Fire de execuție");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        frame.add(sp);

        frame.setVisible(true);

        int[] mas = new int[1000];
        for (int i = 0; i < mas.length; i++) mas[i] = 100 + i;

        boolean[] th1Flag = new boolean[1];

        Th1 t1 = new Th1(mas, ta, th1Flag);
        Th2 t2 = new Th2(mas, ta, th1Flag);
        Th3 t3 = new Th3(mas, ta);
        Th4 t4 = new Th4(mas, ta, t3);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
