package lab6.ContactServer;

import java.io.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

public class Server extends UnicastRemoteObject implements DirectoryOperations {
    private ArrayList<Contact> directory;

    public Server() throws RemoteException {
        super();
        String newline;
        String[] str;
        try {
            directory = new ArrayList<>();
            BufferedReader instream = new BufferedReader(new FileReader("phonedirectory.txt"));

            while ((newline = instream.readLine()) != null) {
                str = newline.split(":");
                directory.add(new Contact(str[0], str[1], str[2]));
            }
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    @Override
    public Contact searchNumber(String name) throws RemoteException {
        Contact contact = null;
        for (Contact value : directory) {
            if (value.getName().equals(name)) {
                contact = value;
            }
        }
        return contact;
    }

    @Override
    public boolean insertContact(String name, String address, String number) throws RemoteException {
        Contact contact = new Contact(name, address, number);
        boolean success = false;

        for (Contact value : directory) {
            if (value.getName().equals(contact.getName())) {
                return success;
            }
        }
        directory.add(contact);

        try {
            BufferedWriter outstream = new BufferedWriter(new FileWriter("phonedirectory.txt", true));
            outstream.write(name + ":" + address + ":" + number);
            outstream.newLine();
            outstream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Inserting " + name);
        success = true;

        return success;
    }
}
