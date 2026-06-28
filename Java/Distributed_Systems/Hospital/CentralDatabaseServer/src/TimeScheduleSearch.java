import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class TimeScheduleSearch implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String profession, name, department;
    private LocalDate dateFrom, dateTo;
    private double cost;

    public TimeScheduleSearch() {}

    public TimeScheduleSearch(String profession, String name, LocalDate dateFrom, LocalDate dateTo, String department, double cost) {
        this.profession = profession;
        this.name = name;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.department = department;
        this.cost = cost;
    }

    public String getProfession() {
        return profession;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public String getDepartment() {
        return department;
    }

    public double getCost() {
        return cost;
    }
}
