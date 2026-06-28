import enums.Roles;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CentralAppointmentInterface extends Remote {
    // Εγγραφή και διαγραφή χρήστη
    boolean registerUser(String name, String amka, String phone, String email, String username, String password, Roles role) throws RemoteException;

    boolean deleteUser(String username, String password) throws RemoteException;

    // Σύνδεση και αποσύνδεση χρήστη
    String login(String username, String password, ClientCallback clientCallback) throws RemoteException;

    boolean logout(String sessionID) throws RemoteException;

    // Καταχώρηση γιατρού
    boolean addDoctor(String sessionID, String name, String profession, String department, String phone, String email, double baseCost) throws RemoteException;

    // Διαχείριση χρονοδιαγράμματος γιατρού
    User getUser(String sessionID) throws RemoteException;

    String getUserNameById(String sessionID, String userID) throws RemoteException;

    List<Doctor> getDoctors(String sessionID) throws RemoteException;

    boolean addTimeSchedule(String sessionID, String doctorID, LocalDate date, LocalTime startTime, int duration, double cost) throws RemoteException;

    boolean updateTimeSchedule(String sessionID, TimeSchedule timeSchedule) throws RemoteException;

    boolean deleteTimeSchedule(String sessionID, String timeScheduleID) throws RemoteException;

    // Αναζήτηση και κράτηση ραντεβού
    List<Appoinment> getUserAppointments(String sessionID) throws RemoteException;

    List<TimeSchedule> getAppointments(TimeScheduleSearch timeScheduleSearch) throws RemoteException;

    boolean bookAppointment(String sessionID, String timeScheduleID, String name, String debitCard) throws RemoteException;

    // Get in line
    boolean waitInLine(String sessionID, String timeScheduleID) throws RemoteException;

    // Ακύρωση κράτησης
    boolean cancelAppointment(String sessionID, String appointmentID) throws RemoteException;

    // Αξιολόγηση γιατρού
    boolean reviewAppointment(String sessionID, String appointmentID, int reviewRating, String reviewComment) throws RemoteException;

    List<Review> accessDoctorReviews(String sessionID, String doctorID) throws RemoteException;
}
