import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

// Αποθέκευση και φόρτωση δεδομένων
public class FileManager {
    private static final String DOCTORS_FILE = "doctors.dat";
    private static final String SCHEDULES_FILE = "schedules.dat";
    private static final String APPOINTMENTS_FILE = "appointments.dat";
    private static final String REVIEWS_FILE = "reviews.dat";
    public static synchronized void saveDoctors() {
        saveObject(Database.doctors, DOCTORS_FILE);
    }
    public static synchronized void saveSchedules() {
        saveObject(Database.schedules, SCHEDULES_FILE);
    }
    public static synchronized void saveAppointments() {
        saveObject(Database.appointments, APPOINTMENTS_FILE);
    }
    public static synchronized void saveReviews() {
        saveObject(Database.reviews, REVIEWS_FILE);
    }

    @SuppressWarnings("unchecked")
    public static synchronized void loadDoctors() {
        Object obj = loadObject(DOCTORS_FILE);

        if (obj != null) {
            Database.doctors.clear();
            Database.doctors.addAll((List<Doctor>) obj);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void loadSchedules() {
        Object obj = loadObject(SCHEDULES_FILE);

        if (obj != null) {
            Database.schedules.clear();
            Database.schedules.addAll((List<TimeSchedule>) obj);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void loadAppointments() {
        Object obj = loadObject(APPOINTMENTS_FILE);

        if (obj != null) {
            Database.appointments.clear();
            Database.appointments.addAll((List<Appoinment>) obj);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void loadReviews() {
        Object obj = loadObject(REVIEWS_FILE);

        if (obj != null) {
            Database.reviews.clear();
            Database.reviews.addAll((List<Review>) obj);
        }
    }

    public static synchronized void loadAll() {
        loadDoctors();
        loadSchedules();
        loadAppointments();
        loadReviews();
    }

    private static void saveObject(Object object, String fileName) {
        Path targetPath = Path.of(fileName);
        Path tempPath = Path.of(fileName + ".tmp");

        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempPath.toFile()))) {
                out.writeObject(object);
                out.flush();
            }

            try {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException moveException) {
                System.out.println("Could not replace " + fileName + " using temp file. Trying direct save...");
                moveException.printStackTrace();

                saveObjectDirectly(object, fileName);

                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveObjectDirectly(Object object, String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(object);
            out.flush();
        }
    }

    private static Object loadObject(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        } catch (EOFException e) {
            System.out.println(fileName + " is empty or corrupted. Starting with empty data.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}