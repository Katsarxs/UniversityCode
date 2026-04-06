/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package model;

import java.io.Serializable;
import java.util.Objects;

public abstract class Media implements Serializable {
    protected String title;
    protected String description;
    protected String category;
    protected int year;

    // Constructor media
    protected Media(String title, String description, String category, int year) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.category = category;
        this.year = year;
    }

    // Get τίτλο
    public String getTitle() {
        return title;
    }

    // Get περιγραφή
    public String getDescription() {
        return description;
    }

    // Get κατηγορία
    public String getCategory() {
        return category;
    }

    // Get έτος
    public int getYear() {
        return year;
    }

    // Θέτει περιγραφή
    public void setDescription(String description) {
        this.description = description;
    }

    // Θέτει κατηγορία
    public void setCategory(String category) {
        this.category = category;
    }

    // Θέτει έτος
    public void setYear(int year) {
        this.year = year;
    }

    // Επιστροφή λεπτομερειών
    public abstract String getDetails();

    @Override
    public String toString() {
        return getDetails();
    }
}