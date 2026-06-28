import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void notifyTimeScheduleCancel(String message) throws RemoteException;

    void notifyWaitlist(String message) throws RemoteException;
}
