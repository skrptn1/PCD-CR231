package lucru_induvidual;
import javax.swing.JLabel;
import java.awt.Color;
import java.util.Random;
import javax.swing.SwingUtilities;

public class Philosopher extends Thread {

    private int id;
    private Fork left;
    private Fork right;
    private JLabel label;
    private Random random = new Random();

    public Philosopher(int id, Fork left, Fork right, JLabel label) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.label = label;
    }

    private void think() throws InterruptedException {
        updateLabel("Gândește 🤔", Color.YELLOW);
        Thread.sleep(random.nextInt(2000) + 1000);
    }

    private void eat() throws InterruptedException {
        updateLabel("Mănâncă 🍝", Color.GREEN);
        Thread.sleep(random.nextInt(2000) + 1000);
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();

                updateLabel("Flămând 😐", Color.RED);

                synchronized (left) {
                    synchronized (right) {
                        eat();
                    }
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void updateLabel(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            label.setText("<html><center>Filozof " + id + "<br>" + text + "</center></html>");
            label.setBackground(color);
        });
    }
}