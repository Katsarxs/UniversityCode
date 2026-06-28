import enums.TimeScheduleStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentSearchPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final JTextField professionField, doctorNameField, dateFromField, dateToField, departmentField, maxCostField;
    private final DefaultTableModel resultsModel;
    private final JTable resultsTable;
    private List<TimeSchedule> results = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();

    public AppointmentSearchPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        professionField = new JTextField(12);
        doctorNameField = new JTextField(12);
        dateFromField = new JTextField(12);
        dateToField = new JTextField(12);
        departmentField = new JTextField(12);
        maxCostField = new JTextField(12);
        resultsModel = UiUtils.nonEditableTableModel("Doctor", "Profession", "Department", "Date", "Time", "Duration", "Cost", "Status");
        resultsTable = new JTable(resultsModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(createSearchForm(), BorderLayout.NORTH);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSearchForm() {
        JPanel panel = UiUtils.titledPanel("Search appointments");
        GridBagConstraints gbc = UiUtils.gbc();
        UiUtils.addRow(panel, gbc, 0, "Profession", professionField);
        UiUtils.addRow(panel, gbc, 1, "Doctor name", doctorNameField);
        UiUtils.addRow(panel, gbc, 2, "Date from yyyy-MM-dd", dateFromField);
        UiUtils.addRow(panel, gbc, 3, "Date to yyyy-MM-dd", dateToField);
        UiUtils.addRow(panel, gbc, 4, "Department", departmentField);
        UiUtils.addRow(panel, gbc, 5, "Max cost", maxCostField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchAppointments());

        JButton clearButton = new JButton("Clear filters");
        clearButton.addActionListener(e -> clearFilters());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(searchButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton bookButton = new JButton("Book selected appointment");
        bookButton.addActionListener(e -> bookSelectedAppointment());

        JButton waitlistButton = new JButton("Join waitlist for selected");
        waitlistButton.addActionListener(e -> joinWaitlistForSelected());

        actions.add(bookButton);
        actions.add(waitlistButton);
        return actions;
    }

    public void searchAppointments() {
        try {
            refreshDoctorsCache();
            TimeScheduleSearch criteria = new TimeScheduleSearch(UiUtils.text(professionField), UiUtils.text(doctorNameField), UiUtils.parseOptionalDate(dateFromField, "Date from"), UiUtils.parseOptionalDate(dateToField, "Date to"), UiUtils.text(departmentField), UiUtils.parseOptionalPositiveDouble(maxCostField, "Max cost"));
            results = server.getAppointments(criteria);

            if (results == null) {
                results = new ArrayList<>();
            }
            resultsModel.setRowCount(0);

            for (TimeSchedule ts : results) {
                Doctor d = findDoctorById(ts.getDoctorID());

                resultsModel.addRow(new Object[]{d == null ? ts.getDoctorID() : d.getName(), d == null ? "" : d.getProfession(), d == null ? "" : d.getDepartment(), ts.getDate(), ts.getStartTime(), ts.getDuration(), ts.getCost(), ts.getStatus()});
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Search error: " + ex.getMessage());
        }
    }

    private void bookSelectedAppointment() {
        try {
            TimeSchedule selected = getSelectedSchedule();

            if (selected == null) {
                UiUtils.showError(this, "Select an appointment first");
                return;
            }

            if (selected.getStatus() == TimeScheduleStatus.BOOKED) {
                UiUtils.showError(this, "It is already booked. You can join the waitlist.");
                return;
            }

            if (selected.getStatus() == TimeScheduleStatus.CANCELLED) {
                UiUtils.showError(this, "This schedule is cancelled.");
                return;
            }

            JTextField nameField = new JTextField(18);
            JTextField cardField = new JTextField(18);

            JPanel paymentPanel = new JPanel(new GridLayout(2, 2, 6, 6));
            paymentPanel.add(new JLabel("Full name"));
            paymentPanel.add(nameField);
            paymentPanel.add(new JLabel("Credit card"));
            paymentPanel.add(cardField);

            int result = JOptionPane.showConfirmDialog(this, paymentPanel, "Payment details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String fullName = UiUtils.text(nameField);
            String card = UiUtils.text(cardField);

            if (fullName.isBlank() || card.isBlank()) {
                UiUtils.showError(this, "Full name and card number are required.");
                return;
            }

            boolean ok = server.bookAppointment(parent.getSessionID(), selected.getTimeScheduleID(), fullName, card);

            if (ok) {
                UiUtils.showInfo(this, "Appointment booked successfully");
                searchAppointments();
            } else {
                UiUtils.showError(this, "Booking failed because someone else booked it first.");
                searchAppointments();
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Booking error: " + ex.getMessage());
        }
    }

    private void joinWaitlistForSelected() {
        try {
            TimeSchedule selected = getSelectedSchedule();

            if (selected == null) {
                UiUtils.showError(this, "Select an appointment first");
                return;
            }

            if (selected.getStatus() != TimeScheduleStatus.BOOKED) {
                UiUtils.showError(this, "You can join waitlist only for booked appointments.");
                return;
            }

            boolean ok = server.waitInLine(parent.getSessionID(), selected.getTimeScheduleID());

            if (ok) {
                UiUtils.showInfo(this, "You were added to the waitlist");
            } else {
                UiUtils.showError(this, "Could not join waitlist. You may already be in it or already have this appointment.");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Waitlist error: " + ex.getMessage());
        }
    }

    private TimeSchedule getSelectedSchedule() {
        int row = resultsTable.getSelectedRow();

        if (row < 0 || row >= results.size()) {
            return null;
        }

        return results.get(row);
    }

    private void refreshDoctorsCache() {
        try {
            doctors = server.getDoctors(parent.getSessionID());

            if (doctors == null) {
                doctors = new ArrayList<>();
            }
        } catch (Exception ignored) {
            doctors = new ArrayList<>();
        }
    }

    private Doctor findDoctorById(String doctorId) {
        for (Doctor d : doctors) {
            if (d.getDoctorID().equals(doctorId)) {
                return d;
            }
        }

        return null;
    }

    private void clearFilters() {
        professionField.setText("");
        doctorNameField.setText("");
        dateFromField.setText("");
        dateToField.setText("");
        departmentField.setText("");
        maxCostField.setText("");
        searchAppointments();
    }
}