package executor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadStarvation {

    static final List<String> MENU = Arrays.asList("Chichken", "Fish", "Prok", "Beef");
    static Random RANDOM = new Random();
    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void runExample() {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(() -> {
            System.out.println("Ordering");

            Future<String> f = pool.submit(() -> {
                System.out.println("Cooking");
                return cooking();
            });

            try {
                System.out.println(f.get() + " is ready");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        pool.execute(() -> {
            System.out.println("Ordering");

            Future<String> f = pool.submit(() -> {
                System.out.println("Cooking");
                return cooking();
            });

            try {
                System.out.println(f.get() + " is ready");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

}
