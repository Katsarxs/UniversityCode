package lab1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Reading and writing context of a file to another file
 * Using FileInputStream and FileOutputStream
 */

public class Example3 {
    public static void main(String[] args) {
        try (FileInputStream fin = new FileInputStream("src/lab1/source.dat");
            FileOutputStream fout = new FileOutputStream("src/lab1/dest.dat")) {
                int i;
                while ((i = fin.read()) != -1) {
                    fout.write(i);
                }
            } catch (IOException e) {
            System.err.println("Error : " + e.getMessage());
        }
    }
}