package lab6.ContactServer;

import java.rmi.*;

public interface DirectoryOperations extends Remote {
    Contact searchNumber(String name) throws RemoteException;

    boolean insertContact(String name, String address, String number) throws RemoteException;
}