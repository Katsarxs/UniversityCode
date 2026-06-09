package lab6.ChatServer;

import java.rmi.*;

/*
 * Interface for remote methods that can be called
 * by the client who uses the server.
 */

public interface ChatInterface extends Remote {
    void sendMessage(ChatMessage msg) throws RemoteException;

    String update() throws RemoteException;
}