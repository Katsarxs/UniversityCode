import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private final CentralAppointmentInterface server;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private String sessionID, currentPanelName = "LOGIN";
    private User currentUser;
    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final AdminPanel adminPanel;
    private final HomePanel homePanel;

    public ClientGUI(CentralAppointmentInterface server) {
        this.server = server;

        setTitle("Medical Appointment System");
        setSize(1050, 720);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (sessionID != null) {
                    JOptionPane.showMessageDialog(ClientGUI.this, "Please logout before closing the application.");
                    return;
                }
                dispose();
                System.exit(0);
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this, server);
        registerPanel = new RegisterPanel(this, server);
        adminPanel = new AdminPanel(this, server);
        homePanel = new HomePanel(this, server);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(adminPanel, "ADMIN");
        mainPanel.add(homePanel, "HOME");

        setContentPane(mainPanel);
        showPanel("LOGIN");
        setVisible(true);
    }

    public void showPanel(String name) {
        currentPanelName = name;

        if ("ADMIN".equals(name)) {
            adminPanel.refreshAll();
        } else if ("HOME".equals(name)) {
            homePanel.refreshAll();
        }

        cardLayout.show(mainPanel, name);
    }

    public void refreshCurrentPanel() {
        if ("ADMIN".equals(currentPanelName)) {
            adminPanel.refreshAll();
        } else if ("HOME".equals(currentPanelName)) {
            homePanel.refreshAll();
        }
    }

    public void clearSession() {
        this.sessionID = null;
        this.currentUser = null;
        this.currentPanelName = "LOGIN";
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CentralAppointmentInterface getServer() {
        return server;
    }
}