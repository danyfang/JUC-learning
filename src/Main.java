import atomic.Atomic;
import container.CopyOnWriteArrayListExample;
import executor.ThreadPool;
import executor.ThreadStarvation;
import flyweight.FlyWeightPattern;
import immutable.Pool;
import task.FutureTaskExample;

public class Main {

    volatile static int x = 0;
    static Object m = new Object();
    public static void main(String[] args) throws Exception{
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

        //ThreadPool.runFixedThreadPoolExample();
        //ThreadPool.runCachedThreadPoolExample();
        //ThreadPool.runThreadPoolFutureExample();

        //CPULoadExample();
        /*
        Object object = new Object();
        new Thread(() -> {
            synchronized (object) {
                System.out.println(object.hashCode());
            }
        }).start();
         */

        //FlyWeightPattern.runExample();

        //ThreadSafeClassExample.demo();

        //CopyOnWriteArrayListExample.runExample();
        //FutureTaskExample.runExample();
        ThreadStarvation.runExample();
    }

    public static void CPULoadExample() {
        new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
