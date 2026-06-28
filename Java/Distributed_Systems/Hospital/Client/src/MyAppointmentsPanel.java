import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final DefaultTableModel model;
    private final JTable table;
    private List<Appoinment> visibleAppointments = new ArrayList<>();

    public MyAppointmentsPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = UiUtils.nonEditableTableModel("Doctor Name", "Date", "Start Time", "Duration", "Cost", "Status");

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(createHeader(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel panel = UiUtils.titledPanel("My Appointments");
        panel.setLayout(new BorderLayout());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        panel.add(refreshBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton cancelBtn = new JButton("Cancel appointment");
        cancelBtn.addActionListener(e -> cancelSelected());

        JButton reviewBtn = new JButton("Review appointment");
        reviewBtn.addActionListener(e -> reviewSelected());

        actions.add(cancelBtn);
        actions.add(reviewBtn);
        return actions;
    }

    public void refresh() {
        try {
            model.setRowCount(0);
            visibleAppointments = new ArrayList<>();
            String sessionID = parent.getSessionID();
            if (sessionID == null) {
                return;
            }

            List<Appoinment> appointments = server.getUserAppointments(sessionID);
            if (appointments == null || appointments.isEmpty()) {
                return;
            }

            List<TimeSchedule> timeSchedules = server.getAppointments(new TimeScheduleSearch());
            List<Doctor> doctors = server.getDoctors(sessionID);
            for (Appoinment appointment : appointments) {
                TimeSchedule schedule = findScheduleById(timeSchedules, appointment.getTimeScheduleID());
                if (schedule == null) {
                    continue;
                }

                Doctor doctor = findDoctorById(doctors, schedule.getDoctorID());
                String doctorName = doctor != null ? doctor.getName() : "Unknown";

                visibleAppointments.add(appointment);
                model.addRow(new Object[]{doctorName, schedule.getDate(), schedule.getStartTime(), schedule.getDuration(), schedule.getCost(), appointment.getStatus()});
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Failed to load appointments: " + ex.getMessage());
        }
    }

    private void cancelSelected() {
        try {
            int row = table.getSelectedRow();
            if (row < 0 || row >= visibleAppointments.size()) {
                UiUtils.showError(this, "Select an appointment first");
                return;
            }

            Appoinment selected = visibleAppointments.get(row);
            int answer = JOptionPane.showConfirmDialog(this, "Cancel selected appointment?", "Confirm cancellation", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean ok = server.cancelAppointment(parent.getSessionID(), selected.getAppointmentID());
            if (ok) {
                UiUtils.showInfo(this, "Appointment cancelled");
                refresh();
            } else {
                UiUtils.showError(this, """
                        Cancellation failed.
                        Possible reasons:
                        - It is the same day as the appointment
                        - Less than 24 hours remain
                        - The appointment does not belong to you""");
                refresh();
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Cancel error: " + ex.getMessage());
        }
    }

    private void reviewSelected() {
        try {
            int row = table.getSelectedRow();
            if (row < 0 || row >= visibleAppointments.size()) {
                UiUtils.showError(this, "Select an appointment first");
                return;
            }

            Appoinment selected = visibleAppointments.get(row);
            String ratingStr = JOptionPane.showInputDialog(this, "Enter rating (1-5):");
            if (ratingStr == null) {
                return;
            }

            int rating;
            try {
                rating = Integer.parseInt(ratingStr);
            } catch (NumberFormatException ex) {
                UiUtils.showError(this, "Rating must be a number.");
                return;
            }

            if (rating < 1 || rating > 5) {
                UiUtils.showError(this, "Rating must be between 1 and 5.");
                return;
            }

            String comment = JOptionPane.showInputDialog(this, "Enter review comment:");
            if (comment == null) {
                comment = "";
            }

            boolean ok = server.reviewAppointment(parent.getSessionID(), selected.getAppointmentID(), rating, comment);
            if (ok) {
                UiUtils.showInfo(this, "Review submitted successfully.");
                refresh();
            } else {
                UiUtils.showError(this, """
                        Review could not be submitted.
                        Possible reasons:
                        - Appointment not finished yet
                        - Review already exists
                        - Invalid rating""");
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Review error: " + ex.getMessage());
        }
    }

    private TimeSchedule findScheduleById(List<TimeSchedule> schedules, String scheduleId) {
        for (TimeSchedule schedule : schedules) {
            if (schedule.getTimeScheduleID().equals(scheduleId)) {
                return schedule;
            }
        }
        return null;
    }

    private Doctor findDoctorById(List<Doctor> doctors, String doctorId) {
        for (Doctor doctor : doctors) {
            if (doctor.getDoctorID().equals(doctorId)) {
                return doctor;
            }
        }
        return null;
    }
}