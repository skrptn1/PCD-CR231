public class lab3 {
    public static void main(String[] args){

        int[] a = new int[100];
        for(int i=0; i<100; i++) a[i] = i+1;

        ThreadS2 t2 = new ThreadS2(a, "Pavel");
        ThreadS4 t4 = new ThreadS4(t2, "CR-231");

        t2.start();
        t4.start();
    }
}

class ThreadS2 extends Thread {
    private int[] a;
    private String nume;

    public ThreadS2(int[] a, String nume){
        this.a = a;
        this.nume = nume;
    }

    @Override
    public void run() {
        int cnt = 0;
        int sum = 0;

        for(int i = a.length-1; i>=0; i--){
            if(a[i] % 2 == 0){
                cnt++;
                sum += i;
                if(cnt == 2){
                    System.out.print("\n"+currentThread().getName()+" - suma:"+sum+" ");
                    cnt = 0;
                    sum = 0;
                    try{
                        Thread.sleep(50);
                    }catch(Exception e){}
                }
            }
        }

        try{
            Thread.sleep(1500);
        }catch(Exception e){}

        System.out.print("\n"+currentThread().getName()+" - ");
        for(char c : nume.toCharArray()){
            System.out.print(c);
            try{
                Thread.sleep(100);
            }catch(Exception e){}
        }
        System.out.println();
    }
}

class ThreadS4 extends Thread {
    private ThreadS2 t2;
    private String grupa;

    public ThreadS4(ThreadS2 t2, String grupa){
        this.t2 = t2;
        this.grupa = grupa;
    }

    @Override
    public void run() {

        for(int i=700; i>=300; i--){
            if(i % 20 == 0){
                System.out.print("\n"+currentThread().getName()+" - ");
            }
            System.out.print(i+" ");
            try{
                Thread.sleep(5);
            }catch(Exception e){}
        }
        System.out.println();

        Thread.yield();

        while(t2.isAlive()){
            try{
                Thread.sleep(5);
            }catch(Exception e){}
        }

        System.out.println();
        System.out.print(currentThread().getName()+" - ");
        for(char c : grupa.toCharArray()){
            System.out.print(c);
            try{
                Thread.sleep(100);
            }catch(Exception e){}
        }
        System.out.println();
    }
}
