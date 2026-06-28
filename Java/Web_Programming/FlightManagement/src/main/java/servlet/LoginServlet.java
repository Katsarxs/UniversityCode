package servlet;

import dao.UserDAO;
import model.User;
import model.enums.AccountState;

import java.io.IOException;
import java.io.Serial;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.owasp.encoder.Encode;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    // Τρέχει για αρχικοποίηση
    @Override
    public void init() {
        this.userDAO = new UserDAO();
        userDAO.checkSystemManager();
    }

    // Χειρίζεται τις ενέργειες HTTP GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Γίνεται κλήση HTTP GET του LoginServlet. Μόλις το πάρει το servlet, βλέπει αν έχει action ή όχι.
         *  Αν όχι, ανακατευθύνεται στη σελίδα σύνδεσης login.jsp. Αλλίως, αν το action είναι logout,
         *  καταστρέφει το session ID και τον πάει στη βασική σελίδα index.jsp*/
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            response.sendRedirect("index.jsp?msg=logged_out");
            return;
        }

        response.sendRedirect("login.jsp");
    }

    // Χειρίζεται τις ενέργειες HTTP POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Γίνεται κλήση HTTP POST του LoginServlet. Γίνεται βασικός έλεγχος των πεδίων, αν είναι σωστά
         *  και αν ο λογαρισμός είναι ενεργοποιημένος. Αν όλα καλά, φτιάνχει session ID για τον χρήστη
         *  και τον ανατατευθύνει στο dashboard.jsp. Αν κάτι πάει λάθος, βγάζει και το αντίστοιχο μήνυμα
         *  λάθους.*/
        request.setCharacterEncoding("UTF-8");
        String username = sanitize(request.getParameter("username"));
        String password = sanitize(request.getParameter("password"));
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Συμπλήρωσε όλα τα πεδία.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.authentivateUser(username, password);
            if (user != null) {
                if (user.getAccountState() == AccountState.DISABLED) {
                    request.setAttribute("errorMessage", "Ο λογαριασμός σου είναι απενεργοποιημένος.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                }

                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                response.sendRedirect("dashboard.jsp");
            } else {
                request.setAttribute("errorMessage", "Λανθασμένο όνομα χρήστη ή κωδικός πρόσβασης.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            request.setAttribute("errorMessage", "Έγινε το εξής σφάλμα κατά την σύνδεση : " + e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Encode.forHtml(input.trim());
    }
}
