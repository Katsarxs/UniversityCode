import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Doctor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name, profession, department, phone, email, doctorID;
    private final double baseCost;

    public Doctor(String name, String profession, String department, String phone, String email, double baseCost) {
        this.name = name;
        this.profession = profession;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.baseCost = baseCost;
        this.doctorID = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public String getDoctorID() {
        return doctorID;
    }

    @Override
    public String toString() {
        return name + " - " + profession;
    }
}