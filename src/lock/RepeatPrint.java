package lock;

import util.Sleeper;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class RepeatPrint {


    public static void runRepeatPrintWaitNofity() {
        WaitNotify wn = new WaitNotify(1, 5);
        new Thread(() -> {
            wn.print("A", 1, 2);
        }).start();
        new Thread(() -> {
            wn.print("B", 2, 3);
        }).start();
        new Thread(() -> {
            wn.print("C", 3, 1);
        }).start();
    }


    public static void runRepeatPrintReentrannt() {
        AwaitSignal as = new AwaitSignal(5);
        Condition c1 = as.newCondition();
        Condition c2 = as.newCondition();
        Condition c3 = as.newCondition();

        new Thread(() -> {
            as.print("A", c1, c2);
        }, "thread").start();
        new Thread(() -> {
            as.print("B", c2, c3);
        }, "thread").start();
        new Thread(() -> {
            as.print("C", c3, c1);
        }, "thread").start();

        Sleeper.sleep(1);

        as.lock();
        try {
            System.out.println("Start...");
            c1.signal();
        } finally {
            as.unlock();
        }
    }

    private static Thread t1;
    private static Thread t2;
    private static Thread t3;
    public static void runRepeatPrintPark() {
        ParkUnPark parkUnPark = new ParkUnPark(5);
        t1 = new Thread(() -> {
            parkUnPark.print("A", t2);
        });
        t2 = new Thread(() -> {
            parkUnPark.print("B", t3);
        });
        t3 = new Thread(() -> {
            parkUnPark.print("C", t1);
        });

        t1.start();
        t2.start();
        t3.start();

        LockSupport.unpark(t1);
    }
}

class WaitNotify{
    private int flag;
    private int loopNumber;

    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    public void print(String s, int waitFLag, int nextFlag) {
        for (int i = 0; i < loopNumber; ++i) {
            synchronized (this) {
                while (flag != waitFLag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                flag = nextFlag;
                System.out.println(s);
                this.notifyAll();
            }
        }
    }
}


class AwaitSignal extends ReentrantLock {

    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String s, Condition room, Condition nextRoom) {
        for (int i = 0; i < loopNumber; ++i) {
            this.lock();
            try {
                room.await();
                System.out.println(s);
                nextRoom.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.unlock();
            }
        }
    }
}


class ParkUnPark {
    private int loopNumber;

    public ParkUnPark(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String s, Thread next) {
        for (int i = 0; i < loopNumber; ++i) {
            LockSupport.park();
            System.out.println(s);
            LockSupport.unpark(next);
        }
    }
}