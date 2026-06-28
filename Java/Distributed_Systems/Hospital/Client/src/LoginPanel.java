import enums.Roles;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = UiUtils.gbc();

        JLabel title = new JLabel("LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> parent.showPanel("REGISTER"));
        exitButton.addActionListener(e -> {
            if (parent.getSessionID() != null) {
                UiUtils.showError(this, "Please logout before exiting.");
                return;
            }

            System.exit(0);
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);
        gbc.gridwidth = 1;
        UiUtils.addRow(this, gbc, 1, "Username", usernameField);
        UiUtils.addRow(this, gbc, 2, "Password", passwordField);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(loginButton);
        buttons.add(registerButton);
        buttons.add(exitButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttons, gbc);
    }

    private void login() {
        try {
            String username = UiUtils.text(usernameField);
            String password = new String(passwordField.getPassword());

            if (username.isBlank() || password.isBlank()) {
                UiUtils.showError(this, "Username and password are required.");
                return;
            }

            ClientServer callback = new ClientServer(parent);

            String sessionID = server.login(username, password, callback);

            if (sessionID == null) {
                UiUtils.showError(this, "Wrong credentials");
                return;
            }

            parent.setSessionID(sessionID);

            User user = server.getUser(sessionID);
            parent.setCurrentUser(user);

            usernameField.setText("");
            passwordField.setText("");

            UiUtils.showInfo(this, "Login success");

            if (user != null && user.getRole() == Roles.ADMIN) {
                parent.showPanel("ADMIN");
            } else {
                parent.showPanel("HOME");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            UiUtils.showError(this, "Login error: " + ex.getMessage());
        }
    }
}