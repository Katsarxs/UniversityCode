package model;

import model.enums.AccountState;
import model.enums.Role;

public class User {
    private int id;
    private String username, email, password, fullname, idNumber;
    private AccountState accountState;
    private Role role;

    public User() {}

    public User(String username, String email, String password, String fullname, String idNumber, AccountState accountState, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.idNumber = idNumber;
        this.accountState = accountState;
        this.role = role;
    }

    public User(int id, String username, String email, String password, String fullname, String idNumber, AccountState accountState, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.idNumber = idNumber;
        this.accountState = accountState;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public Role getRole() {
        return role;
    }
}
