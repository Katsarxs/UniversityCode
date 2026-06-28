import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends JPanel {
    private final ClientGUI parent;
    private final CentralAppointmentInterface server;
    private final DoctorManagementPanel doctorManagementPanel;
    private final TimeScheduleManagementPanel timeScheduleManagementPanel;
    private JComboBox<Doctor> reviewsDoctorCombo;
    private DefaultTableModel reviewsModel;
    private JLabel reviewsSummaryLabel;

    public AdminPanel(ClientGUI parent, CentralAppointmentInterface server) {
        this.parent = parent;
        this.server = server;

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("ADMIN PANEL", SwingConstants.CENTER);
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
                ex.printStackTrace();
                UiUtils.showError(this, "Logout error: " + ex.getMessage());
            }
        });

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        timeScheduleManagementPanel = new TimeScheduleManagementPanel(parent, server);
        doctorManagementPanel = new DoctorManagementPanel(parent, server, () -> {
            timeScheduleManagementPanel.refreshDoctors();
            timeScheduleManagementPanel.refreshSchedules();
            refreshReviewsDoctors();
        });

        tabs.addTab("Doctors", doctorManagementPanel);
        tabs.addTab("Time schedules", timeScheduleManagementPanel);
        tabs.addTab("Doctor reviews", createReviewsPanel());
        tabs.addTab("Account", new AccountPanel(parent, server));

        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();

            if (selected == timeScheduleManagementPanel) {
                timeScheduleManagementPanel.refreshAll();
            } else if (selected == doctorManagementPanel) {
                doctorManagementPanel.refreshDoctors();
            } else {
                refreshReviewsDoctors();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshAll() {
        doctorManagementPanel.refreshDoctors();
        timeScheduleManagementPanel.refreshAll();
        refreshReviewsDoctors();
    }

    private JPanel createReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = UiUtils.titledPanel("Select doctor");
        GridBagConstraints gbc = UiUtils.gbc();

        reviewsDoctorCombo = new JComboBox<>();
        reviewsDoctorCombo.setRenderer(new DoctorListCellRenderer());

        JButton loadButton = new JButton("Load reviews");
        loadButton.addActionListener(e -> loadSelectedDoctorReviews());

        UiUtils.addRow(top, gbc, 0, "Doctor", reviewsDoctorCombo);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        top.add(loadButton, gbc);

        reviewsModel = UiUtils.nonEditableTableModel("Doctor", "Patient", "Rating", "Comment");

        JTable reviewsTable = new JTable(reviewsModel);
        reviewsSummaryLabel = new JLabel("No reviews loaded.");

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reviewsTable), BorderLayout.CENTER);
        panel.add(reviewsSummaryLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshReviewsDoctors() {
        if (reviewsDoctorCombo == null) {
            return;
        }

        try {
            Doctor selected = (Doctor) reviewsDoctorCombo.getSelectedItem();
            String selectedId = selected == null ? null : selected.getDoctorID();

            reviewsDoctorCombo.removeAllItems();

            List<Doctor> doctors = server.getDoctors(parent.getSessionID());

            for (Doctor doctor : doctors) {
                reviewsDoctorCombo.addItem(doctor);

                if (selectedId != null && selectedId.equals(doctor.getDoctorID())) {
                    reviewsDoctorCombo.setSelectedItem(doctor);
                }
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Could not load doctors for reviews: " + ex.getMessage());
        }
    }

    private void loadSelectedDoctorReviews() {
        try {
            Doctor doctor = (Doctor) reviewsDoctorCombo.getSelectedItem();

            if (doctor == null) {
                UiUtils.showError(this, "Select a doctor first.");
                return;
            }

            List<Review> reviews = server.accessDoctorReviews(
                    parent.getSessionID(),
                    doctor.getDoctorID()
            );

            if (reviews == null) {
                reviews = new ArrayList<>();
            }

            reviewsModel.setRowCount(0);

            int sum = 0;

            for (Review review : reviews) {
                sum += review.getReviewRating();

                String patientName = server.getUserNameById(parent.getSessionID(), review.getUserID());

                reviewsModel.addRow(new Object[]{doctor.getName(), patientName, review.getReviewRating(), review.getReviewComment()});
            }

            if (reviews.isEmpty()) {
                reviewsSummaryLabel.setText("No reviews for " + doctor.getName());
            } else {
                double average = sum / (double) reviews.size();
                reviewsSummaryLabel.setText("Doctor: " + doctor.getName() + " | Reviews: " + reviews.size() + " | Average rating: " + String.format("%.2f", average));
            }
        } catch (Exception ex) {
            UiUtils.showError(this, "Could not load reviews: " + ex.getMessage());
        }
    }

    private static class DoctorListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Doctor doctor) {
                setText(doctor.getName() + " - " + doctor.getProfession());
            }
            return this;
        }
    }
}