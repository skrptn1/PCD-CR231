
public class seminar {
     public static void main(String[] args) {
        ThreadCifre t1=new ThreadCifre("Cifre");
        ThreadCifre t2=new ThreadCifre("Litere");
        t1.start();
        t1.setName("Cifre");
        //t1.setPriority(1);
        t2.setName("Litere");
        t2.start();
        try {
            t2.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         t2.interrupt();
        //t2.yield();
        }
     }
class ThreadCifre extends Thread{
    String nume;

    public ThreadCifre(String nume){
        this.nume=nume;
    }
    public void run(){
        int cif[] = { 0 , 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 };
        char lit[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j' };
        for (int i = 0; i < 20; i++) {
            while(Thread.interrupted()){
                System.out.println(" Firul "+Thread.currentThread().getName()+" a fost intrerupt");
                return;
            }
            if(Thread.currentThread().getName().equals("Cifre")){
                int c=(int)(Math.random()*10);
                int cifre=cif[c];
                System.out.println(" Cifre: " + Thread.currentThread().getName()+" firul a generat cifra "+cifre);
                
            }
            if(Thread.currentThread().getName().equals("Litere")){
                int l=(int)(Math.random()*10);
                char litere=lit[l];
                System.out.println(" Litere: " + Thread.currentThread().getName()+" firul a generat litera "+litere);
            }
        }
    }
}


