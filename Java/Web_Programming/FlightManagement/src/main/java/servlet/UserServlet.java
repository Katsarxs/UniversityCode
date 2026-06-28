package servlet;

import dao.UserDAO;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import model.User;
import model.Client;
import model.Employee;
import model.enums.Role;
import model.enums.AccountState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import org.owasp.encoder.Encode;

@WebServlet("/UserServlet")
@MultipartConfig
public class UserServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    // Τρέχει για αρχικοποίηση
    @Override
    public void init() {
        this.userDAO = new UserDAO();
    }

    // Χειρίζεται τις ενέργειες HTTP GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Υπάρχουν 4 HTTP GET actions. profile όπου δείχνει τα στοιχεία του χρήστη,
         * search χρησιμοποιείται στην αναζήτηση του χρήστη, delete διαγραφή του χρήστη,
         * και editForm για να επεξεργαστεί από τον διαχειριστή συστήματος*/
        HttpSession httpSession = request.getSession(false);
        User loggedUser = (httpSession != null) ? (User) httpSession.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "dashboard";
        }

        try {
            switch (action) {
                case "profile":
                    showUserProfile(request, response, loggedUser);
                    break;
                case "search":
                    searchUsers(request, response, loggedUser);
                    break;
                case "delete":
                    deleteUser(request, response, loggedUser);
                    break;
                case "editForm":
                    showEditForm(request, response, loggedUser);
                    break;
                default:
                    response.sendRedirect("dashboard.jsp");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Χειρίζεται τις ενέργειες HTTP GET
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Υπάρχουν 3 HTTP POST actions. register γίνεται εγγραφή χρήστη,
         * update ενημέρωση χρήστη, import εισαγωγή πολλαπλών χρηστών.*/
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("register".equals(action)) {
                registerUser(request, response);
            } else if ("update".equals(action)) {
                updateUser(request, response);
            } else if ("import".equals(action)) {
                addFileUsers(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Προβολή στοιχείων του χρήστη
    private void showUserProfile(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        User userToDisplay;
        if (idParam == null || idParam.isEmpty()) {
            userToDisplay = userDAO.authentivateUser(loggedUser.getUsername(), loggedUser.getPassword());
        } else {
            int userIdToView = Integer.parseInt(idParam);
            if (loggedUser.getId() != userIdToView && loggedUser.getRole() != Role.SYSTEM_MANAGER) {
                request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα να δεις τα στοιχεία αυτού του χρήστη.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            userToDisplay = userDAO.getUser(userIdToView);
        }

        if (userToDisplay == null) {
            request.setAttribute("errorMessage", "Ο χρήστης δεν βρέθηκε στο σύστημα.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setAttribute("displayedUser", userToDisplay);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }


    // Εγγραφή χρήστη
    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = sanitize(request.getParameter("username"));
        String email = sanitize(request.getParameter("email"));
        String password = sanitize(request.getParameter("password"));
        String fullname = sanitize(request.getParameter("fullname"));
        String idNumber = sanitize(request.getParameter("id_number"));
        String roleString = request.getParameter("role");
        Role role = Role.valueOf(roleString);
        User newUser;
        if (role == Role.CLIENT) {
            String afm = sanitize(request.getParameter("afm"));
            String homeAddress = sanitize(request.getParameter("home_address"));
            newUser = new Client(0, username, email, password, fullname, idNumber, AccountState.ACTIVE, role, afm, homeAddress);
        } else {
            String employeeCode = sanitize(request.getParameter("employee_code"));
            newUser = new Employee(0, username, email, password, fullname, idNumber, AccountState.ACTIVE, role, employeeCode);
        }

        boolean success;
        if (newUser instanceof Client) {
            success = userDAO.registerClient((Client) newUser);
        } else {
            success = userDAO.registerEmployee((Employee) newUser);
        }

        if (success) {
            HttpSession session = request.getSession(false);
            User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
            if (loggedUser != null && loggedUser.getRole() == Role.SYSTEM_MANAGER) {
                response.sendRedirect("UserServlet?action=search&query=&msg=add_success");
            } else {
                response.sendRedirect("index.jsp?msg=success");
            }
        } else {
            request.setAttribute("errorMessage", "Η εγγραφή απέτυχε.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    // Αναζήτηση χρηστών
    private void searchUsers(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws ServletException, IOException {
        if (loggedUser.getRole() != Role.SYSTEM_MANAGER) {
            request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα πρόσβασης σε αυτή τη λειτουργία.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        String query = sanitize(request.getParameter("query"));
        List<User> results = userDAO.searchUsers(query);
        request.setAttribute("usersList", results);
        request.getRequestDispatcher("userManagement.jsp").forward(request, response);
    }

    // Διαγραφή χρήστη
    private void deleteUser(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int targetId = Integer.parseInt(request.getParameter("id"));
        if (loggedUser.getRole() != Role.SYSTEM_MANAGER && loggedUser.getId() != targetId) {
            request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα διαγραφής αυτού του λογαριασμού.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        if (userDAO.deleteUser(targetId)) {
            if (targetId == loggedUser.getId()) {
                request.getSession().invalidate();
                response.sendRedirect("login.jsp?msg=account_deleted");
            } else {
                response.sendRedirect("UserServlet?action=search&query=&msg=deletesuccess");
            }
        } else {
            request.setAttribute("errorMessage", "Η διαγραφή απέτυχε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Εμφάνιση φόρμα επεξεργασίας χρήστη
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws ServletException, IOException {
        int targetId = Integer.parseInt(request.getParameter("id"));
        if (loggedUser.getRole() != Role.SYSTEM_MANAGER && loggedUser.getId() != targetId) {
            request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα επεξεργασίας αυτού του προφίλ.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        User userToEdit = userDAO.getUser(targetId);
        request.setAttribute("userToEdit", userToEdit);
        request.getRequestDispatcher("editUser.jsp").forward(request, response);
    }

    // Ενημέρωση Χρήστη
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        String email = sanitize(request.getParameter("email"));
        String password = sanitize(request.getParameter("password"));
        String fullname = sanitize(request.getParameter("fullname"));
        String idNumber = sanitize(request.getParameter("id_number"));
        String afm = sanitize(request.getParameter("afm"));
        String homeAddress = sanitize(request.getParameter("home_address"));
        String employeeCode = sanitize(request.getParameter("employee_code"));
        String stateString = request.getParameter("account_state");
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        User existingUser = userDAO.getUser(id);
        if (existingUser == null) {
            response.sendRedirect("UserServlet?action=search");
            return;
        }

        if (!email.equals(existingUser.getEmail()) && userDAO.checkValue("user", "email", "id", email, id)) {
            request.setAttribute("errorMessage", "Το email '" + email + "' χρησιμοποιείται ήδη από άλλον λογαριασμό.");
            if (loggedUser != null && loggedUser.getId() == id) {
                request.setAttribute("displayedUser", existingUser);
                request.getRequestDispatcher("profile.jsp").forward(request, response);
            } else {
                request.setAttribute("userToEdit", existingUser);
                request.getRequestDispatcher("editUser.jsp").forward(request, response);
            }
            return;
        }

        if (idNumber != null && !idNumber.equals(existingUser.getIdNumber()) && userDAO.checkValue("user", "id_number", "id", idNumber, id)) {
            request.setAttribute("errorMessage", "Ο αριθμός ταυτότητας '" + idNumber + "' χρησιμοποιείται ήδη.");
            if (loggedUser != null && loggedUser.getId() == id) {
                request.setAttribute("displayedUser", existingUser);
                request.getRequestDispatcher("profile.jsp").forward(request, response);
            } else {
                request.setAttribute("userToEdit", existingUser);
                request.getRequestDispatcher("editUser.jsp").forward(request, response);
            }
            return;
        }

        if (existingUser instanceof Client client) {
            if (afm != null && !afm.equals(client.getAfm()) && userDAO.checkValue("client", "afm", "user_id", afm, id)) {
                request.setAttribute("errorMessage", "Το ΑΦΜ '" + afm + "' είναι ήδη καταχωρημένο σε άλλον πελάτη.");
                if (loggedUser != null && loggedUser.getId() == id) {
                    request.setAttribute("displayedUser", existingUser);
                    request.getRequestDispatcher("profile.jsp").forward(request, response);
                } else {
                    request.setAttribute("userToEdit", existingUser);
                    request.getRequestDispatcher("editUser.jsp").forward(request, response);
                }
                return;
            }
        }

        existingUser.setEmail(email);
        if (password != null && !password.trim().isEmpty()) {
            existingUser.setPassword(password.trim());
        }
        existingUser.setFullname(fullname);
        existingUser.setIdNumber(idNumber);

        if (stateString != null) {
            existingUser.setAccountState(AccountState.valueOf(stateString));
        }

        if (existingUser instanceof Client client) {
            client.setAfm(afm);
            client.setHomeAddress(homeAddress);
        } else if (existingUser instanceof Employee employee) {
            employee.setEmployeeCode(employeeCode);
        }

        if (userDAO.updateUser(existingUser)) {
            if (loggedUser != null && loggedUser.getId() == id) {
                session.setAttribute("user", existingUser);
                response.sendRedirect("UserServlet?action=profile&id=" + id + "&msg=update_success");
            } else {
                response.sendRedirect("UserServlet?action=editForm&id=" + id + "&msg=updated");
            }
        } else {
            request.setAttribute("userToEdit", existingUser);
            request.setAttribute("errorMessage", "Η ενημέρωση απέτυχε.");
            request.getRequestDispatcher("editUser.jsp").forward(request, response);
        }
    }

    // Εισαγωγή πολλαπλών χρηστών από csv αρχείο
    private void addFileUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null || loggedUser.getRole() != Role.SYSTEM_MANAGER) {
            response.sendRedirect("index.jsp");
            return;
        }

        Part filePart = request.getPart("csvFile");
        List<User> usersList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) {
                    continue;
                }

                String username = sanitize(data[0]);
                String email = sanitize(data[1]);
                String password = data[2] != null ? data[2].trim() : "";
                String fullname = sanitize(data[3]);
                String idNumber = sanitize(data[4]);
                Role role = Role.valueOf(data[5].trim());
                if (role == Role.CLIENT) {
                    String afm = sanitize(data[6]);
                    String address = data.length > 7 ? sanitize(data[7]) : "";
                    usersList.add(new Client(0, username, email, password, fullname, idNumber, AccountState.ACTIVE, role, afm, address));
                } else {
                    String empCode = sanitize(data[6]);
                    usersList.add(new Employee(0, username, email, password, fullname, idNumber, AccountState.ACTIVE, role, empCode));
                }
            }
        }
        int importedCount = userDAO.importUsers(usersList);
        response.sendRedirect("UserServlet?action=search&query=&msg=imported_" + importedCount);
    }

    // sanitize για αποφυγή xss επίθεσης
    private String sanitize(String input) {
        if (input == null) return null;
        return Encode.forHtml(input.trim());
    }
}
