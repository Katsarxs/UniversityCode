package lab6.ContactServer;

import java.io.Serializable;

public class Contact implements Serializable {
    private final String name;
    private final String address;
    private final String number;

    public Contact(String name, String address, String number) {
        this.name = name;
        this.address = address;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}