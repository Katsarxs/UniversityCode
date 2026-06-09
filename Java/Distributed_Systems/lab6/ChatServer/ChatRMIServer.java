package lab6.ChatServer;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.server.*;

/*
 * RMI Server. Implements the interface methods.
 */

public class ChatRMIServer extends UnicastRemoteObject implements ChatInterface {

    public ChatRMIServer() throws RemoteException {
        super();
    }

    @Override
    public void sendMessage(ChatMessage msg) throws RemoteException {
        try {
            BufferedWriter file = new BufferedWriter(new
                    FileWriter("ChatMessages.txt", true));
            file.write(msg.getName() + " : " + msg.getDate() + " : " +
                    msg.getMessage() + "\n");
            file.close();

        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    @Override
    public String update() throws RemoteException {
        StringBuilder str = new StringBuilder();
        String tmp;
        try {
            BufferedReader file = new BufferedReader(new
                    FileReader("ChatMessages.txt"));
            while ((tmp = file.readLine()) != null)
                str.append(tmp).append("\n");
            file.close();
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return str.toString();
    }

    public static void main(String[] args) {
        ChatRMIServer server;
        try {
            server = new ChatRMIServer();
            //1099 is the port number
            Registry r = java.rmi.registry.LocateRegistry.createRegistry(1099);
            Naming.rebind("//localhost/ChatRMI", server);
            System.out.println("Waiting new Messages");
        } catch (RemoteException | MalformedURLException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }
}
