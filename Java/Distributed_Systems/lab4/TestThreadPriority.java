package lab4;

/*
 * Testing thread priority using setPriority().
 * MAX_PRIORITY = 10
 * MIN_PRIORITY = 1
 * The default priority is 5.
 */

public class TestThreadPriority {
    public static void main(String[] args) {
        int prior = Thread.currentThread().getPriority();
        System.out.println("Main thread started" + " with priority: " + prior);
        MyThread thread1 = new MyThread("Mythread1", 5);
        MyThread thread2 = new MyThread("Mythread2", 5);
        MyThread thread3 = new MyThread("Mythread3", 5);
        thread3.setPriority(Thread.MAX_PRIORITY); // priority = 10
        thread2.setPriority(thread1.getPriority() + 1); // priority = 6 because default for thread1 is 5, so 5 + 1 = 6
        thread1.setPriority(Thread.MIN_PRIORITY); // priority = 1
        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException _) {}
        System.out.println("Main Thread Finished");
    }
}

class MyThread extends Thread {
    private String name;
    private int times;

    MyThread(String name, int times) {
        this.name = name;
        this.times = times;
    }

    @Override
    public void run() {
        int prior = Thread.currentThread().getPriority();
        System.out.println("Thread " + name + " Started" + " with priority: " + prior);
        for (int i = 1; i <= times; i++) {
            System.out.println("\tvalue of i in Thread " + name + " : " + i);
        }
        System.out.println(name + " finished ");
    }
}