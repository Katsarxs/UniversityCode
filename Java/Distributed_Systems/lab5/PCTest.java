package lab5;

/*
 * CubbyHole problem.
 * One producer (thread), one consumer (thread) and one cubbyhole.
 * Using synchronized for methods get() and put(), we make sure that
 * the producer doesn't put more than 1 thing in the cubbyhole and
 * the consumer takes exactly 1 if the cubbyhole has 1, ofcourse.
 */

public class PCTest {
    public static void main(String[] args) {
        CubbyHole c = new CubbyHole();
        Producer p1 = new Producer(c, 1);
        Consumer c1 = new Consumer(c, 1);
        p1.start();
        c1.start();
    }
}

class CubbyHole {
    private int contents;
    private boolean available = false;

    public synchronized int get() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException _) {
            }
        }
        available = false;
        System.out.println("in get");
        notifyAll();
        return contents;
    }

    public synchronized void put(int value) {
        while (available) {
            try {
                wait();
            } catch (InterruptedException _) {
            }
        }
        contents = value;
        available = true;
        System.out.println("in put");
        notifyAll();
    }
}

class Producer extends Thread {
    private final CubbyHole cubbyhole;
    private final int id;

    public Producer(CubbyHole cubbyhole, int id) {
        this.cubbyhole = cubbyhole;
        this.id = id;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            cubbyhole.put(i);
            System.out.println("Producer #" + this.id + " put: " + i);
            //sleep((int) (Math.random() * 1000));
        }
    }
}

class Consumer extends Thread {
    private final CubbyHole cubbyhole;
    private final int id;

    public Consumer(CubbyHole cubbyhole, int id) {
        this.cubbyhole = cubbyhole;
        this.id = id;
    }

    @Override
    public void run() {
        int value;
        for (int i = 1; i <= 10; i++) {
            value = cubbyhole.get();
            System.out.println("Consumer #" + this.id + " got: " + value);
        }
    }
}