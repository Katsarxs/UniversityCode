package lab6.OperationsServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Operations extends Remote {
    void setNum(int x, int y) throws RemoteException;

    int sum() throws RemoteException;
}
