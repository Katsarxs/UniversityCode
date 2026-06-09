package lab1;

import java.io.*;

/* Fixing code that couldn't write objects to file
 * Key changes:
 * Add try catch
 * Implement Serializable in Character class*/

public class Exercise3 {
    public static void main(String[] args) {
        Character ch1 = new Character(); // Εντολή 1
        Character ch2 = new Character("Fictional", 20);
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("src/lab1/CharacterObjects.dat"));
            out.writeObject(ch1);
            out.writeObject(ch2);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found : + " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error : " + e.getMessage());
        }
    }
}

class Character implements Serializable {
    private String type;
    private int power;
    public Character() {

    }
    public Character(String t, int p) {
        this.type = t;
        this.power = p;
    }
}
