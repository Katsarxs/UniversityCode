import enums.AppointmentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Appoinment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String appointmentID, timeScheduleID, userID, name, debitCard;
    private AppointmentStatus status;

    public Appoinment(String userID, String timeScheduleID, String name, String debitCard) {
        this.userID = userID;
        this.timeScheduleID = timeScheduleID;
        this.name = name;
        this.debitCard = debitCard;
        this.appointmentID = UUID.randomUUID().toString();
        this.status = AppointmentStatus.IN_PROGRESS;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getTimeScheduleID() {
        return timeScheduleID;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getDebitCard() {
        return debitCard;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID + ", Schedule ID: " + timeScheduleID + ", Patient: " + name + ", Status: " + status;
    }
}