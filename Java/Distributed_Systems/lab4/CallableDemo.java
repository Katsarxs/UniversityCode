package lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/*
 * Calculating the cube of random integers using ExecutorService.
 * We store the future values also using Future.
 */

public class CallableDemo {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(4);
        List<Future<Integer>> listOfFutures = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            Cube cube = new Cube(random.nextInt(10));
            Future<Integer> response = service.submit(cube);
            listOfFutures.add(response);
        }

        for (Future<Integer> future : listOfFutures) {
            try {
                System.out.println("The future value : " + future.get());
                System.out.println("Task completed : " + future.isDone());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        service.shutdown();
    }
}

class Cube implements Callable {
    private int num;

    Cube(int num) {
        this.num = num;
    }

    @Override
    public Integer call() {
        int result = num * num * num;
        System.out.println("Cube of " + num + " is " + result);
        return result;
    }
}