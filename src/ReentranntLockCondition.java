import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentranntLockCondition {
    private static ReentrantLock room = new ReentrantLock();

    private static boolean hasTakeout = false;
    private static boolean hasCigarette = false;

    static Condition waitTakeoutSet = room.newCondition();
    static Condition waitCigaretteSet = room.newCondition();

    public static void runExample() {
        new Thread(() -> {
            room.lock();
            try {
                while(!hasTakeout) {
                    System.out.println("No takeout yet, waiting...");
                    try {
                        waitTakeoutSet.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (hasTakeout) {
                    System.out.println("Takeout arrives");
                    System.out.println("Start doing things");
                }
            } finally {
                room.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            room.lock();
            try {
                while(!hasCigarette) {
                    System.out.println("No cigarette yet, waiting...");
                    try {
                        waitCigaretteSet.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (hasCigarette) {
                    System.out.println("Cigarette arrives");
                    System.out.println("Taking a rest");
                }
            } finally {
                room.unlock();
            }
        }, "t2").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            room.lock();

            try {
                hasTakeout = true;
                waitTakeoutSet.signal();
            } finally {
                room.unlock();
            }
        }, "t3").start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            room.lock();

            try {
                hasCigarette = true;
                waitCigaretteSet.signal();
            } finally {
                room.unlock();
            }
        }, "t4").start();
    }

}
