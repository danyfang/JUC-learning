import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is to demo a few methods that can control the execution oder between two threads
 * A will always be printed after B
 * */

public class OrderControl {

    private static Object object = new Object();
    private static boolean flag = false;
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition waitContidion = lock.newCondition();

    public static void runWaitNotify() {

        new Thread(() -> {
            synchronized (object) {
                while(!flag) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("A");
            }
        }, "thread").start();

        new Thread(() -> {
            synchronized (object) {
                flag = true;
                System.out.println("B");
                object.notify();
            }
        }, "thread").start();
    }


    public static void runReentranntLock() {
        new Thread(() -> {
            lock.lock();
            try {
                while (!flag) {
                    try {
                        waitContidion.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("A");
            } finally {
                lock.unlock();
            }
        }, "thread").start();
        
        new Thread(() -> {
            lock.lock();
            try {
                flag = true;
                System.out.println("B");
                waitContidion.signal();
            } finally {
                lock.unlock();
            }
        }, "thread").start();
    }



    public static void runParkUnPark() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            System.out.println("A");
        }, "thread");
        t1.start();

        new Thread(() -> {
            System.out.println("B");
            LockSupport.unpark(t1);
        }, "thread").start();
    }
}
