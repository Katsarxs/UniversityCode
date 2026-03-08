package lab1;

import java.io.*;

/*
 * Character Streams
 * Reading and Writing using buffer
 */

public class Example1 {
    public static void main(String[] args) {
        try (BufferedReader in = new BufferedReader(new FileReader("src/lab1/input.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Reading Error: " + e.getMessage());
        }

        try (BufferedWriter out = new BufferedWriter(new FileWriter("src/lab1/output.txt"))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Give text");
            String text = reader.readLine();
            out.write(text);
        } catch (IOException e) {
            System.err.println("Error Writing");
        }
    }
}