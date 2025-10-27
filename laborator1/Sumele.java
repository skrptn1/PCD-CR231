public class Sumele {
    public static void main(String[] args) {

        System.out.println("Lista de valori:");

        int[] mas = new int[100];

        for (int i = 0; i < mas.length; i++) {
            mas[i] = (int) (Math.random() * 100) + 1;
            System.out.print(mas[i] + " ");
        }

        System.out.println();
        System.out.println();

        Thread t1 = new Thread(new Fir(1, mas));
        Thread t2 = new Thread(new Fir(2, mas));

        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Firul a fost întrerupt!");
        }

        String name = "Furtuna Rodion CR-231";
        for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        
    }
}

class Fir implements Runnable {
    int alegere;
    int[] mas;

    public Fir(int alegere, int[] mas) {
        this.alegere = alegere;
        this.mas = mas;
    }

    public void run() {
        int primulPar = 0;
        boolean primulGasit = false;

        if (alegere == 1) {
            for (int i = 0; i < mas.length; i++) {
                int numar = mas[i];

                if (numar % 2 == 0) {
                    if (!primulGasit) {
                        primulPar = numar;
                        primulGasit = true;
                    } else {
                        int produs = primulPar * numar;
                        System.out.println("Firul " + alegere + " : " + primulPar + " * " + numar + " = " + produs);
                        primulGasit = false;
                    }
                }
            }

        } else {
            for (int i = mas.length - 1; i >= 0; i--) {
                int numar = mas[i];

                if (numar % 2 == 0) {
                    if (!primulGasit) {
                        primulPar = numar;
                        primulGasit = true;
                    } else {
                        int produs = primulPar * numar;
                        System.out.println("Firul " + alegere + " : " + primulPar + " * " + numar + " = " + produs);
                        primulGasit = false;
                    }
                }
            }
        }
    }
}
