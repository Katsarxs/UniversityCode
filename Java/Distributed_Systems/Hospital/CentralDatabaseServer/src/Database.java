import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// Λίστες για τα δεδομένα προς αποθήκευση
public class Database {

    public static final List<Doctor> doctors = Collections.synchronizedList(new ArrayList<>());
    public static final List<TimeSchedule> schedules = Collections.synchronizedList(new ArrayList<>());
    public static final List<Appoinment> appointments = Collections.synchronizedList(new ArrayList<>());
    public static final List<Review> reviews = Collections.synchronizedList(new ArrayList<>());
}