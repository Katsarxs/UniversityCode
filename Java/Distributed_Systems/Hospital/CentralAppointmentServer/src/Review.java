import java.io.Serializable;
import java.util.UUID;

public class Review implements Serializable {
    private final String reviewID, appointmentID, userID, doctorID, reviewComment;
    private final int reviewRating;

    public Review(String userID, String doctorID, String appointmentID, int reviewRating, String reviewComment) {
        this.userID = userID;
        this.doctorID = doctorID;
        this.appointmentID = appointmentID;
        this.reviewRating = reviewRating;
        this.reviewComment = reviewComment;
        this.reviewID = UUID.randomUUID().toString();
    }

    public String getUserID() {
        return userID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public String getReviewComment() {
        return reviewComment;
    }
}