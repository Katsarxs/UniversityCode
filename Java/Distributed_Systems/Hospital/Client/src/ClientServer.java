import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientServer extends UnicastRemoteObject implements ClientCallback {
    private transient ClientGUI parent;

    protected ClientServer() throws RemoteException {
        super();
    }

    public ClientServer(ClientGUI parent) throws RemoteException {
        super();
        this.parent = parent;
    }

    // callback για ενημέρωση ακύρωσης
    @Override
    public void notifyTimeScheduleCancel(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parent, message, "Schedule Cancellation", JOptionPane.WARNING_MESSAGE);

            if (parent != null) {
                parent.refreshCurrentPanel();
            }
        });
    }

    // callback για ενημέρωση στο waitlist
    @Override
    public void notifyWaitlist(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parent, message, "Waitlist Notification", JOptionPane.INFORMATION_MESSAGE);

            if (parent != null) {
                parent.refreshCurrentPanel();
            }
        });
    }

    public static void main(String[] args) {
        // Συνδεόμαστε μέσω rmi με τον server
        try {
            CentralAppointmentInterface lookUp = (CentralAppointmentInterface) Naming.lookup("//localhost/Appointment");
            SwingUtilities.invokeLater(() -> new ClientGUI(lookUp));
            System.out.println("Client started and connected.");

        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            JOptionPane.showMessageDialog(null, "Could not connect to Central Appointment Server:\n" + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}