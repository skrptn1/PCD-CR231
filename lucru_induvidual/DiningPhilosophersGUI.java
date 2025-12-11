package lucru_induvidual;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DiningPhilosophersGUI extends JFrame {

    private static final int N = 5;
    private Philosopher[] philosophers;
    private Fork[] forks;
    private JLabel[] labels;

    public DiningPhilosophersGUI() {
        setTitle("🍝 Filozofii Mâncând – Sincronizare Java");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, N));

        labels = new JLabel[N];
        forks = new Fork[N];
        philosophers = new Philosopher[N];

        for (int i = 0; i < N; i++) {
            forks[i] = new Fork(i);
        }

        for (int i = 0; i < N; i++) {
            labels[i] = new JLabel("Filozof " + i + "\nGândește", SwingConstants.CENTER);
            labels[i].setOpaque(true);
            labels[i].setBackground(Color.YELLOW);
            labels[i].setFont(new Font("Arial", Font.BOLD, 16));
            add(labels[i]);
        }

        for (int i = 0; i < N; i++) {
            Fork left = forks[i];
            Fork right = forks[(i + 1) % N];

            // evităm deadlock-ul: ultimul filozof ia furculițele invers
            if (i == N - 1) {
                philosophers[i] = new Philosopher(i, right, left, labels[i]);
            } else {
                philosophers[i] = new Philosopher(i, left, right, labels[i]);
            }
        }

        for (Philosopher p : philosophers) {
            p.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DiningPhilosophersGUI().setVisible(true));
    }
}
