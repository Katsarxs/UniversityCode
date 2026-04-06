/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package model;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Series extends Media {
    @Serial
    private static final long serialVersionUID = 1L;
    private int seasonsNumber;
    private final List<Episode> episodes = new ArrayList<>(); // Για επεισόδια

    // Constructor σειράς
    public Series(String title, String description, String category, int year, int seasonsNumber) {
        super(title, description, category, year);
        this.seasonsNumber = seasonsNumber;
    }

    // Getter σεζόν
    public int getSeasonsNumber() {
        return seasonsNumber;
    }

    // Set σεζόν
    public void setSeasonsNumber(int seasonsNumber) {
        this.seasonsNumber = seasonsNumber;
    }

    // Getter επεισόδια
    public List<Episode> getEpisodes() {
        return Collections.unmodifiableList(episodes);
    }

    // Προσθήκη επεισοδίου
    public boolean addEpisode(Episode episode) {
        Objects.requireNonNull(episode, "episode must not be null");

        if (episode.getSeasonNumber() < 1 || episode.getSeasonNumber() > seasonsNumber) {
            return false;
        }

        boolean duplicate = episodes.stream().anyMatch(existing ->
                existing.getSeasonNumber() == episode.getSeasonNumber()
                        && existing.getNumber() == episode.getNumber());

        if (duplicate) {
            return false;
        }

        episodes.add(episode);
        return true;
    }

    // Διαγραφή επεισοδίου
    public boolean removeEpisode(int season, int episodeNumber) {
        return episodes.removeIf(ep ->
                ep.getSeasonNumber() == season && ep.getNumber() == episodeNumber);
    }

    // Επιστροφή details
    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Series: ").append(title).append('\n')
                .append("Description: ").append(description).append('\n')
                .append("Category: ").append(category).append('\n')
                .append("Year: ").append(year).append('\n')
                .append("Seasons: ").append(seasonsNumber).append('\n')
                .append("Episodes:\n");

        for (Episode episode : episodes) {
            sb.append("  ").append(episode.getDetails()).append('\n');
        }

        return sb.toString();
    }
}