package lab5;

/*
 * CubbyHole problem with 5 contents.
 * Now we have 2 booleans, bufferEmpty and bufferFull.
 * And we check always in get() if contents are empty,
 * and in put() if contents are full.
 */

public class EspressoBar {
    public static void main(String[] args) {
        CubbyHole1 c = new CubbyHole1();
        Producerr p1 = new Producerr(c, 1);
        Consumerr c1 = new Consumerr(c, 1);
        p1.start();
        c1.start();
    }
}

class CubbyHole1 {
    private final int[] contents = {0, 0, 0, 0, 0};
    private boolean bufferEmpty = true;
    private boolean bufferFull = false;

    private final int size = 5;
    private int counter = -1;

    synchronized int get() {
        while (bufferEmpty) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        int value = contents[counter];
        counter--;
        System.out.println("The consumer removes the value : " + value + " by the cubbyhole");
        bufferFull = false;
        if (counter == -1) {
            bufferEmpty = true;
            System.out.println("Buffer is empty");
        }
        notifyAll();
        return value;
    }

    synchronized void put(int value) {
        while (bufferFull) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        bufferEmpty = false;
        System.out.println("The producer adds the value " + value + " in the cubbyhole");
        contents[++counter] = value;
        if (counter == size - 1) {
            bufferFull = true;
            System.out.println("Buffer is full");
        }
        notifyAll();
    }
}

class Consumerr extends Thread {
    private final CubbyHole1 cubbyhole;
    private final int id;

    public Consumerr(CubbyHole1 c, int id) {
        cubbyhole = c;
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

class Producerr extends Thread {
    private final CubbyHole1 cubbyhole;
    private final int id;

    public Producerr(CubbyHole1 c, int id) {
        cubbyhole = c;
        this.id = id;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            cubbyhole.put(i);
            System.out.println("Producer #" + this.id + " put: " + i);
            try {
                sleep((int) (Math.random() * 1000));
            } catch (InterruptedException _) {
            }
        }
    }
}