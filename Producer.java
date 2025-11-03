/* Sunt dati la realizare 2 producatori si 3 consumatori. Producatorii produc numere intregi
de la 10 pana la 130.
    Producatorii impreuna trebuie sa produca 50 de numere intregi
    Fiecare producator consuma un obiect de fiecare data, si fiecare consumator consuma un obiect de fiecare data.
    Consumatorii trebuie sa consume toate datele produse de producatori
    Marimea depozitului este de 11 elemente

* */


public class Producer {
    public static void main(String[] args) {
        Depozit depozit = new Depozit();
        Consumator consumator = new Consumator(depozit,1);
        Consumator consumator2 = new Consumator(depozit,2);
        Consumator consumator3 = new Consumator(depozit,3);
        Producator producator = new Producator(depozit,4);
        Producator producator2 = new Producator(depozit,5);

        producator.start();
        producator2.start();

        consumator.start();
        consumator2.start();
        consumator3.start();

    }
}

class Depozit {
    int[] buffer = new int [11];
    int count = 0;

    synchronized void produce(int val){
        while (count == buffer.length){
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        buffer[count++] = val;
        notifyAll();
    }
    synchronized int consume(){
        while (count == 0){
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        return buffer[--count];
    }
}

class Producator extends Thread {
    Depozit depo;
    int id;
    static int numarare=1;

    Producator(Depozit depo, int id){
        this.depo = depo;
        this.id = id;
    }

    @Override
    public void run() {
        int valINT;
        while(numarare!=50){
            valINT= (int)(Math.random()*121+10);
            numarare++;
            depo.produce(valINT);
            System.out.println("Producatorul cu idul:"+this.id+" a prodous valoarea:" + valINT);
            try {
                Thread.sleep((int)(Math.random()*551+50));
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }
}

class Consumator extends Thread {
    Depozit depo;
    int id;
    static int numarare=1;
    Consumator(Depozit depo, int id){
        this.depo = depo;
        this.id = id;
    }

    @Override
    public void run() {
        int valINT;
        while(numarare!=50){
            valINT= depo.consume();
            numarare++;
            System.out.println("Consumatorul cu idul:"+this.id+" a consumat valoarea:" + valINT);
            try {
                Thread.sleep((int)(Math.random()*251+50));
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }
}