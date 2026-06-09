package lab6.ChatServer;

import java.net.MalformedURLException;
import java.rmi.*;

/*
 * RMI Client
 */

public class ChatRMIClient {
    public static void main(String[] args) {
        //RMISecurityManager security = new RMISecurityManager();
        //System.setSecurityManager(security);
        try {
            String name = "//localhost/ChatRMI";
            ChatInterface look_op = (ChatInterface) Naming.lookup(name);
            look_op.sendMessage(new ChatMessage("Nikos", "Hello World!!!!"));
            System.out.println(look_op.update());
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }
}