/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import model.Episode;
import model.Media;
import model.Movie;
import model.Series;

public final class Library implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private List<Media> mediaList = new ArrayList<>();

    // Προσθήκη media
    public boolean addMedia(Media media) {
        if (media == null || existsTitle(media.getTitle())) {
            return false;
        }
        mediaList.add(media);
        return true;
    }

    // Διαγραφή media
    public boolean removeMedia(String title) {
        return mediaList.removeIf(m -> m.getTitle().equalsIgnoreCase(title));
    }

    // Ενημέρωση media
    public boolean updateMediaPartial(Media updated) {
        if (updated == null) {
            return false;
        }

        for (Media original : mediaList) {
            if (!original.getTitle().equalsIgnoreCase(updated.getTitle())) {
                continue;
            }

            if (original instanceof Movie origMovie && updated instanceof Movie updMovie) {
                if (updMovie.getDescription() != null) origMovie.setDescription(updMovie.getDescription());
                if (updMovie.getCategory() != null) origMovie.setCategory(updMovie.getCategory());
                if (updMovie.getYear() != 0) origMovie.setYear(updMovie.getYear());
                if (updMovie.getDuration() != 0) origMovie.setDuration(updMovie.getDuration());
                if (updMovie.getProducer() != null) origMovie.setProducer(updMovie.getProducer());
                return true;
            }

            if (original instanceof Series origSeries && updated instanceof Series updSeries) {
                if (updSeries.getDescription() != null) origSeries.setDescription(updSeries.getDescription());
                if (updSeries.getCategory() != null) origSeries.setCategory(updSeries.getCategory());
                if (updSeries.getYear() != 0) origSeries.setYear(updSeries.getYear());
                if (updSeries.getSeasonsNumber() != 0) origSeries.setSeasonsNumber(updSeries.getSeasonsNumber());
                return true;
            }
            return false;
        }
        return false;
    }

    // Προσθήκη επεισοδίου
    public boolean addEpisodeToSeries(String seriesTitle, Episode episode) {
        Media media = findByTitle(seriesTitle);
        return media instanceof Series series && series.addEpisode(episode);
    }

    // Διαγραφή επεισοδίου
    public boolean removeEpisodeFromSeries(String seriesTitle, int season, int episodeNumber) {
        Media media = findByTitle(seriesTitle);
        return media instanceof Series series && series.removeEpisode(season, episodeNumber);
    }

    // Αναζήτηση τίτλου
    public List<Media> searchByTitle(String title) {
        String needle = title == null ? "" : title.toLowerCase();
        return mediaList.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(needle))
                .toList();
    }

    // Αναζήτηση κατηγορίας
    public List<Media> searchByCategory(String category) {
        String needle = category == null ? "" : category;
        return mediaList.stream()
                .filter(m -> m.getCategory() != null && m.getCategory().equalsIgnoreCase(needle))
                .toList();
    }

    // Αποθήκευση αρχείου
    public boolean saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Path.of(filename)))) {
            oos.writeObject(mediaList);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Φόρτωση αρχείου
    @SuppressWarnings("unchecked")
    public boolean loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Path.of(filename)))) {
            Object obj = ois.readObject();
            if (!(obj instanceof List<?> list)) {
                return false;
            }
            mediaList = new ArrayList<>((List<Media>) list);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    // Get media
    public List<Media> getAllMedia() {
        return List.copyOf(mediaList);
    }

    // Αναζήτηση τίτλου
    private Media findByTitle(String title) {
        for (Media media : mediaList) {
            if (media.getTitle().equalsIgnoreCase(title)) {
                return media;
            }
        }
        return null;
    }

    // Έλεγχος ύπαρξης
    private boolean existsTitle(String title) {
        return findByTitle(title) != null;
    }
}