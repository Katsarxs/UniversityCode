import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    public void notifyTimeScheduleCancel(String message) throws RemoteException;
    public void notifyWaitlist(String message) throws RemoteException;
}
