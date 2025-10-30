package seminare;
/* soldati la realizare 2 producatori si 3 consumatori . Producatorii produc numere intregi de la 10 pana la 130 . Producatorii impreuna trebuie sa produca 50 de numere intregi 
 * Consumatorii trebuie sa consume toate numerele produse de producatori. Fiecare producator produce cate un obiect de fiecare data si fiecare consumator
 * consuma cate un obiect de fiecrare data . Marimea depozitului este 11 elemente .
*/
public class ProducerConsumer {
    public static void main(String[] args) throws Exception { 
        Depozit depozit = new Depozit();
        Producator p1 = new Producator(depozit, 1);
        Producator p2 = new Producator(depozit, 2);
        Consumator c1 = new Consumator(depozit, 1);
        Consumator c2 = new Consumator(depozit, 2);
        Consumator c3 = new Consumator(depozit, 3);
        
        p1.start();
        p2.start();
        c1.start();
        c2.start();
        c3.start();
          
    }
}
class Depozit {
    int[] buffer = new int[11];
    int count = 0;
    synchronized void produce(int val) {
        while (count == buffer.length) {
            try {    
                wait();
             } catch (InterruptedException e) {
                    e.printStackTrace();
            } 
        }
            buffer[count] = val;
            count++;
            notifyAll();
        
        }
    
    synchronized int cosuma(){
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
            count--;
            int val = buffer[count];
            notifyAll();
            return val;
        
    }
}
class Producator extends Thread {
    Depozit depozit;
    int id;
    static int count = 1;
    Producator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }
    @Override
    public void run() {
        int valoareaIntreaga;
        while (count != 50) {
            valoareaIntreaga = (int)(Math.random() * 120) + 10;
            count++;
                depozit.produce(valoareaIntreaga);
                System.out.println("Producatorul " + id + " a produs valoarea: " + valoareaIntreaga);
                try {
                    Thread.sleep((int) (Math.random() * 600) + 50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        }
    }
class Consumator extends Thread {
    Depozit depozit;
    int id;
    static int count = 1;

    Consumator(Depozit d, int id) {
        this.depozit = d;
        this.id = id;
    }
    @Override
    public void run() {
        int valoareaIntreaga;
        while (count != 50) {
            valoareaIntreaga = depozit.cosuma();
            count++;
                System.out.println("Consumatorul " + id + " a consumat valoarea: " + valoareaIntreaga);
                try {
                    Thread.sleep((int) (Math.random() * 300) + 50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        }
}
     