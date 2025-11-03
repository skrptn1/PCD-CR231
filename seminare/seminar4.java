package seminare;
/* Sunt dati la realizare 2 proucatori si 3 consumatori.Producatorii produc numere intregi de la 10 pana la 130.
Producatorii impreuna Trebuie sa produca 50 de numere intregi.Consumatorii trebuie sa consume toate datele produse de producatori.
Fiecare producator produce cate 1 obiect  de fiecare date si fiecarre sonumator consuma cate 1 obiect de fiecare data.
Marimea depzitului este de 11 elemente.  */
public class seminar4 {
    public static void main(String[] args) {
        Depozit depozit = new Depozit();
        Producator p1 = new Producator(depozit,1);
        Producator p2 = new Producator(depozit,2);
        Consumator c1 = new Consumator(depozit,1);
        Consumator c2 = new Consumator(depozit,2);
        Consumator c3 = new Consumator(depozit,3);
        p1.start();
        p2.start();
        c1.start();
        c2.start();
        c3.start();
    }

}
class Depozit{
    int [] buffer = new int[11];
    int count =0;

     synchronized void produce(int val){
        while(count==buffer.length){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        buffer[count]=val;
        count++;
        notifyAll();

    }
    synchronized int consume(){
        while(count==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int val = buffer[count-1];
        count--;
        notifyAll();
        return val;
    }
    int getCount(){

        return 0;
    }
} 
class Producator extends Thread{
    Depozit depozit;
    int id;
    static int contor=1;
    public Producator(Depozit depozit, int id){
        this.depozit=depozit;
        this.id=id;
}
@Override
    public void run(){
        int numintreg = 0;
        while(contor !=50)
        numintreg = (int)(Math.random()*120)+10;
       contor++;
            depozit.produce(numintreg);
        System.out.println("Producatorul "+id+" a produs numarul "+numintreg);
        try{   
        Thread.sleep((int)(Math.random()*600)+50);
        }catch(InterruptedException e){
            e.printStackTrace();
        }



    }
}
    class Consumator extends Thread{
        Depozit depozit;
        int id;
        static int contor=1;
        public Consumator(Depozit depozit, int id){
            this.depozit=depozit;
            this.id=id;
    }
    @Override
        public void run(){
            int numintreg;
            while(contor !=50);
                numintreg = depozit.consume();
            
            contor++;
            System.out.println("Consumatorul "+id+" a consumat numarul "+numintreg);
            try{   
            Thread.sleep((int)(Math.random()*200)+50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
    
    
    
        }
}
