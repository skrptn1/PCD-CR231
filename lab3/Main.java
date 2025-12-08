package lab3;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Laborator 3 - Fire de execuție");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        frame.add(sp);
        frame.setVisible(true);

        int[] mas = new int[1000];
        for (int i = 0; i < mas.length; i++) mas[i] = 100 + i;

        Th2 t2 = new Th2(mas, ta);
        Th4 t4 = new Th4(mas, ta, t2);
        Th1 t1 = new Th1(mas, ta, t4);
        Th3 t3 = new Th3(mas, ta, t1);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
