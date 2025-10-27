package lab1Mirela;
public class laborator1 {
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
        FirThread t3 = new FirThread(3, mas);
        FirThread t4 = new FirThread(4, mas);

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
            System.out.println("Firul a fost întrerupt!");
        }

        String name = "Furtuna Rodion CR-231\nMaletchi Mirela CR-231";
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
                        System.out.println("FIRUL " + alegere + " : " + primulPar + " * " + numar + " = " + produs);
                        primulGasit = false;
                    }
                }
            }
        }
    }
}

class FirThread extends Thread {
    int alegere;
    int[] mas;

    public FirThread(int id, int[] mas) {
        this.alegere = id;
        this.mas = mas;
    }


    public void run() {
        if (alegere == 3) {
            int i = 0;
            while (i < mas.length - 1) {
                if (mas[i] % 2 == 0) {
                    for (int j = i + 1; j < mas.length; j++) {
                        if (mas[j] % 2 == 0) {
                            int produs = mas[i] * mas[j];
                            System.out.println("Thread " + alegere + " - " + mas[i] + " * " + mas[j] + " = " + produs);
                            i = j; 
                            break;
                        }
                    }
                }
                i++;
            }

        } else if (alegere == 4) {
            for (int i = mas.length - 1; i > 0; i--) {
                if (mas[i] % 2 == 0) {
                    for (int j = i - 1; j >= 0; j--) {
                        if (mas[j] % 2 == 0) {
                            int produs = mas[i] * mas[j];
                            System.out.println("Thread " + alegere + " - " + mas[i] + " * " + mas[j] + " = " + produs);
                            i = j; 
                            break;
                            //iesirea din ciclu
                        }
                    }
                }
            }
        }
    }
}