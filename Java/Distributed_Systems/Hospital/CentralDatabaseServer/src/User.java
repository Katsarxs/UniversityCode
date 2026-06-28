import enums.Roles;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name, amka, phone, email, username, password, userID;
    private final Roles role;

    public User(String name, String amka, String phone, String email, String username, String password, Roles role) {
        this.name = name;
        this.amka = amka;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.userID = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public String getAmka() {
        return amka;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Roles getRole() {
        return role;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof User other)) {
            return false;
        }
        return Objects.equals(userID, other.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }
}