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
        char lit[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};

        for (int i = 0; i < 10; i++) {
            if (Thread.currentThread().getName().equals("Cifre")) {
                int c = (int) (Math.random() * 10);
                int cifra = cif[c];
                int l = (int) (Math.random() * 10);
                System.out.println("Cifre: Firul " + Thread.currentThread().getName() +  " a generat cifra " + cifra); 
            }
        }
        try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Firul Cifre a fost întrerupt!");
            }
        String nume = new String("Ungureanu Vlad");
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
        char lit[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};

        for (int i = 0; i < 10; i++) {
            int l = (int) (Math.random() * 10);
            System.out.println("Litere: Firul " + getName() + " a generat litera " + lit[l]); 
        }
        try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println("Firul Litere a fost întrerupt!");
            }
        String name = "Programarea Concurenta si Distribuita";
        for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
            }
        }
        System.out.println();
    }
}


