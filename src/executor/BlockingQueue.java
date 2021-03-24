package executor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

    private Deque<T> queue = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();

    private Condition producerSet = lock.newCondition();
    private Condition consumerSet = lock.newCondition();

    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while(queue.isEmpty()) {
                try {
                    // 等待剩余时间
                    nanos = consumerSet.awaitNanos(nanos);
                    if (nanos <= 0) {
                        return null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            producerSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    public T take() {
        lock.lock();
        try {
            while(queue.isEmpty()) {
                try {
                    consumerSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            producerSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    public void put(T element) {
        lock.lock();
        try {
            while(queue.size() == capacity) {
                try {
                    producerSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            consumerSet.signal();
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(T task, long timeOut, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeOut);
            while(queue.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    producerSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            consumerSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                queue.addLast(task);
                consumerSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
