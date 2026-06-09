package lab4;

/*
 * Another example of join().
 * The 2 threads run but when t.join(), main thread wait for t.
 */

class JoinThread {
    public static void main(String[] args) throws Exception {
        new Thread(new MyTask()).start();
    }
}

public class OtherTask implements Runnable {
    private String taskName;
    private int counter;

    public OtherTask(String taskName, int counter) {
        this.taskName = taskName;
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 1; i <= counter; i++) {
            System.out.println(taskName + " " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class MyTask implements Runnable {
    @Override
    public void run() {
        try {
            Thread t = new Thread(new OtherTask("Other Task", 10));
            t.start();

            for (int i = 1; i <= 10; i++) {
                System.out.println("My Task " + i);
                Thread.sleep(100);
                if (i == 5) // MyTask wait for Other Task
                    t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}