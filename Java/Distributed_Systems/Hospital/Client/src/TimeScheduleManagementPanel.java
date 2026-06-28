import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimeScheduleManagementPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final JComboBox<Doctor> doctorCombo;
    private final JTextField addDateField, addTimeField, addDurationField, addCostField, updateDateField, updateTimeField, updateDurationField, updateCostField;
    private final JComboBox<TimeSchedule> scheduleCombo;
    private final JLabel statusLabel;

    public TimeScheduleManagementPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        doctorCombo = new JComboBox<>();
        doctorCombo.setRenderer(new DoctorListCellRenderer());
        addDateField = new JTextField(12);
        addTimeField = new JTextField(12);
        addDurationField = new JTextField(12);
        addCostField = new JTextField(12);
        scheduleCombo = new JComboBox<>();
        scheduleCombo.setRenderer(new TimeScheduleListCellRenderer());
        scheduleCombo.addActionListener(e -> fillUpdateFieldsFromSelection());
        updateDateField = new JTextField(12);
        updateTimeField = new JTextField(12);
        updateDurationField = new JTextField(12);
        updateCostField = new JTextField(12);
        statusLabel = new JLabel(" ");

        add(createMainPanel(), BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 12, 0));
        wrapper.add(createAddPanel());
        wrapper.add(createUpdateDeletePanel());
        return wrapper;
    }

    private JPanel createAddPanel() {
        JPanel panel = UiUtils.titledPanel("Add time schedule");
        GridBagConstraints gbc = UiUtils.gbc();

        UiUtils.addRow(panel, gbc, 0, "Doctor", doctorCombo);
        UiUtils.addRow(panel, gbc, 1, "Date yyyy-mm-dd", addDateField);
        UiUtils.addRow(panel, gbc, 2, "Time hh:mm", addTimeField);
        UiUtils.addRow(panel, gbc, 3, "Duration", addDurationField);
        UiUtils.addRow(panel, gbc, 4, "Cost", addCostField);

        JButton addButton = new JButton("Add schedule");
        addButton.addActionListener(e -> addSchedule());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(addButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);
        return panel;
    }

    private JPanel createUpdateDeletePanel() {
        JPanel panel = UiUtils.titledPanel("Update or delete schedule");
        GridBagConstraints gbc = UiUtils.gbc();

        UiUtils.addRow(panel, gbc, 0, "Schedule", scheduleCombo);
        UiUtils.addRow(panel, gbc, 1, "New date yyyy-mm-dd", updateDateField);
        UiUtils.addRow(panel, gbc, 2, "New time hh:mm", updateTimeField);
        UiUtils.addRow(panel, gbc, 3, "New duration", updateDurationField);
        UiUtils.addRow(panel, gbc, 4, "New cost", updateCostField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateSelectedSchedule());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedSchedule());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(updateButton);
        buttons.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);
        return panel;
    }

    public void refreshAll() {
        refreshDoctors();
        refreshSchedules();
    }

    public void refreshDoctors() {
        try {
            Doctor previouslySelected = (Doctor) doctorCombo.getSelectedItem();
            String previousId = previouslySelected == null ? null : previouslySelected.getDoctorID();

            doctorCombo.removeAllItems();
            for (Doctor d : server.getDoctors(parent.getSessionID())) {
                doctorCombo.addItem(d);
                if (previousId != null && previousId.equals(d.getDoctorID())) {
                    doctorCombo.setSelectedItem(d);
                }
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Could not load doctors: " + ex.getMessage());
        }
    }

    public void refreshSchedules() {
        try {
            TimeSchedule previouslySelected = (TimeSchedule) scheduleCombo.getSelectedItem();
            String previousId = previouslySelected == null ? null : previouslySelected.getTimeScheduleID();
            scheduleCombo.removeAllItems();
            for (TimeSchedule ts : server.getAppointments(new TimeScheduleSearch())) {
                scheduleCombo.addItem(ts);

                if (previousId != null && previousId.equals(ts.getTimeScheduleID())) {
                    scheduleCombo.setSelectedItem(ts);
                }
            }

            statusLabel.setText("Schedules loaded: " + scheduleCombo.getItemCount());
            if (scheduleCombo.getItemCount() == 0) {
                clearUpdateFields();
            } else {
                fillUpdateFieldsFromSelection();
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Could not load schedules: " + ex.getMessage());
        }
    }

    private void addSchedule() {
        try {
            Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
            if (doctor == null) {
                UiUtils.showError(this, "Select a doctor first");
                return;
            }

            LocalDate date = UiUtils.parseRequiredDate(addDateField, "Date");
            LocalTime time = UiUtils.parseRequiredTime(addTimeField, "Time");
            int duration = UiUtils.parseRequiredInt(addDurationField, "Duration");
            double cost = UiUtils.parseRequiredDouble(addCostField, "Cost");
            if (duration <= 0 || cost < 0) {
                UiUtils.showError(this, "Duration must be positive and cost cannot be negative.");
                return;
            }

            boolean ok = server.addTimeSchedule(parent.getSessionID(), doctor.getDoctorID(), date, time, duration, cost);
            if (ok) {
                UiUtils.showInfo(this, "Schedule added successfully");
                clearAddFields();
                refreshSchedules();
            } else {
                UiUtils.showError(this, "Failed to add schedule. Check admin role and fields.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Add schedule error: " + ex.getMessage());
        }
    }

    private void updateSelectedSchedule() {
        try {
            TimeSchedule ts = getSelectedSchedule();
            if (ts == null) {
                UiUtils.showError(this, "Select a schedule first");
                return;
            }

            ts.setDate(UiUtils.parseRequiredDate(updateDateField, "New date"));
            ts.setStartTime(UiUtils.parseRequiredTime(updateTimeField, "New time"));
            ts.setDuration(UiUtils.parseRequiredInt(updateDurationField, "New duration"));
            ts.setCost(UiUtils.parseRequiredDouble(updateCostField, "New cost"));
            if (ts.getDuration() <= 0 || ts.getCost() < 0) {
                UiUtils.showError(this, "Duration must be positive and cost cannot be negative.");
                return;
            }

            boolean ok = server.updateTimeSchedule(parent.getSessionID(), ts);
            if (ok) {
                UiUtils.showInfo(this, "Schedule updated successfully");
                refreshSchedules();
            } else {
                UiUtils.showError(this, "Update failed. Booked schedules cannot be updated or schedule was not found.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Update schedule error: " + ex.getMessage());
        }
    }

    private void deleteSelectedSchedule() {
        try {
            TimeSchedule ts = getSelectedSchedule();
            if (ts == null) {
                UiUtils.showError(this, "Select a schedule first");
                return;
            }

            int answer = JOptionPane.showConfirmDialog(this, "Delete schedule?\nIf there are booked appointments, affected online patients will be notified.", "Confirm delete", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean ok = server.deleteTimeSchedule(parent.getSessionID(), ts.getTimeScheduleID());
            if (ok) {
                UiUtils.showInfo(this, "Schedule deleted successfully");
                clearUpdateFields();
                refreshSchedules();
            } else {
                UiUtils.showError(this, "Delete failed. Check admin role or selected schedule.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Delete schedule error: " + ex.getMessage());
        }
    }

    private TimeSchedule getSelectedSchedule() {
        return (TimeSchedule) scheduleCombo.getSelectedItem();
    }

    private void fillUpdateFieldsFromSelection() {
        TimeSchedule ts = getSelectedSchedule();
        if (ts == null) {
            clearUpdateFields();
            return;
        }

        updateDateField.setText(String.valueOf(ts.getDate()));
        updateTimeField.setText(String.valueOf(ts.getStartTime()));
        updateDurationField.setText(String.valueOf(ts.getDuration()));
        updateCostField.setText(String.valueOf(ts.getCost()));
    }

    private Doctor findDoctorById(String doctorId) {
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            Doctor d = doctorCombo.getItemAt(i);
            if (d.getDoctorID().equals(doctorId)) {
                return d;
            }
        }
        return null;
    }

    private void clearAddFields() {
        addDateField.setText("");
        addTimeField.setText("");
        addDurationField.setText("");
        addCostField.setText("");
    }

    private void clearUpdateFields() {
        updateDateField.setText("");
        updateTimeField.setText("");
        updateDurationField.setText("");
        updateCostField.setText("");
    }

    private static class DoctorListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Doctor d) {
                setText(d.toString());
            }
            return this;
        }
    }

    private class TimeScheduleListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof TimeSchedule ts) {
                Doctor d = findDoctorById(ts.getDoctorID());
                String doctorText = d == null ? ts.getDoctorID() : d.getName() + " - " + d.getProfession();

                setText(doctorText + " | " + ts.getDate() + " " + ts.getStartTime() + " | " + ts.getCost() + "€ | " + ts.getStatus());
            }
            return this;
        }
    }
}