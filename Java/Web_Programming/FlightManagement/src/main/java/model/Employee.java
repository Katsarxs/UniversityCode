package model;

import model.enums.AccountState;
import model.enums.Role;

public class Employee extends User {
    private String employeeCode;

    public Employee() {
        super();
    }

    public Employee(String username, String email, String password, String fullname, String idNumber, AccountState accountState, Role role, String employeeCode) {
        super(username, email, password, fullname, idNumber, accountState, role);
        this.employeeCode = employeeCode;
    }

    public Employee(int id, String username, String email, String password, String fullname, String idNumber, AccountState accountState, Role role, String employeeCode) {
        super(id, username, email, password, fullname, idNumber, accountState, role);
        this.employeeCode = employeeCode;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
}
