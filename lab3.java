import javax.swing.*;

public class lab3 {
    public static void main(String[] args){

        JFrame frame = new JFrame("Interfață");
        frame.setSize(800, 500);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        int[] a = new int[100];
        for(int i=0; i<100; i++) a[i] = i+1;

        int[] intervalS3 = new int[201];
        int idx=0; for(int i=100; i<=300; i++) intervalS3[idx++] = i;

        StringBuilder sb = new StringBuilder("Tablou a[100]:\n");
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]).append(' ');
            if ((i+1)%30==0) sb.append('\n');
        }
        sb.append("\n\nInterval S3 [100..300]:\n");
        for (int i = 0; i < intervalS3.length; i++) {
            sb.append(intervalS3[i]).append(' ');
            if ((i+1)%30==0) sb.append('\n');
        }
        sb.append("\n\n");
        String head = sb.toString();
        System.out.print(head);
        SwingUtilities.invokeLater(() -> textArea.append(head));

        ThreadS2 t2 = new ThreadS2(a, "Pavel, Daniel", null, textArea);
        ThreadS4 t4 = new ThreadS4(t2, "CR-231", textArea);
        ThreadS1 t1 = new ThreadS1(a, "Buga, Bivol", textArea, t4);
        ThreadS3 t3 = new ThreadS3(intervalS3, "Programarea Concurenta Distribuita", t1, textArea);

        t1.setName("Th1");
        t2.setName("Th2");
        t3.setName("Th3");
        t4.setName("Th4");

        t2.start();
        t4.start();
        t1.start();
        t3.start();
    }
}

class ThreadS1 extends Thread {
    private int[] data;
    private String prenume;
    private JTextArea textArea;
    private ThreadS4 t4;

    public ThreadS1(int[] data, String prenume, JTextArea textArea, ThreadS4 t4) {
        this.data = data;
        this.prenume = prenume;
        this.textArea = textArea;
        this.t4 = t4;
    }

    @Override
    public void run() {
        int cnt = 0;
        long sum = 0;

        for(int i = 0; i < data.length - 1; i++){
            if(data[i] % 2 == 0){
                for(int j = i + 1; j < data.length; j++){
                    if(data[j] % 2 == 0){
                        sum += (long)data[i] + data[j];
                        cnt++;
                        break;
                    }
                }
            }
        }

        String line = "\n"+currentThread().getName()+" - suma totala:"+sum+" ("+cnt+" perechi)";
        System.out.print(line);
        SwingUtilities.invokeLater(() -> textArea.append(line));

        while(!t4.done){
            Thread.onSpinWait();
        }

        String prefix = "\n"+currentThread().getName()+" - ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));
        for(char ch : prenume.toCharArray()){
            System.out.print(ch);
            char c = ch;
            SwingUtilities.invokeLater(() -> textArea.append(String.valueOf(c)));
            try{ Thread.sleep(100);}catch(Exception e){}
        }
        System.out.println();
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}

class ThreadS2 extends Thread {
    private int[] a;
    private String nume;
    private ThreadS1 t1;
    private JTextArea textArea;

    public ThreadS2(int[] a, String nume, ThreadS1 t1, JTextArea textArea){
        this.a = a;
        this.nume = nume;
        this.t1 = t1;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        int cnt = 0;
        int sum = 0;

        for(int i = a.length-1; i>=0; i--){
            if(a[i] % 2 == 0){
                cnt++;
                sum += i;
                if(cnt == 2){
                    String out = "\n"+currentThread().getName()+" - suma:"+sum+" ";
                    System.out.print(out);
                    String s = out;
                    SwingUtilities.invokeLater(() -> textArea.append(s));
                    cnt = 0;
                    sum = 0;
                    try{ Thread.sleep(50);}catch(Exception e){}
                }
            }
        }

        try{ Thread.sleep(3500); }catch(Exception e){}

        String prefix = "\n"+currentThread().getName()+" - ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));
        for(char ch : nume.toCharArray()){
            System.out.print(ch);
            char c = ch;
            SwingUtilities.invokeLater(() -> textArea.append(String.valueOf(c)));
            try{ Thread.sleep(100);}catch(Exception e){}
        }
        System.out.println();
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}

class ThreadS3 extends Thread {
    private int[] interval;
    private String disciplina;
    private ThreadS1 t1;
    private JTextArea textArea;

    public ThreadS3(int[] interval, String disciplina ,ThreadS1 t1, JTextArea textArea){
        this.interval = interval;
        this.disciplina = disciplina;
        this.t1= t1;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        for(int i = 0; i < interval.length; i++){
            if(i % 20 == 0){
                String hdr = "\n"+currentThread().getName()+" - ";
                System.out.print(hdr);
                String h = hdr;
                SwingUtilities.invokeLater(() -> textArea.append(h));
            }
            String tok = interval[i]+" ";
            System.out.print(tok);
            String t = tok;
            SwingUtilities.invokeLater(() -> textArea.append(t));
            try{ Thread.sleep(5);}catch(Exception e){}
        }
        System.out.println();
        SwingUtilities.invokeLater(() -> textArea.append("\n"));

        while(t1.isAlive()){
            try{ Thread.sleep(10);}catch(Exception e){}
            Thread.yield();
        }

        String prefix = "\n"+currentThread().getName()+" - ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));
        for(char ch : disciplina.toCharArray()){
            System.out.print(ch);
            char c = ch;
            SwingUtilities.invokeLater(() -> textArea.append(String.valueOf(c)));
            try{ Thread.sleep(100);}catch(Exception e){}
        }
        System.out.println();
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
    }
}

class ThreadS4 extends Thread {
    private ThreadS2 t2;
    private String grupa;
    private JTextArea textArea;
    public boolean done=false;


    public ThreadS4(ThreadS2 t2, String grupa, JTextArea textArea){
        this.t2 = t2;
        this.grupa = grupa;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        for(int i=700; i>=300; i--){
            if(i % 20 == 0){
                String hdr = "\n"+currentThread().getName()+" - ";
                System.out.print(hdr);
                String h = hdr;
                SwingUtilities.invokeLater(() -> textArea.append(h));
            }
            String tok = i+" ";
            System.out.print(tok);
            String t = tok;
            SwingUtilities.invokeLater(() -> textArea.append(t));
            try{ Thread.sleep(5);}catch(Exception e){}
        }
        System.out.println();
        SwingUtilities.invokeLater(() -> textArea.append("\n"));

        try{ t2.join(); }catch(Exception e){}

        String prefix = "\n"+currentThread().getName()+" - ";
        System.out.print(prefix);
        SwingUtilities.invokeLater(() -> textArea.append(prefix));
        for(char ch : grupa.toCharArray()){
            System.out.print(ch);
            char c = ch;
            SwingUtilities.invokeLater(() -> textArea.append(String.valueOf(c)));
            try{ Thread.sleep(100);}catch(Exception e){}
        }
        System.out.println();
        SwingUtilities.invokeLater(() -> textArea.append("\n"));
        done=true;
    }
}
