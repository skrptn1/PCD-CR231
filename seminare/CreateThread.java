package seminare;

import java.util.Random;

/* 
 * Sarcina - de creat 2 fire de executie care sa aprecieze produsele numerelor mai mici ca 20 (le extrage pe ecran) dintr-un sir de numere aleatorii in intervalul de 0 pana la 50 
 * Sirul contine 70 de numere 
 * 1) Primul sir de caractere parcurge sirul de la inceput 
 * 2) Al doilea parcurge sirul de la sfarsit   
*/
public class CreateThread {
    public static void main(String[] args) {

        int Numbers[] = new int[70];
        for (int i = 0; i < 70; i++) {
            Numbers[i] = (int) (Math.random() * 50);
            System.out.print(" " + Numbers[i]);
        }
        System.out.println();
        Thread1 thread1 = new Thread1(0, 70, Numbers,1);
        Thread1 thread2 = new Thread1(69, 0, Numbers,-1);
        thread1.start();
        thread2.start();
    }

}

class Thread1 extends Thread {
    int from;
    int to;
    int Numbers[] = new int[70];
    int step;

    public Thread1(int from, int to, int Numbers[], int step) {
        this.from = from;
        this.to = to;
        this.Numbers = Numbers;
        this.step=step;
    }

    @Override
    public void run() {
        
            int produs = 1;
            int counter = 0;
            int i=from;
            while(i!=to) {
                if (Numbers[i] < 20) {
                    produs = produs * Numbers[i];
                    counter++;
                    if (counter >= 2) {
                        System.out.println(currentThread().getName() + " Produsul: " + produs);
                        produs = 1;
                        counter = 0;
                    }
                }
                i=i+step;
            }
        
    }
}

