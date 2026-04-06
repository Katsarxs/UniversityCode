/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package model;

import java.io.Serial;

public final class Movie extends Media {
    @Serial
    private static final long serialVersionUID = 1L;
    private int duration;
    private String producer;

    // Constructor ταινίας
    public Movie(String title, String description, String category, int year, int duration, String producer) {
        super(title, description, category, year);
        this.duration = duration;
        this.producer = producer;
    }

    // Παίρνει διάρκεια
    public int getDuration() {
        return duration;
    }

    // Θέτει διάρκεια
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // Παίρνει παραγωγό
    public String getProducer() {
        return producer;
    }

    // Θέτει παραγωγό
    public void setProducer(String producer) {
        this.producer = producer;
    }

    // Επιστροφή λεπτομερειών
    @Override
    public String getDetails() {
        return "Movie: " + title + "\n"
                + "Description: " + description + "\n"
                + "Category: " + category + "\n"
                + "Year: " + year + "\n"
                + "Duration: " + duration + " min\n"
                + "Producer: " + producer;
    }
}