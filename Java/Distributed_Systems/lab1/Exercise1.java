package lab1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/* Reading a file and output based on options
 * Function map() used to transform the object
 * Function filter() to filter based or parameters
 * Function sorted() to sort data
 * Function toList() to add data to list
 */

public class Exercise1 {
    public static void main(String[] args) {
        try (Stream<String> lines = Files.lines(Paths.get("src/lab1/Letter.txt"))) {
            List<String> result = lines
                    .filter(line -> line.startsWith("Me gusta"))
                    .filter(line -> line.endsWith("!"))
                    .map(String::toUpperCase)
                    .sorted()
                    .map(line -> line.substring(0, 16))
                    .toList();
            result.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error : " + e.getMessage());
        }
    }
}
