package dao;

import model.User;
import model.Client;
import model.Employee;
import model.enums.Role;
import model.enums.AccountState;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Εγγραφή νέου χρήστη
    public boolean registerClient(Client client) {
        if (!checkUser(client.getUsername(), client.getIdNumber())) {
            return false;
        }

        String clientSQL = "INSERT INTO client (user_id, afm, home_address) VALUES (?, ?, ?)";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            int id = insertUser(connection, client);
            try (PreparedStatement preparedStatement = connection.prepareStatement(clientSQL)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, client.getAfm());
                preparedStatement.setString(3, client.getHomeAddress());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception e2) {
                    System.out.println("Σφάλμα : " + e2.getMessage());
                }
            }
            System.out.println("Error : " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("Σφάλμα : " + e.getMessage());
                }
            }
        }
    }

    // Εγγραφή νέου χρήστη υπαλλήλου
    public boolean registerEmployee(Employee employee) {
        if (!checkUser(employee.getUsername(), employee.getIdNumber())) {
            return false;
        }

        String employeeSQL = "INSERT INTO employee (user_id, employee_code) VALUES (?, ?)";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            int id = insertUser(connection, employee);
            try (PreparedStatement preparedStatement = connection.prepareStatement(employeeSQL)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, employee.getEmployeeCode());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception e2) {
                    System.out.println("Σφάλμα : " + e2.getMessage());
                }
            }
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("Σφάλμα : " + e.getMessage());
                }
            }
        }
    }

    public void checkSystemManager() {
        String checkSQL = "SELECT id FROM user WHERE role = 'SYSTEM_MANAGER'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (!resultSet.next()) {
                Employee administrator = new Employee("admin", "admin@admin.com", "123", "Νίκος Κατσαρός", "3212023075", AccountState.ACTIVE, Role.SYSTEM_MANAGER, "EMP-001");
                registerEmployee(administrator);
                System.out.println("Μπήκε διαχειριστής για το σύστημα με username: admin και password: 123");
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
    }

    // Εισαγωγή πολλαπλών χρηστών
    public int importUsers(List<User> usersList) {
        int imported = 0;
        for (User user : usersList) {
            boolean success = false;
            if (user instanceof Client) {
                success = registerClient((Client) user);
            } else if (user instanceof Employee) {
                success = registerEmployee((Employee) user);
            }

            if (success) {
                imported++;
            } else {
                System.out.println("Διπλότυπη εγγραφή αγνοήθηκε, ο " + user.getUsername() + " υπάρχει");
            }
        }
        return imported;
    }

    // Εμφάνιση χρήστη
    public User getUser(int id) {
        String selectUserSQL = "SELECT u.*, c.afm, c.home_address, e.employee_code " + "FROM user u " + "LEFT JOIN client c ON u.id = c.user_id " + "LEFT JOIN employee e ON u.id = e.user_id " + "WHERE u.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = Role.valueOf(resultSet.getString("role"));
                    AccountState accountState = AccountState.valueOf(resultSet.getString("account_state"));
                    if (role == Role.CLIENT) {
                        return new Client(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("fullname"), resultSet.getString("id_number"), accountState, role, resultSet.getString("afm"), resultSet.getString("home_address"));
                    } else {
                        return new Employee(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("fullname"), resultSet.getString("id_number"), accountState, role, resultSet.getString("employee_code"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return null;
    }

    // Ενημέρωση χρήστη
    public boolean updateUser(User user) {
        boolean changedPassword = user.getPassword() != null && !user.getPassword().isEmpty();
        String updateUserSQL;
        if (changedPassword) {
            updateUserSQL = "UPDATE user SET email = ?, fullname = ?, id_number = ?, account_state = ?, password = ? WHERE id = ?";
        } else {
            updateUserSQL = "UPDATE user SET email = ?, fullname = ?, id_number = ?, account_state = ? WHERE id = ?";
        }

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL)) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getFullname());
                preparedStatement.setString(3, user.getIdNumber());
                preparedStatement.setString(4, user.getAccountState().name());

                if (changedPassword) {
                    preparedStatement.setString(5, user.getPassword());
                    preparedStatement.setInt(6, user.getId());
                } else {
                    preparedStatement.setInt(5, user.getId());
                }

                preparedStatement.executeUpdate();
            }

            if (user instanceof Client client) {
                String updateClientSQL = "UPDATE client SET afm = ?, home_address = ? WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateClientSQL)) {
                    preparedStatement.setString(1, client.getAfm());
                    preparedStatement.setString(2, client.getHomeAddress());
                    preparedStatement.setInt(3, client.getId());
                    preparedStatement.executeUpdate();
                }
            } else if (user instanceof Employee employee) {
                String updateEmployeeSQL = "UPDATE employee SET employee_code = ? WHERE user_id = ?";
                try (PreparedStatement preparedStatementEmployee = connection.prepareStatement(updateEmployeeSQL)) {
                    preparedStatementEmployee.setString(1, employee.getEmployeeCode());
                    preparedStatementEmployee.setInt(2, employee.getId());
                    preparedStatementEmployee.executeUpdate();
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception e2) {
                    System.out.println("Σφάλμα : " + e2.getMessage());
                }
            }
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("Σφάλμα : " + e.getMessage());
                }
            }
        }
    }

    // Αναζήτηση χρήστη
    public List<User> searchUsers(String query) {
        List<User> usersList = new ArrayList<>();
        String searchUsersSQL = "SELECT u.*, c.afm, c.home_address, e.employee_code " + "FROM user u " + "LEFT JOIN client c ON u.id = c.user_id " + "LEFT JOIN employee e ON u.id = e.user_id " + "WHERE u.username LIKE ? " + "OR u.fullname LIKE ? " + "OR u.id_number LIKE ? " + "OR c.afm LIKE ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(searchUsersSQL)) {
            String formattedSearchParam = "%" + query + "%";
            preparedStatement.setString(1, formattedSearchParam);
            preparedStatement.setString(2, formattedSearchParam);
            preparedStatement.setString(3, formattedSearchParam);
            preparedStatement.setString(4, formattedSearchParam);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Role role = Role.valueOf(resultSet.getString("role"));
                    AccountState accountState = AccountState.valueOf(resultSet.getString("account_state"));
                    if (role == Role.CLIENT) {
                        usersList.add(new Client(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), null, resultSet.getString("fullname"), resultSet.getString("id_number"), accountState, role, resultSet.getString("afm"), resultSet.getString("home_address")));
                    } else {
                        usersList.add(new Employee(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), null, resultSet.getString("fullname"), resultSet.getString("id_number"), accountState, role, resultSet.getString("employee_code")));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return usersList;
    }

    // Διαγραφή χρήστη
    public boolean deleteUser(int id) {
        String deleteUserSQL = "DELETE FROM user WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }

    // Αυθεντικοποίηση χρήστη
    public User authentivateUser(String username, String password) {
        String loginSQL = "SELECT u.*, c.afm, c.home_address, e.employee_code " + "FROM user u " + "LEFT JOIN client c ON u.id = c.user_id " + "LEFT JOIN employee e ON u.id = e.user_id " + "WHERE u.username = ? AND u.password = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(loginSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = Role.valueOf(resultSet.getString("role"));
                    AccountState accountState = AccountState.valueOf(resultSet.getString("account_state"));
                    if (role == Role.CLIENT) {
                        return new Client(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("fullname"), resultSet.getString("id_number"), accountState, role, resultSet.getString("afm"), resultSet.getString("home_address"));
                    } else {
                        return new Employee(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("fullname"), resultSet.getString("id_number"), accountState, role, resultSet.getString("employee_code"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return null;
    }

    // Τσεκάρει διπλότυπα
    public boolean checkValue(String tableName, String columnName, String idColumnName, String value, int currentUserId) {
        String checkSQL = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ? AND " + idColumnName + " != ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkSQL)) {
            preparedStatement.setString(1, value);
            preparedStatement.setInt(2, currentUserId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return false;
    }

    // Προσθήκη χρήστη και επιστρέφει user id
    private int insertUser(Connection connection, User user) throws Exception {
        String userInsertSQL = "INSERT INTO user (username, email, password, fullname, id_number, account_state, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatementUser = connection.prepareStatement(userInsertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatementUser.setString(1, user.getUsername());
            preparedStatementUser.setString(2, user.getEmail());
            preparedStatementUser.setString(3, user.getPassword());
            preparedStatementUser.setString(4, user.getFullname());
            preparedStatementUser.setString(5, user.getIdNumber());
            preparedStatementUser.setString(6, user.getAccountState().name());
            preparedStatementUser.setString(7, user.getRole().name());
            preparedStatementUser.executeUpdate();
            try (ResultSet resultSetKeys = preparedStatementUser.getGeneratedKeys()) {
                if (resultSetKeys.next()) {
                    int id = resultSetKeys.getInt(1);
                    user.setId(id);
                    return id;
                } else {
                    throw new Exception("Failed to retrieve auto-incremented user ID.");
                }
            }
        }
    }

    // Τσεκάρει αν υπάρχει ο χρήστης
    private boolean checkUser(String username, String idNumber) {
        String querySQL = "SELECT id FROM user WHERE username = ? OR id_number = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, idNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return !resultSet.next();
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }
}
