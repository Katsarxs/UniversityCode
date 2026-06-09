package lab4;

/*
 * Class implementing Runnable.
 */

public class CountingJob implements Runnable{
    private int counter;

    public CountingJob(int c) {
        this.counter = c;
    }

    @Override
    public void run() {
        while(true) {
            System.out.println(counter++);
        }
    }
}

class Test1 {
    public static void main(String[] args) {
        CountingJob countingJob = new CountingJob(1);
        Thread t = new Thread(countingJob);
        t.start();
    }
}