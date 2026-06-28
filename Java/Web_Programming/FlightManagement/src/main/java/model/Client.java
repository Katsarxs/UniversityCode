package model;

import model.enums.AccountState;
import model.enums.Role;

public class Client extends User {
    private String afm, homeAddress;

    public Client() {
        super();
    }

    public Client(String username, String email, String password, String fullname, String idNumber, AccountState accountState, Role role, String afm, String homeAddress) {
        super(username, email, password, fullname, idNumber, accountState, role);
        this.afm = afm;
        this.homeAddress = homeAddress;
    }

    public Client(int id, String username, String email, String password, String fullname, String idNumber, AccountState accountState, Role role, String afm, String homeAddress) {
        super(id, username, email, password, fullname, idNumber, accountState, role);
        this.afm = afm;
        this.homeAddress = homeAddress;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
