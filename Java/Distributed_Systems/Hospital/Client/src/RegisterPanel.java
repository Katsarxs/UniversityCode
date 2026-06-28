import enums.Roles;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final JTextField nameField, amkaField, phoneField, emailField, usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<Roles> roleBox;

    public RegisterPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = UiUtils.gbc();

        JLabel title = new JLabel("REGISTER");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        nameField = new JTextField(18);
        amkaField = new JTextField(18);
        phoneField = new JTextField(18);
        emailField = new JTextField(18);
        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        roleBox = new JComboBox<>(Roles.values());

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to login");

        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> parent.showPanel("LOGIN"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);
        gbc.gridwidth = 1;

        UiUtils.addRow(this, gbc, 1, "Name", nameField);
        UiUtils.addRow(this, gbc, 2, "AMKA", amkaField);
        UiUtils.addRow(this, gbc, 3, "Phone", phoneField);
        UiUtils.addRow(this, gbc, 4, "Email", emailField);
        UiUtils.addRow(this, gbc, 5, "Username", usernameField);
        UiUtils.addRow(this, gbc, 6, "Password", passwordField);
        UiUtils.addRow(this, gbc, 7, "Role", roleBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(registerButton);
        buttons.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        add(buttons, gbc);
    }

    private void register() {
        try {
            boolean ok = server.registerUser(UiUtils.text(nameField), UiUtils.text(amkaField), UiUtils.text(phoneField), UiUtils.text(emailField), UiUtils.text(usernameField), new String(passwordField.getPassword()), (Roles) roleBox.getSelectedItem());

            if (ok) {
                UiUtils.showInfo(this, "Register success. You can now login.");
                clearFields();
                parent.showPanel("LOGIN");
            } else {
                UiUtils.showError(this, "Register failed. Check empty fields or duplicate AMKA/phone/email/username.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Register error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        amkaField.setText("");
        phoneField.setText("");
        emailField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }
}
