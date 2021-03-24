import java.util.concurrent.FutureTask;

public class FutureTaskExample {

    public static void run() throws Exception{
        FutureTask<Integer> task = new FutureTask<Integer>(() -> {
            Thread.sleep(100);
            return 10;
        });

        new Thread(task, "ThreadName").start();

        System.out.println("The main thread is executing");

        System.out.println("Main thread tries to get value from another thread");

        // blocking here
        System.out.println(task.get());

    }
}
