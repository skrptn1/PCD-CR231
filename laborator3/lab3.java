public class lab3 {
    public static void main(String[] args) {

        Th1 fir1 = new Th1();
        Th2 fir2 = new Th2();

        fir1.start();
        fir2.start();

        try {
            fir1.join();
            fir2.join();
            
        } catch (InterruptedException e) {
            System.out.println("Firul a fost intrerupt!");
        }
    }
}

class Th1 extends Thread {
    public void run() {
        int primulPar = 0;
        boolean primulGasit = false;

        for (int i = 1; i <= 100; i++) {
            if (i % 2 == 0) {
                if (!primulGasit) {
                    primulPar = i;
                    primulGasit = true;
                } else {
                    int produs = primulPar * i;
                    System.out.println("Th1: " + primulPar + " * " + i + " = " + produs);
                    primulGasit = false;

                    Thread.yield();

                    try { Thread.sleep(150); } 
                    catch (InterruptedException e) { }
                }
            }
        }
       synchronized(System.out) {
        String name = "Rodion, Mirela";
        for (int i = 0; i < name.length(); i++) {
        System.out.print(name.charAt(i));
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }
    System.out.println();
}

        Thread.currentThread().interrupt();
    }
}

class Th2 extends Thread {
    public void run() {
        int primulPar = 0;
        boolean primulGasit = false;

        for (int i = 100; i >= 1; i--) {
            if (i % 2 == 0) {
                if (!primulGasit) {
                    primulPar = i;
                    primulGasit = true;
                } else {
                    int produs = primulPar * i;
                    System.out.println("Th2: " + primulPar + " * " + i + " = " + produs);
                    primulGasit = false;

                    Thread.yield();

                    try { Thread.sleep(150); } 
                    catch (InterruptedException e) { }
                }
            }

            if (Thread.interrupted()) {
                System.out.println("Th2 a fost întrerupt de Th1!");
                break;
            }
        }

        synchronized(System.out) {
        String name = "Furtuna, Maletchi";
        for (int i = 0; i < name.length(); i++) {
        System.out.print(name.charAt(i));
        try { Thread.sleep(100); } catch (InterruptedException e) {}
     }
        System.out.println();
    }

    }
}
