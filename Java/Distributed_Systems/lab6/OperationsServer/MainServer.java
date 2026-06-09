package lab6.OperationsServer;

import java.rmi.Naming;
import java.rmi.registry.Registry;

public class MainServer {
    public static void main(String[] args) {
        try {
            Registry registry = java.rmi.registry.LocateRegistry.createRegistry(1099);

            OperationsServer server = new OperationsServer();
            Naming.rebind("//localhost/OperationsServer", server);
            System.out.println("Server up and running....");
        } catch (Exception e) {
            System.out.println("Server not connected: " + e);
            System.exit(1);
        }
    }
}
