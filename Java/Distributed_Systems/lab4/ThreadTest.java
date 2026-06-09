package lab4;

import java.io.IOException;

/*
 * Running 3 threads as daemons.
 * If the main thread ends, the other threads are too.
 */

public class ThreadTest extends Thread{
    private String threadName;
    private long delay;

    public ThreadTest(String threadName, long delay) {
        this.threadName = threadName;
        this.delay = delay;
        setDaemon(true); // If we delete this, the main thead will end, but the others will keep going
    }

    @Override
    public void run() {
        try {
            while(true) {
                System.out.println(threadName + "\n");
                sleep(delay);
            }
        } catch (InterruptedException e) {
            System.out.println(threadName + e);
        }
    }

    public static void main(String[] args) {
        Thread first = new ThreadTest("My thread 1", 100);
        Thread second = new ThreadTest("My thread 2", 200);
        Thread third = new ThreadTest("My thread 3", 300);

        System.out.println("Press ENTER when you want to finish...\n");

        first.start();
        second.start();
        third.start();

        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("Ending thread " + Thread.currentThread().getName());
    }
}
