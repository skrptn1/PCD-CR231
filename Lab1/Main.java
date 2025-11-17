public class Main {
    public static void main(String[] args) {
        TaskStartForward t1, t3; 
        TaskStartBackward t2, t4; 
        NameThread nameThread;

        int mas[] = new int[100];
        for (int i = 0; i < mas.length; i++) {
            mas[i] = (int) (Math.random() * 100) + 1; 
            System.out.print(mas[i] + " ");
        }
        System.out.println();
        System.out.println("---------------");

        t1 = new TaskStartForward(mas);  
        t3 = new TaskStartForward(mas);  
        t2 = new TaskStartBackward(mas); 
        t4 = new TaskStartBackward(mas); 

        nameThread = new NameThread("Mocreac Cristian, Untila Maxim - Grupul 5");

<<<<<<< HEAD
        t1.setName("Maxim-Unu");      t1.start();
        t3.setName("Maxim-Doi");      t3.start();
        t2.setName("Th3");    t2.start();
        t4.setName("Th4");   t4.start();
=======
        t1.setName("Th1");      t1.start();
        t3.setName("Th3");      t3.start();
        t2.setName("Cristi-Trei");    t2.start();
        t4.setName("Cristi-Patru");   t4.start();
>>>>>>> 332ce75b2a623395dc6ae4324601f1ac95bb2811

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        nameThread.start();
    }
}

class TaskStartForward extends Thread {
    int[] mas;

    public TaskStartForward(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        int suma = 0;
        for (int i = 0; i + 2 < mas.length; i += 4) {
            int a = mas[i];      
            int b = mas[i + 2];   
            int produs = a * b;
            suma += produs;
            System.out.println(Thread.currentThread().getName()
                    + " (Sarcina 1) pereche (" + i + "," + (i + 2) + ") = "
                    + a + "*" + b + " = " + produs
                    + " | suma curentă = " + suma);
        }
        System.out.println(Thread.currentThread().getName()
                + " (Sarcina 1) SUMA FINALA = " + suma);
    }
}

class TaskStartBackward extends Thread {
    int[] mas;

    public TaskStartBackward(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        int start = (mas.length - 1);
        if (start % 2 != 0) {
            start--; 
        }

        int suma = 0;
        for (int i = start; i - 2 >= 0; i -= 4) {
            int a = mas[i];       
            int b = mas[i - 2];   
            int produs = a * b;
            suma += produs;
            System.out.println(Thread.currentThread().getName()
                    + " (Sarcina 2) pereche (" + i + "," + (i - 2) + ") = "
                    + a + "*" + b + " = " + produs
                    + " | suma curentă = " + suma);
        }
        System.out.println(Thread.currentThread().getName()
                + " (Sarcina 2) SUMA FINALA = " + suma);
    }
}

class NameThread extends Thread {
    String name;

    public NameThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < name.length(); i++) {
            System.out.print(name.charAt(i));
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
