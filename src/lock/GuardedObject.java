package lock;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class GuardedObject {
    private int id;
    private Object response;

    public GuardedObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object get(long timeout) {
        synchronized (this) {
            long begin = System.currentTimeMillis();

            long passedTime = 0;

            while(response == null) {
                long waitTime = timeout - passedTime;

                if (waitTime < 0) {
                    break;
                }

                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }

   public void complete(Object response) {
        synchronized (this) {
            this.response = response;
            this.notifyAll();
        }
    }


    public static class GuardedObjectExample {
        private static GuardedObject guardedObject = new GuardedObject(1);
        public static void runExample() {
            new Thread(() -> {
                System.out.println("Start waiting for results");
                Object response = guardedObject.get(10000);
                System.out.println(Thread.currentThread().getName() + " Got the results " + response.toString());
            }, "Thread 1").start();

            new Thread(() -> {
                System.out.println("Start waiting for results");
                Object response = guardedObject.get(10000);
                System.out.println(Thread.currentThread().getName() + " Got the results " + response.toString());
            }, "Thread 3").start();

            new Thread(() -> {
                System.out.println("Start downloading...");
                //Simulate time consuming task
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                guardedObject.complete(new Object());
            }, "Thread 2").start();
        }
    }

    public static class MailBoxes {
        private static Map<Integer, GuardedObject> boxes = new Hashtable<>();

        private static int id = 1;
        private static synchronized int generateId() {
            return id++;
        }

        public static GuardedObject createGuardedObject() {
            GuardedObject go = new GuardedObject(generateId());
            boxes.put(go.getId(), go);
            return go;
        }

        public static Set<Integer> getIds() {
            return boxes.keySet();
        }

        public static GuardedObject getGuardedObject(int id) {
            //Synchronized is not needed because this method is thread safe
            return boxes.get(id);
        }


        public static void runExample() {
            for (int i = 0; i < 3; ++i) {
                new Citizen().start();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int id : MailBoxes.getIds()) {

                new Postman(id, id + ", you've got mail").start();
            }
        }


    }


    static class Citizen extends Thread {
        @Override
        public void run() {
            GuardedObject guardedObject = MailBoxes.createGuardedObject();
            System.out.println("Citizen expecting mail..." + guardedObject.id);
            Object mail = guardedObject.get(5000);
            System.out.println("Mail arriving..." + guardedObject.id);
            System.out.println(mail);
        }
    }

    static class Postman extends Thread {
        private int id;
        private String mail;
        public Postman(int id, String mail) {
            this.id = id;
            this.mail = mail;
        }
        @Override
        public void run() {
            System.out.println("Mailman delivering mail..." + id);
            GuardedObject go = MailBoxes.getGuardedObject(id);
            go.complete(mail);
        }
    }
}
