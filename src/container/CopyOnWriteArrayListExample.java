package container;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CopyOnWriteArrayListExample {

    public static class ReadTask implements Runnable {
        List<String> list;

        public ReadTask(List<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (String s : list) {
                System.out.println(s);
            }
        }
    }

    public static class WriteTask implements Runnable {
        List<String> list;
        int index;

        public WriteTask(List<String> list, int index) {
            this.list = list;
            this.index = index;
        }

        @Override
        public void run() {
            list.remove(index);
            list.add(index, "write_" + index);
        }
    }


    public static void runExample() {
        final int NUM = 10;

        List<String> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < NUM; ++i) {
            list.add("main_" + i);
        }

        ExecutorService pool = Executors.newFixedThreadPool(NUM);
        for (int i = 0; i < NUM; ++i) {
            pool.execute(new ReadTask(list));
            pool.execute(new WriteTask(list, i));
        }

        pool.shutdown();
    }
}
