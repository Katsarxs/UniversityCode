package lab4;

import java.util.ArrayList;
import java.util.List;

/*
 * Finding prime numbers from range 1-100 using 4 threads.
 * The class FindPrimes extends Thread.
 * We use join to print the correct prime numbers.
 */

public class Find {
    public static void main(String[] args) throws InterruptedException {
        int rangeStart = 1;
        int rangeEnd = 100;
        int threadCount = 4;
        // Calculate range per thread
        int rangePerThread = (rangeEnd - rangeStart + 1) / threadCount;

        Thread[] threads = new Thread[threadCount];
        FindPrimes[] primeFinders = new FindPrimes[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int start = rangeStart + i * rangePerThread;
            int end = (i == threadCount - 1) ? rangeEnd : start + rangePerThread - 1;

            // Create threads and start
            primeFinders[i] = new FindPrimes(start, end);
            threads[i] = primeFinders[i];
            threads[i].start();

        }
        // join() for all threads
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        List<Integer> allPrimes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            allPrimes.addAll(primeFinders[i].getPrimes());
        }

        // Combine results
        System.out.println("All prime numbers in range " + rangeStart + "-" + rangeEnd + ": " + allPrimes);
    }
}

class FindPrimes extends Thread {
    private final int start;
    private final int end;
    private final List<Integer> primes;

    public FindPrimes(int start, int end) {
        this.start = start;
        this.end = end;
        this.primes = new ArrayList<>();
    }

    @Override
    public void run() {
        for (int num = start; num <= end; num++) {
            if (isPrime(num)) {
                primes.add(num);
            }
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " found primes: " + primes);
    }

    private boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> getPrimes() {
        return primes;
    }
}