package lab6.DateTimeServerRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TimeOperations extends Remote {
    String update(String mode) throws RemoteException;
}
