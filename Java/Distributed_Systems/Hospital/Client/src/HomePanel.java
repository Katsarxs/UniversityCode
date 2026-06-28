import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private final AppointmentSearchPanel appointmentSearchPanel;
    private final MyAppointmentsPanel myAppointmentsPanel;

    public HomePanel(ClientGUI parent, CentralAppointmentInterface server) {
        JPanel topPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());

        JLabel title = new JLabel("PATIENT PANEL", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            try {
                if (parent.getSessionID() != null) {
                    server.logout(parent.getSessionID());
                }

                parent.clearSession();
                UiUtils.showInfo(this, "Logout success");
                parent.showPanel("LOGIN");

            } catch (Exception ex) {
                UiUtils.showError(this, "Logout error: " + ex.getMessage());
            }
        });

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        myAppointmentsPanel = new MyAppointmentsPanel(parent, server);
        appointmentSearchPanel = new AppointmentSearchPanel(parent, server);

        tabs.addTab("My Appointments", myAppointmentsPanel);
        tabs.addTab("Search / Book", appointmentSearchPanel);
        tabs.addTab("Account", new AccountPanel(parent, server));

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshAll() {
        appointmentSearchPanel.searchAppointments();
        myAppointmentsPanel.refresh();
    }
}
