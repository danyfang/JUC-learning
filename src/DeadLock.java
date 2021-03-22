public class DeadLock {

    public static void runExample() {
        Object A = new Object();
        Object B = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (A) {
                System.out.println("A is locked");
                synchronized (B) {
                    System.out.println("gains lock of B");
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized (B) {
                System.out.println("B is locked");
                synchronized (A) {
                    System.out.println("gains lock of A");
                }
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
