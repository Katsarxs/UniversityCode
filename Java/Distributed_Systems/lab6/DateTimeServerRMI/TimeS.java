package lab6.DateTimeServerRMI;

import java.rmi.*;
import java.rmi.registry.Registry;

public class TimeS {
    public static void main(String[] args) {
        try {
            TimeImplementation TimeServer = new TimeImplementation();
            Registry r = java.rmi.registry.LocateRegistry.createRegistry(1099);
            Naming.rebind("//localhost/TimeServer", TimeServer);
            System.out.println("Server up and running....");
        } catch (Exception e) {
            System.out.println("Server not connected: " + e);
        }
    }
}