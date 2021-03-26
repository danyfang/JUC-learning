import util.Sleeper;

import java.util.LinkedList;

public class MessageQueue {

    private int capacity;
    private LinkedList<Message> queue = new LinkedList<>();

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    public Message take() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                System.out.println("MessageQueue is empty");
                try{
                    queue.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Message result = queue.removeFirst();
            queue.notifyAll();

            return result;
        }
    }

    /**
     * 要理解为什么给 queue 加锁而不是 this，就要从 CR 的分析开始
     * 问题是多个线程对 this 对象的读写造成冲突吗？ 不是，是对里面的
     * queue 对象的读写造成的冲突
     * this 对象里只有一个 queue 的引用，这个锁住了有什么用？
     * 如果共享的变量是 this 的一个成员变量，比如说 int 类型的，
     * 那么可以锁住 this
     *
     * 很简单：做一下逃逸分析，因为 take 和 put 方法都是完全可以
     * 被不同的线程所调用的，也就是逃出了创建它的线程栈
     * */
    public void put(Message message) {
        synchronized (queue) {// think about why this should be queue instead of this
            while(queue.size() == capacity) {
                System.out.println("MessageQueue is full");
                try{
                    queue.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            queue.addLast(message);
            queue.notifyAll();
        }
    }


    public static void runMessageQueueExample() {
        MessageQueue queue = new MessageQueue(10);

        // We use two threads to put and one thread to fetch

        new Thread(() -> {
            while(true) {
                Sleeper.sleep(1);
                queue.put(new Message(1, "message from thread 1"));
            }
        }).start();

        new Thread(() -> {
            while(true) {
                Sleeper.sleep(1);
                queue.put(new Message(1, "message from thread 2"));
            }
        }).start();

        new Thread(() -> {
            while (true) {
                Sleeper.sleep(1);
                Message message = queue.take();
                System.out.println(message.content);
            }
        }).start();
    }
}


final class Message {
    int id;
    Object content;

    public Message(int id, Object content) {
        this.id = id;
        this.content = content;
    }
}
