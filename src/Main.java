import atomic.Atomic;
import executor.ThreadPool;
import immutable.Pool;

public class Main {

    volatile static int x = 0;
    static Object m = new Object();
    public static void main(String[] args) {
        //lock.GuardedObject.GuardedObjectExample.runExample();
        //lock.GuardedObject.MailBoxes.runExample();
        //lock.DeadLock.runExample();
        //lock.PhilosopherDinning.runExample();
        //lock.ReentranntLockCondition.runExample();
        //OderControl.runWaitNotify();
        //OderControl.runReentranntLock();
        //lock.OrderControl.runParkUnPark();
        //lock.RepeatPrint.runRepeatPrintWaitNofity();
        //lock.RepeatPrint.runRepeatPrintReentrannt();
        //lock.RepeatPrint.runRepeatPrintPark();
        //MessageQueue.runMessageQueueExample();

        //Atomic.atomicIntegerExample();
        //Atomic.atomicUpdaterExample();
        //Atomic.atomicArrayExample();

        //Pool.runPoolExample();
        //ThreadPool.runThreadPoolExample();

        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread());
        });

        t1.run();

        //ThreadPool.runFixedThreadPoolExample();
        //ThreadPool.runCachedThreadPoolExample();
        try {
            ThreadPool.runThreadPoolFutureExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
