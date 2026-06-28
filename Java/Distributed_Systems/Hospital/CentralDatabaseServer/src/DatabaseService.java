import enums.AppointmentStatus;
import enums.TimeScheduleStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    public static Response handle(Request request) {
        if (request == null || request.getOperation() == null) {
            return new Response(false, "Invalid request", null);
        }

        return switch (request.getOperation()) {
            case ADD_DOCTOR -> addDoctor((Doctor) request.getData());
            case GET_DOCTORS -> getDoctors();
            case ADD_SCHEDULE -> addSchedule((TimeSchedule) request.getData());
            case UPDATE_SCHEDULE -> updateSchedule((TimeSchedule) request.getData());
            case DELETE_SCHEDULE -> deleteSchedule((String) request.getData());
            case SEARCH_SCHEDULES -> searchSchedules((TimeScheduleSearch) request.getData());
            case GET_SCHEDULE -> getSchedule((String) request.getData());
            case BOOK_APPOINTMENT -> bookAppointment((Appoinment) request.getData());
            case CANCEL_APPOINTMENT -> cancelAppointment((String) request.getData());
            case GET_USER_APPOINTMENTS -> getUserAppointments((String) request.getData());
            case GET_APPOINTMENTS_BY_SCHEDULE -> getAppointmentsBySchedule((String) request.getData());
            case ADD_REVIEW -> addReview((Object[]) request.getData());
            case GET_DOCTOR_REVIEWS -> getDoctorReviews((String) request.getData());
            default -> new Response(false, "Unknown Operation", null);
        };
    }

    private static synchronized Response addDoctor(Doctor doctor) {
        if (doctor == null) {
            return new Response(false, "Invalid doctor", null);
        }

        for (Doctor d : Database.doctors) {
            if (d.getEmail().equalsIgnoreCase(doctor.getEmail())) {
                return new Response(false, "Email already exists", null);
            }

            if (d.getPhone().equals(doctor.getPhone())) {
                return new Response(false, "Phone already exists", null);
            }
        }

        Database.doctors.add(doctor);
        FileManager.saveDoctors();

        return new Response(true, "Doctor added", null);
    }

    private static synchronized Response getDoctors() {
        return new Response(true, "", new ArrayList<>(Database.doctors));
    }

    private static synchronized Response addSchedule(TimeSchedule schedule) {
        if (schedule == null) {
            return new Response(false, "Invalid schedule", null);
        }

        Doctor doctor = Database.doctors.stream()
                .filter(d -> d.getDoctorID().equals(schedule.getDoctorID()))
                .findFirst()
                .orElse(null);

        if (doctor == null) {
            return new Response(false, "Doctor not found", null);
        }

        Database.schedules.add(schedule);
        FileManager.saveSchedules();

        return new Response(true, "Schedule added", null);
    }

    private static synchronized Response updateSchedule(TimeSchedule schedule) {
        if (schedule == null) {
            return new Response(false, "Invalid schedule", null);
        }

        for (TimeSchedule ts : Database.schedules) {
            if (ts.getTimeScheduleID().equals(schedule.getTimeScheduleID())) {
                if (ts.getStatus() == TimeScheduleStatus.BOOKED) {
                    return new Response(false, "Cannot update booked schedule", null);
                }

                ts.setDate(schedule.getDate());
                ts.setStartTime(schedule.getStartTime());
                ts.setDuration(schedule.getDuration());
                ts.setCost(schedule.getCost());

                FileManager.saveSchedules();

                return new Response(true, "Updated", null);
            }
        }

        return new Response(false, "Schedule not found", null);
    }

    private static synchronized Response deleteSchedule(String scheduleId) {
        if (scheduleId == null || scheduleId.isBlank()) {
            return new Response(false, "Invalid schedule id", null);
        }

        boolean removed = Database.schedules.removeIf(s -> s.getTimeScheduleID().equals(scheduleId));

        if (removed) {
            Database.appointments.removeIf(a -> a.getTimeScheduleID().equals(scheduleId));

            FileManager.saveSchedules();
            FileManager.saveAppointments();
        }

        return new Response(removed, removed ? "Deleted" : "Not Found", null);
    }

    private static synchronized Response getAppointmentsBySchedule(String scheduleId) {
        List<Appoinment> appointments = Database.appointments.stream()
                .filter(a -> a.getTimeScheduleID().equals(scheduleId))
                .toList();

        return new Response(true, "", new ArrayList<>(appointments));
    }

    private static synchronized Response searchSchedules(TimeScheduleSearch search) {
        if (search == null) {
            search = new TimeScheduleSearch();
        }

        refreshCompletedAppointments();

        List<TimeSchedule> results = new ArrayList<>();

        for (TimeSchedule ts : Database.schedules) {
            Doctor doctor = Database.doctors.stream()
                    .filter(d -> d.getDoctorID().equals(ts.getDoctorID()))
                    .findFirst()
                    .orElse(null);

            if (doctor == null) {
                continue;
            }

            if (search.getProfession() != null
                    && !search.getProfession().isBlank()
                    && !doctor.getProfession().equalsIgnoreCase(search.getProfession())) {
                continue;
            }

            if (search.getName() != null
                    && !search.getName().isBlank()
                    && !doctor.getName().equalsIgnoreCase(search.getName())) {
                continue;
            }

            if (search.getDepartment() != null
                    && !search.getDepartment().isBlank()
                    && !doctor.getDepartment().equalsIgnoreCase(search.getDepartment())) {
                continue;
            }

            if (search.getDateFrom() != null && ts.getDate().isBefore(search.getDateFrom())) {
                continue;
            }

            if (search.getDateTo() != null && ts.getDate().isAfter(search.getDateTo())) {
                continue;
            }

            if (search.getCost() > 0 && ts.getCost() > search.getCost()) {
                continue;
            }

            results.add(ts);
        }

        return new Response(true, "", results);
    }

    private static synchronized Response getSchedule(String scheduleId) {
        TimeSchedule schedule = Database.schedules.stream()
                .filter(s -> s.getTimeScheduleID().equals(scheduleId))
                .findFirst()
                .orElse(null);

        if (schedule == null) {
            return new Response(false, "Schedule not found", null);
        }

        return new Response(true, "", schedule);
    }

    private static synchronized Response bookAppointment(Appoinment appointment) {
        if (appointment == null) {
            return new Response(false, "Invalid appointment", null);
        }

        TimeSchedule ts = Database.schedules.stream()
                .filter(s -> s.getTimeScheduleID().equals(appointment.getTimeScheduleID()))
                .findFirst()
                .orElse(null);

        if (ts == null) {
            return new Response(false, "Schedule not found", null);
        }

        if (ts.getStatus() != TimeScheduleStatus.AVAILABLE) {
            return new Response(false, "Not available", null);
        }

        ts.setStatus(TimeScheduleStatus.BOOKED);
        Database.appointments.add(appointment);

        FileManager.saveAppointments();
        FileManager.saveSchedules();

        return new Response(true, "Booked", appointment);
    }

    private static synchronized Response cancelAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.isBlank()) {
            return new Response(false, "Invalid appointment id", null);
        }

        Appoinment app = Database.appointments.stream()
                .filter(a -> a.getAppointmentID().equals(appointmentId))
                .findFirst()
                .orElse(null);

        if (app == null) {
            return new Response(false, "Appointment not found", null);
        }

        Database.appointments.remove(app);

        Database.schedules.stream()
                .filter(s -> s.getTimeScheduleID().equals(app.getTimeScheduleID()))
                .findFirst()
                .ifPresent(ts -> ts.setStatus(TimeScheduleStatus.AVAILABLE));

        FileManager.saveAppointments();
        FileManager.saveSchedules();

        return new Response(true, "Cancelled", app);
    }

    private static synchronized Response getUserAppointments(String userId) {
        refreshCompletedAppointments();

        List<Appoinment> results = Database.appointments.stream()
                .filter(a -> a.getUserID().equals(userId))
                .toList();

        return new Response(true, "", new ArrayList<>(results));
    }

    private static synchronized Response addReview(Object[] data) {
        refreshCompletedAppointments();

        if (data == null || data.length < 4) {
            return new Response(false, "Invalid review data", null);
        }

        String userId = (String) data[0];
        String appointmentId = (String) data[1];
        int rating = (int) data[2];
        String comment = (String) data[3];

        if (rating < 1 || rating > 5) {
            return new Response(false, "Invalid rating", null);
        }

        Appoinment appointment = Database.appointments.stream()
                .filter(a -> a.getAppointmentID().equals(appointmentId))
                .findFirst()
                .orElse(null);

        if (appointment == null) {
            return new Response(false, "Appointment not found", null);
        }

        if (!appointment.getUserID().equals(userId)) {
            return new Response(false, "Appointment does not belong to user", null);
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return new Response(false, "Appointment is not completed", null);
        }

        boolean exists = Database.reviews.stream()
                .anyMatch(r -> r.getAppointmentID().equals(appointmentId));

        if (exists) {
            return new Response(false, "Already reviewed", null);
        }

        TimeSchedule ts = Database.schedules.stream()
                .filter(s -> s.getTimeScheduleID().equals(appointment.getTimeScheduleID()))
                .findFirst()
                .orElse(null);

        if (ts == null) {
            return new Response(false, "Schedule not found", null);
        }

        Review review = new Review(
                userId,
                ts.getDoctorID(),
                appointmentId,
                rating,
                comment == null ? "" : comment
        );

        Database.reviews.add(review);
        FileManager.saveReviews();

        return new Response(true, "Review added", null);
    }

    private static synchronized Response getDoctorReviews(String doctorId) {
        List<Review> results = Database.reviews.stream()
                .filter(r -> r.getDoctorID().equals(doctorId))
                .toList();

        return new Response(true, "", new ArrayList<>(results));
    }

    private static synchronized void refreshCompletedAppointments() {
        boolean changed = false;

        for (Appoinment appointment : Database.appointments) {
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                continue;
            }

            TimeSchedule ts = Database.schedules.stream()
                    .filter(s -> s.getTimeScheduleID().equals(appointment.getTimeScheduleID()))
                    .findFirst()
                    .orElse(null);

            if (ts == null) {
                continue;
            }

            LocalDateTime end = LocalDateTime.of(ts.getDate(), ts.getEndTime());

            if (LocalDateTime.now().isAfter(end)) {
                appointment.setStatus(AppointmentStatus.COMPLETED);
                changed = true;
            }
        }

        if (changed) {
            FileManager.saveAppointments();
        }
    }
}