/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class Episode implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String title;
    private final int seasonNumber;
    private final int number;
    private final int duration;

    public Episode(String title, int seasonNumber, int number, int duration) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.seasonNumber = seasonNumber;
        this.number = number;
        this.duration = duration;
    }

    // Get τίτλο
    public String getTitle() {
        return title;
    }

    // Get αριθμού σεζόν
    public int getSeasonNumber() {
        return seasonNumber;
    }

    // Get αριθμού επεισοδίου
    public int getNumber() {
        return number;
    }

    // Get διάρκεια
    public int getDuration() {
        return duration;
    }

    // Get λεπτομερειών
    public String getDetails() {
        return "S" + seasonNumber + "E" + number + " - " + title + " (" + duration + " min)";
    }

    @Override
    public String toString() {
        return getDetails();
    }
}