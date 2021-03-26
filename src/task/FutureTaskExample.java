package task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FutureTaskExample {

    static int i;

    public static void runExample() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);


        List<FutureTask<Integer>> tasks = new ArrayList<>();

        for (i = 0; i < 100; ++i) {
            tasks.add(new FutureTask<>(() -> i));
        }


        for (int j = 0; j < 100; ++j) {
            pool.execute(tasks.get(j));
            System.out.println(tasks.get(j).get()); // results are all 100
        }
        pool.shutdown();

    }
}
