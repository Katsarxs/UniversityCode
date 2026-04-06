/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package client.ui;

import client.ClientController;
import model.Movie;
import model.Series;

import javax.swing.*;

public class ClientDialogs {

    // Παράθυρο για προσθήκη ταινιας
    public void addMovieDialog(JFrame frame, ClientController controller) {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField producerField = new JTextField();

        Object[] fields = {
                "Title:", titleField,
                "Description:", descField,
                "Category:", categoryField,
                "Year:", yearField,
                "Duration (min):", durationField,
                "Producer:", producerField
        };

        
        int result = JOptionPane.showConfirmDialog(frame, fields, "Add Movie", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Movie movie = new Movie(
                    titleField.getText().trim(),
                    descField.getText().trim(),
                    categoryField.getText().trim(),
                    Integer.parseInt(yearField.getText().trim()),
                    Integer.parseInt(durationField.getText().trim()),
                    producerField.getText().trim()
            );
            controller.addMovie(movie);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Year and Duration must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Παράθυρο για προσθήκη σειράς
    public void addSeriesDialog(JFrame frame, ClientController controller) {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField seasonsField = new JTextField();

        Object[] fields = {
                "Title:", titleField,
                "Description:", descField,
                "Category:", categoryField,
                "Year:", yearField,
                "Seasons:", seasonsField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields, "Add Series", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Series series = new Series(
                    titleField.getText().trim(),
                    descField.getText().trim(),
                    categoryField.getText().trim(),
                    Integer.parseInt(yearField.getText().trim()),
                    Integer.parseInt(seasonsField.getText().trim())
            );
            controller.addSeries(series);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Year and Seasons must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Παράθυρο για ενημέρωση property ταινίας
    public void updateMovieDialog(JFrame frame, ClientController controller, String title) {
        JTextField descField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField producerField = new JTextField();

        Object[] fields = {
                "Description :", descField,
                "Category :", categoryField,
                "Year :", yearField,
                "Duration :", durationField,
                "Producer :", producerField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields, "Update Movie", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Movie updated = new Movie(
                    title,
                    emptyToNull(descField.getText()),
                    emptyToNull(categoryField.getText()),
                    parseIntOrZero(yearField.getText()),
                    parseIntOrZero(durationField.getText()),
                    emptyToNull(producerField.getText())
            );
            controller.updateMedia(updated, "movie");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Year and Duration must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Παράθυρο για ενημέρωση property σειράς
    public void updateSeriesDialog(JFrame frame, ClientController controller, String title) {
        JTextField descField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField seasonsField = new JTextField();

        Object[] fields = {
                "Description :", descField,
                "Category :", categoryField,
                "Year :", yearField,
                "Seasons :", seasonsField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields, "Update Series", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Series updated = new Series(
                    title,
                    emptyToNull(descField.getText()),
                    emptyToNull(categoryField.getText()),
                    parseIntOrZero(yearField.getText()),
                    parseIntOrZero(seasonsField.getText())
            );
            controller.updateMedia(updated, "series");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Year and Seasons must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Παράθυρο για διαγραφή επεισοδίου
    public void removeEpisodeDialog(JFrame frame, ClientController controller) {
        JTextField seriesField = new JTextField();
        JTextField seasonField = new JTextField();
        JTextField episodeField = new JTextField();

        Object[] fields = {
                "Series Title:", seriesField,
                "Season Number:", seasonField,
                "Episode Number:", episodeField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields, "Remove Episode", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            String seriesTitle = seriesField.getText().trim();
            int seasonNumber = Integer.parseInt(seasonField.getText().trim());
            int episodeNumber = Integer.parseInt(episodeField.getText().trim());

            controller.removeEpisode(seriesTitle, seasonNumber, episodeNumber);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Season and Episode Number must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper συνάρτηση απο String σε integer
    private int parseIntOrZero(String text) {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) return 0;
        return Integer.parseInt(value);
    }

    // Helper συνάρτηση απο κενο σε null
    private String emptyToNull(String text) {
        String value = text == null ? "" : text.trim();
        return value.isEmpty() ? null : value;
    }
}