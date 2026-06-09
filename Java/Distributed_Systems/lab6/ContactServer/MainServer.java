package lab6.ContactServer;

import java.rmi.*;
import java.rmi.registry.Registry;

public class MainServer {
    public static void main(String[] args) {
        try {
            Registry registry = java.rmi.registry.LocateRegistry.createRegistry(1099);

            Server server = new Server();
            Naming.rebind("//localhost/ContactServer", server);
            System.out.println("Server up and running....");
        } catch (Exception e) {
            System.out.println("Server not connected: " + e);
            System.exit(1);
        }
    }
}
