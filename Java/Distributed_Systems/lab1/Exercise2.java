package lab1;

import java.io.*;

/*
 * Reading printing data from file generated from Example4.java
 * Using ObjectInputStream*/

public class Exercise2 {
    public static void main(String[] args) {
        ObjectInputStream in = null;
        Phonebook ph;
        int[] array = null;
        String string = null;
        try {
            in = new ObjectInputStream(new FileInputStream("src/lab1/object.dat"));
            array = (int []) in.readObject();
            string = (String) in.readObject();
            while ((ph = (Phonebook) in.readObject()) != null) {
                System.out.println(ph);
            }
        } catch (EOFException e) {
            System.out.println("End of file reached");
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.err.println("Error : " + e.getMessage());
            }
        }

        System.out.println(string);
        for (int n : array) {
            System.out.print(n + " ");
        }

    }
}
