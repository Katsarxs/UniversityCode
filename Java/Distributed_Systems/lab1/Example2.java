package lab1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/*
 * Reading and Writing file using readAllLines function
 */

public class Example2 {
    public static void main(String[] args) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/lab1/Letter.txt"));
            lines.add("New line addition");
            Files.write(Paths.get("src/lab1/NewLetter.txt"), lines);
        } catch (IOException e) {
            System.err.println("Error : " + e.getMessage());
        }
    }
}
