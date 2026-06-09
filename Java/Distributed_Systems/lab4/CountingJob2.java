package lab4;

/*
 * Class extending Thread.
 */

public class CountingJob2 extends Thread {
    private int counter;

    public CountingJob2(int c) {
        this.counter = c;
    }

    @Override
    public void run() {
        while(true) {
            System.out.println(counter++);
        }
    }
}

class Test2 {
    public static void main(String[] args) {
        Thread t = new CountingJob2(1);
        t.start();
    }
}