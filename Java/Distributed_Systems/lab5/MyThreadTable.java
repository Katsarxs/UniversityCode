package lab5;

/*
 * Each thread prints a table of integers.
 * If the block of printTable wasn't synchronized, the results would be different.
 */

public class MyThreadTable {
    public static void main(String[] args) {
        Table table = new Table();
        Thread t1 = new MyThread("Thread 1", table, 10);
        Thread t2 = new MyThread("Thread 2", table, 100);
        t1.start();
        t2.start();
    }

}

class Table {
    void printTable(int n) {
        synchronized (this) {
            for (int i = 1; i <= 10; i++) {
                System.out.println(n * i);
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class MyThread extends Thread {
    private Table table;
    private int initial;

    MyThread(String name, Table table, int initial) {
        super(name);
        this.table = table;
        this.initial = initial;
    }

    @Override
    public void run() {
        System.out.println("Thread : " + getName());
        table.printTable(initial);
    }
}