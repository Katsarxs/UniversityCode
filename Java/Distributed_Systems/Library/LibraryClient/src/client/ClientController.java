/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package client;

import client.ui.ClientView;
import model.Episode;
import model.Media;
import model.Movie;
import model.Series;
import network.Request;
import network.Response;

import java.io.IOException;
import java.util.List;

public class ClientController {

    private final ClientConnection connection;
    private ClientView view;

    public ClientController(ClientConnection connection) {
        this.connection = connection;
    }

    public void setView(ClientView view) {
        this.view = view;
    }

    // Προσθήκη ταινίας
    public void addMovie(Movie movie) {
        try {
            Response response = connection.send(Request.addMedia(movie));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to add movie: " + e.getMessage());
        }
    }

    // Προσθήκη σειράς
    public void addSeries(Series series) {
        try {
            Response response = connection.send(Request.addMedia(series));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to add series: " + e.getMessage());
        }
    }

    // Προσθήκη επεισοδίου
    public void addEpisode(String seriesTitle, Episode episode) {
        try {
            Response response = connection.send(Request.addEpisode(seriesTitle, episode));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to add episode: " + e.getMessage());
        }
    }

    // Διαγραφή επεισοδίου
    public void removeEpisode(String seriesTitle, int seasonNumber, int episodeNumber) {
        try {
            Response response = connection.send(Request.removeEpisode(seriesTitle, seasonNumber, episodeNumber));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to delete episode: " + e.getMessage());
        }
    }

    // Ενημέρωση media
    public void updateMedia(Media media, String typeLabel) {
        try {
            Response response = connection.send(Request.updateMedia(media));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to update " + typeLabel + ": " + e.getMessage());
        }
    }

    // Διαγραφή media
    public void deleteMedia(String title) {
        try {
            Response response = connection.send(Request.deleteMedia(title));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to delete media: " + e.getMessage());
        }
    }

    // Αποθήκευση βιβλιοθήκης
    public void saveLibrary(String filename) {
        try {
            Response response = connection.send(Request.saveLibrary(filename));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to save library: " + e.getMessage());
        }
    }

    // Φόρτωση βιβλιοθήκης
    public void loadLibrary(String filename) {
        try {
            Response response = connection.send(Request.loadLibrary(filename));
            showResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to load library: " + e.getMessage());
        }
    }

    // Αναζήτηση τίτλου
    public void searchByTitle(String query) {
        try {
            Response response = connection.send(Request.searchTitle(query));
            showSearchResults("Title search", query, response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to search by title: " + e.getMessage());
        }
    }

    // Αναζήτηση κατηγορίας
    public void searchByCategory(String query) {
        try {
            Response response = connection.send(Request.searchCategory(query));
            showSearchResults("Category search", query, response);
        } catch (IOException | ClassNotFoundException e) {
            view.showError("Failed to search by category: " + e.getMessage());
        }
    }

    // Εμφάνιση response
    private void showResponse(Response response) {
        if (response == null) {
            view.appendText("No response received.");
            return;
        }

        view.appendText((response.isSuccess() ? "[SUCCESS] " : "[FAILURE] ") + response.getMessage());

        List<Media> data = response.getData();
        if (data != null && !data.isEmpty()) {
            view.appendText("Results:");
            for (Media media : data) {
                view.appendText(media.getDetails());
            }
        }
    }

    // Εμφάνιση αποτελεσμάτων
    private void showSearchResults(String label, String query, Response response) {
        if (response == null) {
            view.appendText(label + " failed.");
            return;
        }

        view.appendText(label + " for: " + query);

        List<Media> data = response.getData();
        if (data == null || data.isEmpty()) {
            view.appendText("No results found.");
            return;
        }

        for (Media media : data) {
            view.appendText(media.getDetails());
        }
    }
}