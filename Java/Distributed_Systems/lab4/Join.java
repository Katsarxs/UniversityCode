package lab4;

import java.util.Arrays;

/*
 * Printing min, med, max of a random array.
 */

public class Join {
    public static void main(String[] args) {
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        Thread t = new SortThread(array);
        t.start();
        try {
            t.join(); // If we delete join(), the results are wrong
            System.out.println("Minimum: " + array[0]);
            System.out.println("Median: " + array[array.length/2]);
            System.out.println("Maximum: " + array[array.length-1]);
        }
        catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }
}

class SortThread extends Thread {
    private double[] array;

    public SortThread(double[] array) {
        this.array = array;
    }

    @Override
    public void run() {
        Arrays.sort(array);
    }
}