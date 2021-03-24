package executor;

import util.Sleeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPool {

    private BlockingQueue<Runnable> queue;

    private HashSet<Worker> workers = new HashSet<>();

    private int coreSize;

    private long timeOut;

    private TimeUnit timeUnit;

    private RejectPolicy rejectPolicy;

    public ThreadPool(int coreSize, long timeOut, TimeUnit timeUnit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.queue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }


    public void execute(Runnable task) {
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                System.out.println("New worker " + worker);
                workers.add(worker);
                worker.start();
            } else {
                System.out.println("Task added " + task);

                /**
                 * 当队列满了，我们有很多选择该怎么做
                 * 1.继续死等
                 * 2.超时等待
                 * 3.让调用者放弃任务执行
                 * 4.让调用者抛出异常
                 * 5.让调用者自己执行任务
                 *
                 *
                 * 选择把执行的操作决定权给到调用者自己，可以用 functional interface
                 * 策略模式
                 * */
                //queue.put(task); //1
                queue.tryPut(rejectPolicy, task);
            }
        }
    }


    public static void runThreadPoolExample() {
        ThreadPool pool = new ThreadPool(2, 1000, TimeUnit.MILLISECONDS, 2, ((queue1, task) -> {
            /**
             * 当队列满了，我们有很多选择该怎么做
             * 1.继续死等
             * 2.超时等待
             * 3.让调用者放弃任务执行
             * 4.让调用者抛出异常
             * 5.让调用者自己执行任务
             *
             *
             * 选择把执行的操作决定权给到调用者自己，可以用 functional interface
             * 策略模式
             * */
            //queue1.put(task); // 1
            //queue1.offer(task, 1500, TimeUnit.MILLISECONDS);//2
            //System.out.println("Give up"); // 3
            //throw new RuntimeException("Executing failed " + task);
            task.run();
        }));

        for (int i = 0; i < 5; ++i) {
            int j = i;
            pool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(j);
            });
        }
    }


    public static void runFixedThreadPoolExample() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Execution finished " + Thread.currentThread());
        });
        pool.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Execution finished " + Thread.currentThread());
        });
        pool.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Execution finished " + Thread.currentThread());
        });
        pool.shutdown();
    }

    public static void runCachedThreadPoolExample() {
        ExecutorService pool = Executors.newCachedThreadPool();

        SynchronousQueue<Integer> queue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println("Putting 1");
                queue.put(1);
                System.out.println("Putting 2");
                queue.put(2);
                System.out.println("Putting 3");
                queue.put(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Sleeper.sleep(1);
        new Thread(() -> {
            try{
                System.out.println("Taking " + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Sleeper.sleep(2);
        new Thread(() -> {
            try{
                System.out.println("Taking " + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Sleeper.sleep(3);
        new Thread(() -> {
            try{
                System.out.println("Taking " + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void runThreadPoolFutureExample() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    Thread.sleep(1000);
                    return "1";
                },
                () -> {
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    Thread.sleep(1000);
                    return "3";
                }
        ));
        for (Future<String> future : futures) {
            System.out.println(future.get());
        }


        pool.shutdown();
    }



    class Worker extends Thread{
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            while (task != null || (task = queue.poll(timeOut, timeUnit)) != null) {
                try {
                    System.out.println("Executing task " + task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (workers) {
                System.out.println("Worker removed " + this);
                workers.remove(this);
            }
        }
    }
}


@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}