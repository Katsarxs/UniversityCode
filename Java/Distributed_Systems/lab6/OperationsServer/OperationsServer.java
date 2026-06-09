package lab6.OperationsServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/*
 * RMI Server implementation. Takes integers and then prints the sum
 * by calling sum().
 */

public class OperationsServer extends UnicastRemoteObject implements Operations {
    private int x;
    private int y;

    public OperationsServer() throws RemoteException {
        super();
    }

    @Override
    public void setNum(int x, int y) throws RemoteException {
        this.x = x;
        this.y = y;
    }

    @Override
    public int sum() throws RemoteException {
        return x + y;
    }
}
