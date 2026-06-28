import enums.Roles;
import enums.TimeScheduleStatus;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CentralAppointmentServer extends UnicastRemoteObject implements CentralAppointmentInterface {
    private static final String USERS_FILE = "users.dat";

    // Λίστες για δεδομένα
    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, User> activeUsers = new ConcurrentHashMap<>();
    private final Map<String, ClientCallback> callbacks = new ConcurrentHashMap<>();
    private final Map<String, Queue<User>> waitlists = new ConcurrentHashMap<>();
    private final DatabaseClient db = new DatabaseClient();

    protected CentralAppointmentServer() throws RemoteException {
        super();
        loadUsers();
    }

    // Εγγραφή χρήστη
    @Override
    public boolean registerUser(String name, String amka, String phone, String email, String username, String password, Roles role) throws RemoteException {
        if (name == null || name.isBlank() || amka == null || amka.isBlank() || phone == null || phone.isBlank() || email == null || email.isBlank() || username == null || username.isBlank() || password == null || password.isBlank() || role == null) {
            return false;
        }

        synchronized (users) {
            for (User user : users) {
                if (user.getAmka().equals(amka) || user.getPhone().equals(phone) || user.getEmail().equalsIgnoreCase(email) || user.getUsername().equalsIgnoreCase(username)) {
                    return false;
                }
            }

            User newUser = new User(name, amka, phone, email, username, password, role);
            users.add(newUser);
            saveUsers();
            return true;
        }
    }

    // Διαγραφή χρήστη
    @Override
    public boolean deleteUser(String username, String password) throws RemoteException {
        if (username == null || password == null) {
            return false;
        }

        synchronized (users) {
            Iterator<User> iterator = users.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    iterator.remove();
                    activeUsers.entrySet().removeIf(entry -> entry.getValue().getUserID().equals(user.getUserID()));
                    callbacks.keySet().removeIf(sessionId -> !activeUsers.containsKey(sessionId));

                    for (Queue<User> queue : waitlists.values()) {
                        queue.remove(user);
                    }

                    saveUsers();
                    return true;
                }
            }
        }

        return false;
    }

    // Σύνδεση χρήστη
    @Override
    public String login(String username, String password, ClientCallback clientCallback) throws RemoteException {
        if (username == null || password == null || clientCallback == null) {
            return null;
        }

        synchronized (users) {
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    String sessionID = UUID.randomUUID().toString();
                    activeUsers.put(sessionID, user);
                    callbacks.put(sessionID, clientCallback);
                    return sessionID;
                }
            }
        }

        return null;
    }

    // Έξοδος χρήστη
    @Override
    public boolean logout(String sessionID) throws RemoteException {
        if (sessionID == null) {
            return false;
        }

        activeUsers.remove(sessionID);
        callbacks.remove(sessionID);
        return true;
    }

    // Επιστροφή χρήστη
    @Override
    public User getUser(String sessionID) throws RemoteException {
        return activeUsers.get(sessionID);
    }

    // Επιστροφή ονόματος χρήστη από id
    @Override
    public String getUserNameById(String sessionID, String userID) throws RemoteException {
        User requester = activeUsers.get(sessionID);

        if (requester == null || requester.getRole() != Roles.ADMIN || userID == null || userID.isBlank()) {
            return "Unknown user";
        }

        synchronized (users) {
            for (User user : users) {
                if (user.getUserID().equals(userID)) {
                    return user.getName();
                }
            }
        }

        return "Unknown user";
    }

    // Προσθήκη γιατρού
    @Override
    public boolean addDoctor(String sessionID, String name, String profession, String department, String phone, String email, double baseCost) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.ADMIN) {
            return false;
        }

        if (name == null || name.isBlank() || profession == null || profession.isBlank() || department == null || department.isBlank() || phone == null || phone.isBlank() || email == null || email.isBlank() || baseCost < 0) {
            return false;
        }

        Doctor doctor = new Doctor(name, profession, department, phone, email, baseCost);
        Response response = db.sendRequest(new Request(Operation.ADD_DOCTOR, doctor));

        return response != null && response.isSuccess();
    }

    // Επιστροφή λίστας με γιατρούς
    @Override
    @SuppressWarnings("unchecked")
    public List<Doctor> getDoctors(String sessionID) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null) {
            return new ArrayList<>();
        }

        Response response = db.sendRequest(new Request(Operation.GET_DOCTORS, null));

        if (response == null || !response.isSuccess() || response.getData() == null) {
            return new ArrayList<>();
        }

        return (List<Doctor>) response.getData();
    }

    // Προσθήκη χρονοδιαγράμματος
    @Override
    public boolean addTimeSchedule(String sessionID, String doctorID, LocalDate date, LocalTime startTime, int duration, double cost) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.ADMIN) {
            return false;
        }

        if (doctorID == null || doctorID.isBlank() || date == null || startTime == null || duration <= 0 || cost < 0) {
            return false;
        }

        TimeSchedule timeSchedule = new TimeSchedule(doctorID, date, startTime, duration, cost);
        Response response = db.sendRequest(new Request(Operation.ADD_SCHEDULE, timeSchedule));

        return response != null && response.isSuccess();
    }

    // Ενημέρωση χρονοδιαγράμματος
    @Override
    public boolean updateTimeSchedule(String sessionID, TimeSchedule timeSchedule) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.ADMIN || timeSchedule == null) {
            return false;
        }
        Response response = db.sendRequest(new Request(Operation.UPDATE_SCHEDULE, timeSchedule));
        return response != null && response.isSuccess();
    }

    // Ακύρωση χρονοδιαγράμματος
    @Override
    @SuppressWarnings("unchecked")
    public boolean deleteTimeSchedule(String sessionID, String timeScheduleID) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.ADMIN || timeScheduleID == null || timeScheduleID.isBlank()) {
            return false;
        }

        Response appointmentsResponse = db.sendRequest(new Request(Operation.GET_APPOINTMENTS_BY_SCHEDULE, timeScheduleID));
        List<Appoinment> affectedAppointments = new ArrayList<>();

        if (appointmentsResponse != null && appointmentsResponse.isSuccess() && appointmentsResponse.getData() != null) {
            affectedAppointments = (List<Appoinment>) appointmentsResponse.getData();
        }

        Response deleteResponse = db.sendRequest(new Request(Operation.DELETE_SCHEDULE, timeScheduleID));

        if (deleteResponse == null || !deleteResponse.isSuccess()) {
            return false;
        }

        for (Appoinment appointment : affectedAppointments) {
            notifyOnlineUserByUserId(appointment.getUserID(), true, "Your appointment was deleted. Book a new one.");
        }

        Queue<User> queue = waitlists.remove(timeScheduleID);

        if (queue != null) {
            for (User waitlistedUser : queue) {
                notifyOnlineUserByUserId(waitlistedUser.getUserID(), false, "The time schedule associated with the waitlist was deleted.");
            }
        }

        return true;
    }

    // Επσιτροφή λίστας με ραντεβού χρήστη
    @Override
    @SuppressWarnings("unchecked")
    public List<Appoinment> getUserAppointments(String sessionID) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null) {
            return new ArrayList<>();
        }

        Response response = db.sendRequest(new Request(Operation.GET_USER_APPOINTMENTS, user.getUserID()));

        if (response == null || !response.isSuccess() || response.getData() == null) {
            return new ArrayList<>();
        }

        return (List<Appoinment>) response.getData();
    }

    // Επιστροφή των ραντεβού από αναζήτηση
    @Override
    @SuppressWarnings("unchecked")
    public List<TimeSchedule> getAppointments(TimeScheduleSearch search) throws RemoteException {
        if (search == null) {
            search = new TimeScheduleSearch();
        }

        Response response = db.sendRequest(new Request(Operation.SEARCH_SCHEDULES, search));

        if (response == null || !response.isSuccess() || response.getData() == null) {
            return new ArrayList<>();
        }

        return (List<TimeSchedule>) response.getData();
    }

    // Κράτηση ραντεβού
    @Override
    public boolean bookAppointment(String sessionID, String timeScheduleID, String name, String debitCard) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.PATIENT) {
            return false;
        }

        if (timeScheduleID == null || timeScheduleID.isBlank() || name == null || name.isBlank() || debitCard == null || debitCard.isBlank()) {
            return false;
        }

        Appoinment appointment = new Appoinment(user.getUserID(), timeScheduleID, name, debitCard);
        Response response = db.sendRequest(new Request(Operation.BOOK_APPOINTMENT, appointment));
        return response != null && response.isSuccess();
    }

    // Αίτηση για λίστα αναμονής
    @Override
    @SuppressWarnings("unchecked")
    public boolean waitInLine(String sessionID, String timeScheduleID) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.PATIENT || timeScheduleID == null || timeScheduleID.isBlank()) {
            return false;
        }

        Response scheduleResponse = db.sendRequest(new Request(Operation.GET_SCHEDULE, timeScheduleID));

        if (scheduleResponse == null || !scheduleResponse.isSuccess() || scheduleResponse.getData() == null) {
            return false;
        }

        TimeSchedule ts = (TimeSchedule) scheduleResponse.getData();

        if (ts.getStatus() != TimeScheduleStatus.BOOKED) {
            return false;
        }

        Response appointmentsResponse = db.sendRequest(new Request(Operation.GET_USER_APPOINTMENTS, user.getUserID()));

        if (appointmentsResponse != null && appointmentsResponse.isSuccess() && appointmentsResponse.getData() != null) {
            List<Appoinment> appointments = (List<Appoinment>) appointmentsResponse.getData();

            for (Appoinment appointment : appointments) {
                if (appointment.getTimeScheduleID().equals(timeScheduleID)) {
                    return false;
                }
            }
        }

        Queue<User> queue = waitlists.computeIfAbsent(timeScheduleID, key -> new ArrayDeque<>());

        synchronized (queue) {
            if (queue.contains(user)) {
                return false;
            }

            queue.add(user);
        }

        return true;
    }

    // Ακύρωση ραντεβού
    @Override
    public boolean cancelAppointment(String sessionID, String appointmentID) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.PATIENT || appointmentID == null || appointmentID.isBlank()) {
            return false;
        }
        Appoinment appointmentToCancel = findUserAppointment(user.getUserID(), appointmentID);
        if (appointmentToCancel == null) {
            return false;
        }
        Response scheduleResponse = db.sendRequest(new Request(Operation.GET_SCHEDULE, appointmentToCancel.getTimeScheduleID()));
        if (scheduleResponse == null || !scheduleResponse.isSuccess() || scheduleResponse.getData() == null) {
            return false;
        }

        TimeSchedule schedule = (TimeSchedule) scheduleResponse.getData();
        LocalDateTime appointmentDateTime = LocalDateTime.of(schedule.getDate(), schedule.getStartTime());
        if (schedule.getDate().equals(LocalDate.now())) {
            return false;
        }

        if (LocalDateTime.now().plusHours(24).isAfter(appointmentDateTime)) {
            return false;
        }


        Response cancelResponse = db.sendRequest(new Request(Operation.CANCEL_APPOINTMENT, appointmentID));
        if (cancelResponse == null || !cancelResponse.isSuccess() || cancelResponse.getData() == null) {
            return false;
        }

        Appoinment cancelledAppointment = (Appoinment) cancelResponse.getData();
        notifyFirstOnlineWaitlistedUser(cancelledAppointment.getTimeScheduleID());

        return true;
    }

    // Αξιολόηγηση ραντεβού
    @Override
    public boolean reviewAppointment(String sessionID, String appointmentID, int reviewRating, String reviewComment) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.PATIENT) {
            return false;
        }

        if (appointmentID == null || appointmentID.isBlank() || reviewRating < 1 || reviewRating > 5) {
            return false;
        }

        Object[] payload = new Object[]{user.getUserID(), appointmentID, reviewRating, reviewComment};
        Response response = db.sendRequest(new Request(Operation.ADD_REVIEW, payload));

        return response != null && response.isSuccess();
    }

    // Επιστροφή αξιολογήσεων γιατρών
    @Override
    @SuppressWarnings("unchecked")
    public List<Review> accessDoctorReviews(String sessionID, String doctorID) throws RemoteException {
        User user = activeUsers.get(sessionID);

        if (user == null || user.getRole() != Roles.ADMIN || doctorID == null || doctorID.isBlank()) {
            return new ArrayList<>();
        }

        Response response = db.sendRequest(new Request(Operation.GET_DOCTOR_REVIEWS, doctorID));

        if (response == null || !response.isSuccess() || response.getData() == null) {
            return new ArrayList<>();
        }

        return (List<Review>) response.getData();
    }

    // Ραντεβού χρήστη
    @SuppressWarnings("unchecked")
    private Appoinment findUserAppointment(String userID, String appointmentID) {
        Response response = db.sendRequest(new Request(Operation.GET_USER_APPOINTMENTS, userID));

        if (response == null || !response.isSuccess() || response.getData() == null) {
            return null;
        }

        List<Appoinment> appointments = (List<Appoinment>) response.getData();

        for (Appoinment appointment : appointments) {
            if (appointment.getAppointmentID().equals(appointmentID)) {
                return appointment;
            }
        }

        return null;
    }

    // Βοηθητική για ειδοποίηση χρήστη σε αναμονή
    private void notifyFirstOnlineWaitlistedUser(String timeScheduleID) {
        Queue<User> queue = waitlists.get(timeScheduleID);

        if (queue == null) {
            return;
        }

        synchronized (queue) {
            while (!queue.isEmpty()) {
                User next = queue.poll();

                String sessionID = findActiveSessionByUserId(next.getUserID());

                if (sessionID != null) {
                    notifyWaitlistUser(sessionID, "The time schedule is free now. Book it.");
                    break;
                }
            }

            if (queue.isEmpty()) {
                waitlists.remove(timeScheduleID);
            }
        }
    }

    // Βοηθητική για επιστροφή χρήστη απο id
    private String findActiveSessionByUserId(String userID) {
        for (Map.Entry<String, User> entry : activeUsers.entrySet()) {
            if (entry.getValue().getUserID().equals(userID)) {
                return entry.getKey();
            }
        }

        return null;
    }

    // Βοηθητική για ειδοποίηση χρήστη σε αναμονή απο id
    private void notifyOnlineUserByUserId(String userID, boolean cancelledScheduleNotification, String message) {
        String sessionID = findActiveSessionByUserId(userID);

        if (sessionID == null) {
            return;
        }

        if (cancelledScheduleNotification) {
            notifyCancelledSchedule(sessionID, message);
        } else {
            notifyWaitlistUser(sessionID, message);
        }
    }

    // Βοηθητική για ειδοποίηση χρήστη για διαγραφη
    private void notifyCancelledSchedule(String sessionID, String message) {
        try {
            ClientCallback callback = callbacks.get(sessionID);

            if (callback != null) {
                callback.notifyTimeScheduleCancel(message);
            }
        } catch (RemoteException e) {
            callbacks.remove(sessionID);
            activeUsers.remove(sessionID);
        }
    }

    private void notifyWaitlistUser(String sessionID, String message) {
        try {
            ClientCallback callback = callbacks.get(sessionID);

            if (callback != null) {
                callback.notifyWaitlist(message);
            }
        } catch (RemoteException e) {
            callbacks.remove(sessionID);
            activeUsers.remove(sessionID);
        }
    }

    // Αποθέκευση χρηστών σε αρχείο
    private synchronized void saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            out.writeObject(new ArrayList<>(users));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Φόρτωση των χρηστών
    @SuppressWarnings("unchecked")
    private synchronized void loadUsers() {
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();

            if (obj instanceof List<?>) {
                users.clear();
                users.addAll((List<User>) obj);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // H main που τρέχει τον rmi server
    public static void main(String[] args) {
        try {
            CentralAppointmentServer server = new CentralAppointmentServer();
            Registry registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
            Naming.rebind("//localhost/Appointment", server);
            System.out.println("Central Appointment Server started");
        } catch (RemoteException | MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
}