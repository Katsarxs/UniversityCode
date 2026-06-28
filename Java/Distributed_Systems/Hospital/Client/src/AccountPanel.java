import javax.swing.*;
import java.awt.*;

public class AccountPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final JTextField deleteUsernameField;
    private final JPasswordField deletePasswordField;

    public AccountPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Account Delete", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = UiUtils.gbc();

        deleteUsernameField = new JTextField(18);
        deletePasswordField = new JPasswordField(18);

        UiUtils.addRow(center, gbc, 1, "Username", deleteUsernameField);
        UiUtils.addRow(center, gbc, 2, "Password", deletePasswordField);

        JButton deleteButton = new JButton("Delete user");
        deleteButton.addActionListener(e -> deleteUser());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        center.add(buttons, gbc);

        add(center, BorderLayout.CENTER);
    }

    private void deleteUser() {
        try {
            String username = UiUtils.text(deleteUsernameField);
            String password = new String(deletePasswordField.getPassword());

            if (username.isBlank() || password.isBlank()) {
                UiUtils.showError(this, "Username and password are required");
                return;
            }

            int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user '" + username + "'?", "Confirm delete", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean ok = server.deleteUser(username, password);
            if (ok) {
                UiUtils.showInfo(this, "User deleted successfully");
                deleteUsernameField.setText("");
                deletePasswordField.setText("");

                if (parent.getCurrentUser() != null && username.equals(parent.getCurrentUser().getUsername())) {
                    server.logout(parent.getSessionID());
                    parent.clearSession();
                    parent.showPanel("LOGIN");
                }
            } else {
                UiUtils.showError(this, "Delete failed. Check username/password.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Delete user error: " + ex.getMessage());
        }
    }
}
