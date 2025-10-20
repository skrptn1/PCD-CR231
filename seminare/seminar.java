public class seminar {
    public static void main(String[] args) {
        ThreadCifre t1 = new ThreadCifre("Cifre");
        ThreadLitere t2 = new ThreadLitere("Litere");

        t1.setName("Cifre");
        t2.setName("Litere");

        t1.start();
        t2.start();
    }
}

class ThreadCifre extends Thread {
    String nume;

    public ThreadCifre(String nume) {
        this.nume = nume;
    }

    public void run() {
        int cif[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        for (int i = 0; i < 10; i++) {
            if (Thread.currentThread().getName().equals("Cifre")) {
                int c = (int) (Math.random() * 10);
                int cifra = cif[c];
                System.out.println("Cifre: Firul " + Thread.currentThread().getName() +
                                   " a generat cifra " + cifra);
            }
        }
        String nume = new String("Munteanu Maxim");
        for (int i = 0; i < nume.length(); i++) {
            System.out.print(nume.charAt(i));
            try {
                sleep(150);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        System.out.println();
    }
}

class ThreadLitere extends Thread {
    String nume;

    public ThreadLitere(String nume) {
        this.nume = nume;
    }

    public void run() {
        char lit[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k','l','m','n','o','p','q','r','s','t','u','v','w','x'};

        for (int i = 0; i < 24; i++) {
            if (Thread.currentThread().getName().equals("Litere")) {
                int l = (int) (Math.random() * 10);
                char litera = lit[l];
                System.out.println("Litere: Firul " + Thread.currentThread().getName() + " a generat litera " + litera);
            }
        }
        String name = new String("Programarea Concurenta si Distribuita");
        for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try {
                sleep(150);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        System.out.println();
    }
}
