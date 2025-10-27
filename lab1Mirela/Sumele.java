public class Sumele {
    public static void main (String [] args){
        


            int[] mas = new int[100];

        for (int i=0; i<mas.length; i++) {
            mas[i] = (int)(Math.random() * 100) + 1;
        }

         System.out.println();
         System.out.println();
        

        FirThread t3 = new FirThread(3, mas);
        FirThread t4 = new FirThread(4, mas);

    

         t3.start();
      

        System.out.println();

       t4.start();
        try {
            t3.join();
            t4.join(); 
        } catch (InterruptedException e) {
            System.out.println("Firul a fost întrerupt!");
        }
           

        System.out.println();
            String nume = "Maletchi Mirela CR-231";
            for (int i = 0; i < nume.length(); i++) {
            System.out.print(nume.charAt(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

        System.out.println();
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
                        }
                    }
                }
            }
        }
    }
}