public class ThreadState {
    private static int value = 0;
    public static void printState() {

        //NEW
        Thread t1 = new Thread(() -> {
            try{
                value += 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //RUNNABLE
        Thread t2 = new Thread(() -> {
            while(true) {
                value += 1;
                value -= 1;
            }
        });
        t2.start();

        //TERMINATED
        Thread t3 = new Thread(() -> {
            value += 10;
        });
        t3.start();

        //TIMED WAITING
        Thread t4 = new Thread(() -> {
            try{
                Thread.sleep(1000000);
                value += 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t4.start();

        //WAITING
        Thread t5 = new Thread(() -> {
            synchronized (ThreadState.class) {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t5.start();

        //BLOCKED
        Thread t6 = new Thread(() -> {
            synchronized (ThreadState.class) {
                while(true) {
                    value++;
                }
            }
        });
        t6.start();




        System.out.println(t1.getState());
        System.out.println(t2.getState());
        System.out.println(t3.getState());
        System.out.println(t4.getState());
        System.out.println(t5.getState());
        System.out.println(t6.getState());
    }
}
