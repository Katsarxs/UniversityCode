package lab1;

import java.io.*;

/*
 * Writing into an object using ObjectOutputStream
 */

public class Example4 {
    public static void main(String[] args) {
        ObjectOutputStream out;
        int array[] = {1, 4, 5, 8};
        try {
            out = new ObjectOutputStream(new FileOutputStream("src/lab1/object.dat"));
            out.writeObject(array);
            out.writeObject("abc");
            out.writeObject(new Phonebook("Micheal", "9475849583"));
            out.writeObject(new Phonebook("Nick", "7394057394"));
            out.flush();
            out.close();
            System.out.println("Object written to file");
        } catch (IOException e) {
            System.err.println("Error" + e.getMessage());
        }
    }
}

class Phonebook implements Serializable {
    private String name;
    private String phone;

    public Phonebook(String n, String p) {
        this.name = n;
        this.phone = p;
    }

    public String toString() {
        return "The Subscriber " + this.name + " has the number " + this.phone;
    }
}
