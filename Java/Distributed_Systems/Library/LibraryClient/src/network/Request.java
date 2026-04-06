/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package network;

import java.io.Serial;
import java.io.Serializable;
import model.Episode;
import model.Media;

public final class Request implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Τύποι ενεργειών
    public enum Type {
        ADD_MEDIA,
        UPDATE_MEDIA,
        DELETE_MEDIA,
        ADD_EPISODE,
        REMOVE_EPISODE,
        SEARCH_TITLE,
        SEARCH_CATEGORY,
        SAVE_LIBRARY,
        LOAD_LIBRARY
    }

    // Πεδία request
    private final Type type;
    private final Media media;
    private final String title;
    private final Episode episode;
    private final String query;
    private final String filename;

    // Constructor request
    private Request(Type type, Media media, String title, Episode episode, String query, String filename) {
        this.type = type;
        this.media = media;
        this.title = title;
        this.episode = episode;
        this.query = query;
        this.filename = filename;
    }

    // Προσθήκη media
    public static Request addMedia(Media media) {
        return new Request(Type.ADD_MEDIA, media, null, null, null, null);
    }

    // Ενημέρωση media
    public static Request updateMedia(Media media) {
        return new Request(Type.UPDATE_MEDIA, media, null, null, null, null);
    }

    // Διαγραφή media
    public static Request deleteMedia(String title) {
        return new Request(Type.DELETE_MEDIA, null, title, null, null, null);
    }

    // Προσθήκη επεισοδίου
    public static Request addEpisode(String seriesTitle, Episode episode) {
        return new Request(Type.ADD_EPISODE, null, seriesTitle, episode, null, null);
    }

    // Διαγραφή επεισοδίου
    public static Request removeEpisode(String seriesTitle, int seasonNumber, int episodeNumber) {
        return new Request(Type.REMOVE_EPISODE, null, seriesTitle,
                new Episode("", seasonNumber, episodeNumber, 0), null, null);
    }

    // Αναζήτηση τίτλου
    public static Request searchTitle(String query) {
        return new Request(Type.SEARCH_TITLE, null, null, null, query, null);
    }

    // Αναζήτηση κατηγορίας
    public static Request searchCategory(String query) {
        return new Request(Type.SEARCH_CATEGORY, null, null, null, query, null);
    }

    // Αποθήκευση βιβλιοθήκης
    public static Request saveLibrary(String filename) {
        return new Request(Type.SAVE_LIBRARY, null, null, null, null, filename);
    }

    // Φόρτωση βιβλιοθήκης
    public static Request loadLibrary(String filename) {
        return new Request(Type.LOAD_LIBRARY, null, null, null, null, filename);
    }

    // Getters
    public Type getType() {
        return type;
    }

    public Media getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public Episode getEpisode() {
        return episode;
    }

    public String getQuery() {
        return query;
    }

    public String getFilename() {
        return filename;
    }
}