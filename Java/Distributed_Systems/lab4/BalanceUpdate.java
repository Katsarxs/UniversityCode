package lab4;

/*
 * The main thread runs monitor and updates threads.
 * Despite balance != 500, it may print 500 because of the other thread
 */

public class BalanceUpdate {
    private static int balance = 500; // balance initialization

    public static void main(String[] args) {
        startBalanceUpdateThread(); // Thread for balance update
        startBalanceMonitorThread(); // Thread for balance monitoring
    }

    public static void updateBalance() {
        balance = balance + 50;
        balance = balance - 50;
    }

    public static void monitorBalance() {
        if (balance != 500) {
            System.out.println("Balance changed: " + balance);
            System.exit(0);
        }
    }

    public static void startBalanceUpdateThread() {
        Thread t = new Thread(() -> {
            while (true) {
                updateBalance();
            }
        });
        t.start();
    }

    public static void startBalanceMonitorThread() {
        Thread t = new Thread(() -> {
            while (true) {
                monitorBalance();
            }
        });
        t.start();
    }
}