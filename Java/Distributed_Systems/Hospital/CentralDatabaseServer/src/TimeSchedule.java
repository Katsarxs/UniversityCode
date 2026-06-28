import enums.TimeScheduleStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TimeSchedule implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String doctorID, timeScheduleID;
    private LocalDate date;
    private LocalTime startTime;
    private int duration;
    private double cost;
    private TimeScheduleStatus status;

    public TimeSchedule(String doctorID, LocalDate date, LocalTime startTime, int duration, double cost) {
        this.doctorID = doctorID;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.cost = cost;
        this.status = TimeScheduleStatus.AVAILABLE;
        this.timeScheduleID = UUID.randomUUID().toString();
    }

    public String getDoctorID() {
        return doctorID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public TimeScheduleStatus getStatus() {
        return status;
    }

    public String getTimeScheduleID() {
        return timeScheduleID;
    }

    public void setStatus(TimeScheduleStatus status) {
        this.status = status;
    }

    public LocalTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "Date : " + date + " Start Time : " + startTime + " Duration : " + duration + " Cost : " + cost;
    }
}
