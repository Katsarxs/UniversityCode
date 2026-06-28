import enums.Roles;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CentralAppointmentInterface extends Remote {
    /**
     * Registers a new user.
     * @param name User's full name.
     * @param amka User's AMKA.
     * @param phone User's phone number.
     * @param email User's email address.
     * @param username User's username.
     * @param password User's password.
     * @param role User's role.
     * @return {@code true} If the registration was completed successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean registerUser(String name, String amka, String phone, String email, String username, String password, Roles role) throws RemoteException;

    /**
     * Deletes an existing user.
     * @param username User's username.
     * @param password User's password.
     * @return {@code true} If the user was deleted successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean deleteUser(String username, String password) throws RemoteException;

    /**
     * User login.
     * @param username User's username.
     * @param password User's password.
     * @param clientCallback Client's callback.
     * @return Session ID of the user.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    String login(String username, String password, ClientCallback clientCallback) throws RemoteException;

    /**
     * User logout.
     * @param sessionID Session ID of the user.
     * @return {@code true} If the logout was completed successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean logout(String sessionID) throws RemoteException;

    /**
     * Adds a new doctor.
     * @param sessionID Session ID of the user doing this task.
     * @param name Doctor's full name.
     * @param profession Doctor's profession.
     * @param department Department to which the doctor belongs.
     * @param phone Doctor's phone number.
     * @param email Doctor's email address.
     * @param baseCost Doctor's base appointment cost.
     * @return {@code true} If the doctor was added successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean addDoctor(String sessionID, String name, String profession, String department, String phone, String email, double baseCost) throws RemoteException;

    /**
     * Retrieves user with the specific session ID.
     * @param sessionID User's session ID.
     * @return User
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    User getUser(String sessionID) throws RemoteException;

    /**
     * Retrieves a user's name by user ID.
     * @param sessionID Session ID of the user doing this task.
     * @param userID User's ID.
     * @return Name of the user.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    String getUserNameById(String sessionID, String userID) throws RemoteException;

    /**
     * Retrieves the list of all doctors.
     * @param sessionID Session ID of the user doing this task.
     * @return A list of all doctors.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    List<Doctor> getDoctors(String sessionID) throws RemoteException;

    /**
     * Adds a time schedule for a doctor.
     * @param sessionID Session ID of the user doing this task.
     * @param doctorID Doctor's ID.
     * @param date Date of time schedule.
     * @param startTime Starting time of time schedule.
     * @param duration Duration of time schedule in minutes.
     * @param cost Cost of time schedule.
     * @return {@code true} If the schedule was added successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean addTimeSchedule(String sessionID, String doctorID, LocalDate date, LocalTime startTime, int duration, double cost) throws RemoteException;

    /**
     * Updates a time schedule.
     * @param sessionID Session ID of the user doing this task.
     * @param timeSchedule Updated time schedule.
     * @return {@code true} If the time schedule was updated successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean updateTimeSchedule(String sessionID, TimeSchedule timeSchedule) throws RemoteException;

    /**
     * Deletes a time schedule.
     * @param sessionID Session ID of the user doing this task.
     * @param timeScheduleID Time schedule's ID.
     * @return {@code true} If the time schedule was deleted successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean deleteTimeSchedule(String sessionID, String timeScheduleID) throws RemoteException;

    /**
     * Retrieves all appointments for a user.
     * @param sessionID Session ID of the user doing this task.
     * @return A list of appointments of the user.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    List<Appoinment> getUserAppointments(String sessionID) throws RemoteException;

    /**
     * Retrieves available time schedules based on criteria.
     *
     * @param timeScheduleSearch Criteria of time schedule.
     * @return A list of time schedules matching criteria.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    List<TimeSchedule> getAppointments(TimeScheduleSearch timeScheduleSearch) throws RemoteException;

    /**
     * Books an appointment for a user.
     *
     * @param sessionID Session ID of the user doing this task.
     * @param timeScheduleID Time schedule's ID.
     * @param name The name of the person booking the appointment
     * @param debitCard The debit card number used for payment
     * @return {@code true} If the appointment was booked successfully; {@code false} otherwise
     * @throws RemoteException If a communication error occurs during the remote method invocation
     */
    boolean bookAppointment(String sessionID, String timeScheduleID, String name, String debitCard) throws RemoteException;

    /**
     * Places a user in the waiting line for a specific time schedule.
     * @param sessionID Session ID of the user doing this task.
     * @param timeScheduleID Time schedule's ID to wait for.
     * @return {@code true} If the user was successfully added to the line; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean waitInLine(String sessionID, String timeScheduleID) throws RemoteException;

    /**
     * Cancels an existing appointment.
     * @param sessionID Session ID of the user doing this task.
     * @param appointmentID The ID of the appointment to cancel.
     * @return {@code true} If the appointment was canceled successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean cancelAppointment(String sessionID, String appointmentID) throws RemoteException;

    /**
     * Submits a review and rating for a specific completed appointment.
     * @param sessionID Session ID of the user doing this task.
     * @param appointmentID The ID of the appointment being reviewed.
     * @param reviewRating The numerical rating given to the doctor.
     * @param reviewComment Additional feedback or text comments.
     * @return {@code true} If the review was submitted successfully; {@code false} otherwise.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    boolean reviewAppointment(String sessionID, String appointmentID, int reviewRating, String reviewComment) throws RemoteException;

    /**
     * Retrieves all reviews for a specific doctor.
     * @param sessionID Session ID of the user doing this task.
     * @param doctorID The ID of the doctor whose reviews are being requested.
     * @return A list of reviews associated with the specified doctor.
     * @throws RemoteException If a communication error occurs during the remote method invocation.
     */
    List<Review> accessDoctorReviews(String sessionID, String doctorID) throws RemoteException;
}
