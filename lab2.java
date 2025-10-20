class Thread1 implements Runnable {
    private int from, to, step;
    private int[] tablou;
    private String name;

    public Thread1(String name, int from, int to, int step, int[] tablou) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
    }

    @Override
    public void run() {
        int suma = 0;
        int cnt=0;
        int i = from;
        while (i != to) {
            if ((tablou[i] % 2) == 0) {
                cnt++;
                suma+=tablou[i];
                if(cnt==2)
                {
                    cnt=0;
                    System.out.println("Thread "+this.name+" suma: "+suma);
                    suma=0;
                }
            }
            i += step;
        }
        if(from==0)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String info = new String("Thread "+this.name+": Au efectuat lucrarea Buga Pavel si Bivol Daniel, grupa CR-231 var1.");
            for(char j :  info.toCharArray())
            {
                try {
                    System.out.print(j);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
class Thread2 implements Runnable {
    private int from, to, step;
    private int[] tablou;
    private String name;

    public Thread2(String name, int from, int to, int step, int[] tablou) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
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
                    System.out.println("Thread " + this.name + " suma: " + suma);
                    suma = 0;
                }
            }
            i += step;
        }
    }
}

class Main {
    static void main(String[] args) {
        int[] tablou = new int[100];

        for (int i = 0; i < 100; i++) {
            tablou[i] = (int)(Math.random()*99)+1;
            System.out.print(tablou[i] + " ");
        }
        System.out.println();

        Thread t1 = new Thread(new Thread1("Unu", 0, tablou.length, 1, tablou));
        Thread t2 = new Thread(new Thread1("Doi", tablou.length - 1, -1, -1, tablou));

        Thread t3 = new Thread(new Thread2("Trei", 0, tablou.length, 1, tablou));
        Thread t4 = new Thread(new Thread2("Patru", tablou.length - 1, -1, -1, tablou));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}

