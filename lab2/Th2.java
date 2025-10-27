package lab2;

class Th2 implements Runnable {

    private int from, to, step;
    private int[] tablou;
    String name = "Th2";
    private App app;

    public Th2(String name, int from, int to, int step, int[] tablou, App app) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
        this.name = name;
        this.app = app;
    }

    public void run() {
        int s1 = -1, s2 = -1, s;
        int i = from;
        while (i >= to) {
            if (tablou[i] % 2 == 0) {
                if (s1 == -1)
                    s1 = i;
            } else {
                s2 = i;
                s = s1 + s2;
                String output = "MIHAI ZAHRENCO " + this.name + " poz " + s1 + " + " + s2 + " = " +
                        s + " val " + tablou[s1] + " " + tablou[s2] + "\n";
                app.appendText(output);
                System.out.println(output);

                s1 = -1;
                s2 = -1;
            }
        }
        i -= step;
    }
}
