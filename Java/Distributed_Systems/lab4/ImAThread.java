package lab4;

/*
 * Creating and running threads depending on the user's input.
 * Runnable and Thread.
 */

public class ImAThread {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java N\nN = integer");
            System.exit(1);
        }
        int N = Integer.parseInt(args[0]);

        Thread[] tRunnable = new Thread[N];
        Thread[] tThread = new Thread[N];

        for (int i = 0; i < N; i++) {
            tRunnable[i] = new Thread(new ImAThreadRunnable());
            tThread[i] = new ImAThreadThread();
        }

        System.out.println("Printing threads with Runnable way");
        for (int i = 0; i < N; i++) {
            tRunnable[i].start();
            try {
                tRunnable[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Printing threads with Thread way");
        for (int i = 0; i < N; i++) {
            tThread[i].start();
            try {
                tThread[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

class ImAThreadRunnable implements Runnable {
    private static int count;

    public ImAThreadRunnable() {
    }

    @Override
    public void run() {
        System.out.println("Hello, I am the thread: Thread #" + ++count);
    }
}

class ImAThreadThread extends Thread {
    private static int count;

    public ImAThreadThread() {
    }

    @Override
    public void run() {
        System.out.println("Hello, I am the thread: Thread #" + ++count);
    }
}