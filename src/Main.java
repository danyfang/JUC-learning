import atomic.Atomic;

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
        Atomic.atomicArrayExample();
    }
}
