import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// Βοηθητικές συναρτήσεις για το gui
public final class UiUtils {
    private UiUtils() {}

    public static void addRow(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
        gbc.weightx = 0;
    }

    public static JPanel titledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    public static GridBagConstraints gbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    public static String text(JTextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    public static LocalDate parseRequiredDate(JTextField field, String fieldName) {
        String value = text(field);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in format yyyy-MM-dd");
        }
    }

    public static LocalDate parseOptionalDate(JTextField field, String fieldName) {
        String value = text(field);
        if (value.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in format yyyy-MM-dd");
        }
    }

    public static LocalTime parseRequiredTime(JTextField field, String fieldName) {
        String value = text(field);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in format HH:mm");
        }
    }

    public static int parseRequiredInt(JTextField field, String fieldName) {
        String value = text(field);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be an integer");
        }
    }

    public static double parseRequiredDouble(JTextField field, String fieldName) {
        String value = text(field);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a number");
        }
    }

    public static double parseOptionalPositiveDouble(JTextField field, String fieldName) {
        String value = text(field);
        if (value.isEmpty()) {
            return 0.0;
        }

        try {
            double number = Double.parseDouble(value);
            if (number < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a number");
        }
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static DefaultTableModel nonEditableTableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}