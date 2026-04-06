/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
import client.ClientConnection;
import client.ClientController;
import client.ui.ClientFrame;

import javax.swing.*;

public class Client {

    public static void main(String[] args) {
        // Εκκίνηση Client
        SwingUtilities.invokeLater(() -> {
            ClientConnection connection = new ClientConnection("localhost", 5500);
            ClientController controller = new ClientController(connection);
            ClientFrame frame = new ClientFrame(controller);
            controller.setView(frame);
            frame.open();
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    connection.close();
                }
            });
        });
    }
}