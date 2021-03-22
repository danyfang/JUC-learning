import java.util.concurrent.locks.ReentrantLock;

public class PhilosopherDinning {
    public static void runExample() {
        // five Philosophers

        //runDeadLock();
        runReentrantLock();
    }

    private static void runReentrantLock() {
        ChopstickReentrant c1 = new ChopstickReentrant("chopstick 1");
        ChopstickReentrant c2 = new ChopstickReentrant("chopstick 2");
        ChopstickReentrant c3 = new ChopstickReentrant("chopstick 3");
        ChopstickReentrant c4 = new ChopstickReentrant("chopstick 4");
        ChopstickReentrant c5 = new ChopstickReentrant("chopstick 5");

        PhilosopherReentrant p1 = new PhilosopherReentrant("Aristotle", c1, c2);
        PhilosopherReentrant p2 = new PhilosopherReentrant("Plato", c2, c3);
        PhilosopherReentrant p3 = new PhilosopherReentrant("Socrates", c3, c4);
        PhilosopherReentrant p4 = new PhilosopherReentrant("Heraclitus", c4, c5);
        PhilosopherReentrant p5 = new PhilosopherReentrant("Epicurus", c5, c1);


        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
    }

    private static void runDeadLock() {
        ChopStick c1 = new ChopStick("chopstick 1");
        ChopStick c2 = new ChopStick("chopstick 2");
        ChopStick c3 = new ChopStick("chopstick 3");
        ChopStick c4 = new ChopStick("chopstick 4");
        ChopStick c5 = new ChopStick("chopstick 5");


        Philosopher p1 = new Philosopher("Aristotle", c1, c2);
        Philosopher p2 = new Philosopher("Plato", c2, c3);
        Philosopher p3 = new Philosopher("Socrates", c3, c4);
        Philosopher p4 = new Philosopher("Heraclitus", c4, c5);
        Philosopher p5 = new Philosopher("Epicurus", c5, c1);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
    }
}

class PhilosopherReentrant extends Thread {
    String name;
    ChopstickReentrant left;
    ChopstickReentrant right;


    public PhilosopherReentrant(String name, ChopstickReentrant left, ChopstickReentrant right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while(true) {
            if (left.tryLock()) {
                try {
                    if (right.tryLock()) {
                        try {
                            eat();
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();
                }
            }
        }
    }

    private void eat() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + " is eating");
    }

}

class ChopstickReentrant extends ReentrantLock {
    String name;

    public ChopstickReentrant(String name) {
        this.name = name;
    }
}

class Philosopher extends Thread{
    String name;
    ChopStick left;
    ChopStick right;

    public Philosopher(String name, ChopStick left, ChopStick right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while(true) {
            // following is dangerous
            synchronized (left) {
                synchronized (right) {
                    eat();
                }
            }
        }
    }

    private void eat() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + " is eating");
    }
}


class ChopStick {
    String name;

    public ChopStick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChopStick{" +
                "name='" + name + '\'' +
                '}';
    }
}
