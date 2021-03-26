import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeClassExample {
    /** We say that String class is thread safe, that means each of its method is thread safe
     *  The following is an example of Thread safe
     */
    static int i = 0;
    static AtomicInteger j = new AtomicInteger();


    public static void demo() throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        List<Callable<Integer>> iTasks = new ArrayList<>();
        for (int x = 0; x < 100; ++x) {
            iTasks.add(() -> {
                for (int y = 0; y < 1000; ++y) {
                    i++;
                }
                return null;
            });
        }

        List<Callable<Integer>> jTasks = new ArrayList<>();
        for (int x = 0; x < 100; ++x) {
            jTasks.add(() -> {
                for (int y = 0; y < 1000; ++y) {
                    j.incrementAndGet();
                }
                return null;
            });
        }

        pool.invokeAll(iTasks);
        pool.invokeAll(jTasks);

        System.out.println(i);
        System.out.println(j);

        pool.shutdown();
    }
}
