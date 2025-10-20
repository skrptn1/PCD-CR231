import javax.swing.*;

class Thread1 implements Runnable {
    private int from, to, step;
    private int[] tablou;
    private String name;
    private JTextArea textArea;

    public Thread1(String name, int from, int to, int step, int[] tablou, JTextArea textArea) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        int suma = 0;
        int cnt = 0;
        int i = from;
        while (i != to) {
            if ((tablou[i] % 2) == 0) {
                cnt++;
                suma += tablou[i];
                if (cnt == 2) {
                    cnt = 0;
                    String mesaj = "Thread " + this.name + " suma: " + suma;
                    System.out.println(mesaj);
                    SwingUtilities.invokeLater(() -> textArea.append(mesaj + "\n"));
                    suma = 0;
                }
            }
            i += step;
        }
        if (from == 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String info = "Thread " + this.name + ": Au efectuat lucrarea Buga Pavel si Bivol Daniel, \ngrupa CR-231 var1.";
            for (char j : info.toCharArray()) {
                try {
                    System.out.print(j);
                    SwingUtilities.invokeLater(() -> textArea.append(j + ""));
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            SwingUtilities.invokeLater(() -> textArea.append("\n"));
        }
    }
}

class Thread2 implements Runnable {
    private int from, to, step;
    private int[] tablou;
    private String name;
    private JTextArea textArea;

    public Thread2(String name, int from, int to, int step, int[] tablou, JTextArea textArea) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        int suma = 0;
        int cnt = 0;
        int i = from;
        while (i != to) {
            if ((tablou[i] % 2) == 0) {
                cnt++;
                suma += tablou[i];
                if (cnt == 2) {
                    cnt = 0;
                    String mesaj = "Thread " + this.name + " suma: " + suma;
                    System.out.println(mesaj);
                    SwingUtilities.invokeLater(() -> textArea.append(mesaj + "\n"));
                    suma = 0;
                }
            }
            i += step;
        }
    }
}

class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Interfață");
        frame.setSize(600, 400);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        int[] tablou = new int[100];
        for (int i = 0; i < 100; i++) {
            tablou[i] = (int) (Math.random() * 99) + 1;
        }

        StringBuilder sb = new StringBuilder("Tablou: \n");
        for (int i = 0; i < tablou.length; i++) {
            sb.append(tablou[i]).append(" ");
            if(i%30==0 && i!=0){
                sb.append("\n");
            }
        }
        String tablouText = sb.toString() + "\n\n";
        System.out.println(tablouText);
        textArea.append(tablouText);

        Thread t1 = new Thread(new Thread1("Unu", 0, tablou.length, 1, tablou, textArea));
        Thread t2 = new Thread(new Thread1("Doi", tablou.length - 1, -1, -1, tablou, textArea));
        Thread t3 = new Thread(new Thread2("Trei", 0, tablou.length, 1, tablou, textArea));
        Thread t4 = new Thread(new Thread2("Patru", tablou.length - 1, -1, -1, tablou, textArea));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
