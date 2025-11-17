import javax.print.attribute.standard.PrinterMakeAndModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/*
    3 prod 4 cons, depozit de 14 obiecte, se produc 52 obiecte simboluri inafara de litere si cifre, cons sa consume obiectele produse
    de realizat problema cu pool de threaduri
     */

public class sem5 {

    static final int BUFFER_SIZE = 14;
    static final int PRODUCER = 3;
    static final int CONSUMER = 4;
    static final int TOTAL_ITEMS = 52;

    public static void main(String[] args) {

        BlockingQueue<Character> queue = new ArrayBlockingQueue<>(BUFFER_SIZE);
        AtomicInteger producedCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER + CONSUMER);

        for (int i = 0; i < PRODUCER; i++)
            executor.submit(new Producter6(i, queue, producedCount));

        for (int i = 0; i < CONSUMER; i++)
            executor.submit(new Consumator6(i, queue, producedCount));

        executor.shutdown();
    }
}

class Producter6 implements Runnable {

    private final int id;
    private final BlockingQueue<Character> queue;
    private final AtomicInteger producedCount;
    private final String simboluri = "!@#$%^&*()_+=-{}[]:;<>?/|~`.,";
    private final Random random = new Random();

    public Producter6(int id, BlockingQueue<Character> queue, AtomicInteger producedCount) {
        this.id = id;
        this.queue = queue;
        this.producedCount = producedCount;
    }

    @Override
    public void run() {
        while (true) {
            int current = producedCount.incrementAndGet();
            if (current > sem5.TOTAL_ITEMS) break;

            char simbol = simboluri.charAt(random.nextInt(simboluri.length()));

            try {
                queue.put(simbol);
                System.out.println(
                        "[PRODUCATOR " + id + "]  A produs: '" + simbol +
                                "'   |   Depozit: " + queue.size() + "/14"
                );
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

class Consumator6 implements Runnable {

    private final int id;
    private final BlockingQueue<Character> queue;
    private final AtomicInteger producedCount;

    public Consumator6(int id, BlockingQueue<Character> queue, AtomicInteger producedCount) {
        this.id = id;
        this.queue = queue;
        this.producedCount = producedCount;
    }

    @Override
    public void run() {
        while (true) {

            if (producedCount.get() >= sem5.TOTAL_ITEMS && queue.isEmpty()) break;

            try {
                char simbol = queue.take();

                System.out.println(
                        "    [CONSUMATOR " + id + "]  A consumat: '" + simbol +
                                "'   |   Depozit: " + queue.size() + "/14"
                );

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}