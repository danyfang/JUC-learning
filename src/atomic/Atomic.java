package atomic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Atomic {


    public static void atomicIntegerExample() {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        System.out.println(atomicInteger.updateAndGet(v -> v + 11110));
    }


    public static void atomicUpdaterExample () {
        Student student = new Student();

        AtomicReferenceFieldUpdater updater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

        for (int i = 0; i < 10; ++i) {
            new Thread(() -> {
                System.out.println(updater.compareAndSet(student, null, "Danny"));
                System.out.println(student.name);
            }).start();
        }
    }


    public static void atomicArrayExample() {
        demo(
                () -> new int[10],
                (array) -> array.length,
                (array, index) -> array[index]++,
                array -> System.out.println(Arrays.toString(array))
        );


        demo(
                () -> new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array, index) -> array.getAndIncrement(index),
                array -> System.out.println(array)
        );
    }




    /**
     * A helper function used for demo
     * Supplier, 无中生有 () -> 结果
     * function 函数， 一个参数，一个结果 (参数) -> 结果
     * consumer 消费者， 一个参数，没有结果 (参数) -> void
     * */

    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer) {

        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();
        int length = lengthFun.apply(array);
        for (int i = 0; i < length; ++i) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; ++j) {
                    putConsumer.accept(array, j % length);
                }
            }));
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        printConsumer.accept(array);
    }
}

class Student {
    volatile String name;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}
