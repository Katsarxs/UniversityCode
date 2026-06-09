package lab4;

import java.util.Random;
import java.util.concurrent.*;

/*
 * Printing a random list of integers with threads using ExecutorService.
 * We create an instance of ExecutorService, create a thread implementing Callable.
 * Then we create 2 tasks and submit them to Executor.
 * We store the future results of the tasks using Future.
 */

public class ThreadFindingMax {
    public static void main(String[] args) {
        int[] data = new int[100000];
        Random random = new Random();

        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextInt(100);
            System.out.print(data[i] + " ");
        }

        try {
            int max = ThreadFindTask.max(data);
            System.out.println("\n" + max);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class FindMaxTask implements Callable<Integer> {
    private int[] data;
    private int start;
    private int end;

    FindMaxTask(int[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    public Integer call() {
        int max = Integer.MIN_VALUE;
        for (int i = start; i < end; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }
}

class ThreadFindTask {
    public static int max(int[] data) throws ExecutionException, InterruptedException {
        if (data.length == 1) {
            return data[0];
        } else if (data.length == 0) {
            System.out.println("Must have at least one number");
        }

        FindMaxTask task1 = new FindMaxTask(data, 0, data.length / 2);
        FindMaxTask task2 = new FindMaxTask(data, data.length / 2, data.length);

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future1 = service.submit(task1);
        Future<Integer> future2 = service.submit(task2);

        int max = Integer.max(future1.get(), future2.get());
        service.shutdown();
        return max;
    }
}