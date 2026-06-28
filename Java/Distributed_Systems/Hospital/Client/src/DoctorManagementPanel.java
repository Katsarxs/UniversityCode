import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DoctorManagementPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final Runnable onDoctorsChanged;
    private final JTextField nameField, professionField, departmentField, phoneField, emailField, baseCostField;
    private final JLabel statusLabel;

    public DoctorManagementPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this(parent, server, null);
    }

    public DoctorManagementPanel(ClientGUI parent, CentralAppointmentInterface server, Runnable onDoctorsChanged) {
        this.parent = parent;
        this.server = server;
        this.onDoctorsChanged = onDoctorsChanged;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = UiUtils.titledPanel("Add doctor");
        GridBagConstraints gbc = UiUtils.gbc();

        nameField = new JTextField(22);
        professionField = new JTextField(22);
        departmentField = new JTextField(22);
        phoneField = new JTextField(22);
        emailField = new JTextField(22);
        baseCostField = new JTextField(22);
        statusLabel = new JLabel(" ");

        UiUtils.addRow(form, gbc, 0, "Name", nameField);
        UiUtils.addRow(form, gbc, 1, "Profession", professionField);
        UiUtils.addRow(form, gbc, 2, "Department", departmentField);
        UiUtils.addRow(form, gbc, 3, "Phone", phoneField);
        UiUtils.addRow(form, gbc, 4, "Email", emailField);
        UiUtils.addRow(form, gbc, 5, "Base cost", baseCostField);

        JButton addButton = new JButton("Add doctor");
        addButton.addActionListener(e -> addDoctor());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(addButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        form.add(statusLabel, gbc);

        add(form, BorderLayout.NORTH);
    }

    public void refreshDoctors() {
        try {
            List<Doctor> doctors = server.getDoctors(parent.getSessionID());
            statusLabel.setText("Doctors currently registered: " + doctors.size());
        } catch (Exception ex) {
            statusLabel.setText("Could not load doctors count.");
        }
    }

    private void addDoctor() {
        try {
            double baseCost = UiUtils.parseRequiredDouble(baseCostField, "Base cost");
            boolean ok = server.addDoctor(parent.getSessionID(), UiUtils.text(nameField), UiUtils.text(professionField), UiUtils.text(departmentField), UiUtils.text(phoneField), UiUtils.text(emailField), baseCost);
            if (ok) {
                UiUtils.showInfo(this, "Doctor added successfully");
                clearForm();
                refreshDoctors();

                if (onDoctorsChanged != null) {
                    onDoctorsChanged.run();
                }
            } else {
                UiUtils.showError(this, "Failed to add doctor. Check role, fields or duplicate phone/email.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Add doctor error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        professionField.setText("");
        departmentField.setText("");
        phoneField.setText("");
        emailField.setText("");
        baseCostField.setText("");
    }
}
