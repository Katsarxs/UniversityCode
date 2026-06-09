package lab5;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * CubbyHole problem with Lock.
 * We create 1 lock and 2 conditions.
 * Instead of wait() and notifyAll() we use await() and signal() on the conditions.
 * We always use try-catch-finally statements to lock and unlock.
 */

class CubbyHoleLock {
    private final int[] contents = {0, 0, 0, 0, 0};
    private boolean bufferEmpty = true;
    private boolean bufferFull = false;
    private final Lock myLock = new ReentrantLock();
    private final Condition full = myLock.newCondition();
    private final Condition empty = myLock.newCondition();
    private final int size = 5;
    private int counter = -1;

    int get() {
        int value = 0;
        myLock.lock();
        try {
            while (bufferEmpty) {
                empty.await();
            }

            value = contents[counter];
            counter--;
            System.out.println("The consumer removes the value : " + value + " by the cubbyhole");
            bufferFull = false;

            if (counter == -1) {
                bufferEmpty = true;
                System.out.println("The buffer is empty");
            }
            full.signal();

        } catch (InterruptedException _) {

        } finally {
            myLock.unlock();
            return value;
        }
    }

    void put(int value) {
        myLock.lock();
        try {
            while (bufferFull) {
                full.await();
            }

            bufferEmpty = false;
            System.out.println("The producer adds the value " + value + " in the cubbyhole");
            contents[++counter] = value;
            if (counter == size - 1) {
                bufferFull = true;
                System.out.println("The cubbyhole is full");
            }
            empty.signal();
        } catch (InterruptedException _) {
        } finally {
            myLock.unlock();
        }
    }
}