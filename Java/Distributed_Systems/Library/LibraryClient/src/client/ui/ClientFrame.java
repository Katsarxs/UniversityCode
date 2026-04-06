/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package client.ui;

import client.ClientController;
import model.Episode;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame implements ClientView {

    // Αντικείμενο controller
    private final ClientController controller;
    // Αντικείμενο για παράθυρα
    private final ClientDialogs dialogs;
    // Περιοχή εξόδου κειμένου
    private JTextArea outputArea;

    // Contractor παραθύρου
    public ClientFrame(ClientController controller) {
        super("Digital Library Client");
        this.controller = controller;
        this.dialogs = new ClientDialogs();
        createGui();
    }

    public void open() {
        setVisible(true);
        appendText("Client started.");
    }

    // Δημιουργία gui
    private void createGui() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addMovieBtn = new JButton("Add Movie");
        JButton addSeriesBtn = new JButton("Add Series");
        JButton addEpisodeBtn = new JButton("Add Episode");
        JButton removeEpisodeBtn = new JButton("Remove Episode");
        JButton updateBtn = new JButton("Update Media");
        JButton searchTitleBtn = new JButton("Search Title");
        JButton searchCategoryBtn = new JButton("Search Category");
        JButton deleteBtn = new JButton("Delete Media");
        JButton saveBtn = new JButton("Save Library");
        JButton loadBtn = new JButton("Load Library");
        JButton clearBtn = new JButton("Clear Log");

        addMovieBtn.addActionListener(e -> dialogs.addMovieDialog(this, controller));
        addSeriesBtn.addActionListener(e -> dialogs.addSeriesDialog(this, controller));
        addEpisodeBtn.addActionListener(e -> addEpisode());
        removeEpisodeBtn.addActionListener(e -> dialogs.removeEpisodeDialog(this, controller));
        updateBtn.addActionListener(e -> updateMedia());
        searchTitleBtn.addActionListener(e -> searchByTitle());
        searchCategoryBtn.addActionListener(e -> searchByCategory());
        deleteBtn.addActionListener(e -> deleteMedia());
        saveBtn.addActionListener(e -> saveLibrary());
        loadBtn.addActionListener(e -> loadLibrary());
        clearBtn.addActionListener(e -> outputArea.setText(""));

        buttonPanel.add(addMovieBtn);
        buttonPanel.add(addSeriesBtn);
        buttonPanel.add(addEpisodeBtn);
        buttonPanel.add(removeEpisodeBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(searchTitleBtn);
        buttonPanel.add(searchCategoryBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(clearBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Προσθήκη επεισοδίου
    private void addEpisode() {
        JTextField seriesField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField seasonField = new JTextField();
        JTextField numberField = new JTextField();
        JTextField durationField = new JTextField();

        Object[] fields = {
                "Series Title:", seriesField,
                "Episode Title:", titleField,
                "Season Number:", seasonField,
                "Episode Number:", numberField,
                "Duration (min):", durationField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Episode", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Episode episode = new Episode(
                    titleField.getText().trim(),
                    Integer.parseInt(seasonField.getText().trim()),
                    Integer.parseInt(numberField.getText().trim()),
                    Integer.parseInt(durationField.getText().trim())
            );
            controller.addEpisode(seriesField.getText().trim(), episode);
        } catch (NumberFormatException e) {
            showError("Season, Episode Number, and Duration must be valid numbers.");
        }
    }

    // Ενημέρωση media
    private void updateMedia() {
        String title = JOptionPane.showInputDialog(this, "Enter the exact title of the media to update:");
        if (title == null || title.trim().isEmpty()) return;

        Object selected = JOptionPane.showInputDialog(
                this,
                "Choose media type to update:",
                "Update Media",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Movie", "Series"},
                "Movie"
        );

        if (selected == null) return;

        if ("Movie".equals(selected.toString())) {
            dialogs.updateMovieDialog(this, controller, title.trim());
        } else {
            dialogs.updateSeriesDialog(this, controller, title.trim());
        }
    }

    // Αναζήτηση με τίτλο
    private void searchByTitle() {
        String query = JOptionPane.showInputDialog(this, "Enter title keyword:");
        if (query == null || query.trim().isEmpty()) return;
        controller.searchByTitle(query.trim());
    }

    // Αναζήτηση με κατηγορία
    private void searchByCategory() {
        String query = JOptionPane.showInputDialog(this, "Enter category keyword:");
        if (query == null || query.trim().isEmpty()) return;
        controller.searchByCategory(query.trim());
    }

    // Διαγραφή media
    private void deleteMedia() {
        String title = JOptionPane.showInputDialog(this, "Enter title to delete:");
        if (title == null || title.trim().isEmpty()) return;
        controller.deleteMedia(title.trim());
    }

    // Αποθήκευση βιβλιοθήκης
    private void saveLibrary() {
        String filename = JOptionPane.showInputDialog(this, "Enter filename to save library:");
        if (filename == null || filename.trim().isEmpty()) return;
        controller.saveLibrary(filename.trim());
    }

    // Φόρτωση βιβλιοθήκης
    private void loadLibrary() {
        String filename = JOptionPane.showInputDialog(this, "Enter filename to load library:");
        if (filename == null || filename.trim().isEmpty()) return;
        controller.loadLibrary(filename.trim());
    }

    // Προσθήκη κειμενου
    @Override
    public void appendText(String text) {
        outputArea.append(text + "\n\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    // Error
    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}