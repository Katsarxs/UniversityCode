package servlet;

import dao.BookingDAO;
import dao.FlightDAO;
import model.Booking;
import model.Flight;
import model.User;
import model.enums.Role;
import model.enums.FlightState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import org.owasp.encoder.Encode;

@WebServlet("/FlightServlet")
@MultipartConfig
public class FlightServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private FlightDAO flightDAO;

    // Τρέχει για αρχικοποίηση
    @Override
    public void init() {
        this.flightDAO = new FlightDAO();
    }

    // Χειρίζεται τις ενέργειες HTTP GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Υπάρχουν 4 HTTP GET actions. view όπου δείχνει τα στοιχεία της πτήσης,
         * search χρησιμοποιείται στην αναζήτηση της πτήσης, delete διαγραφή της πτήσης,
         * και editForm για να επεξεργασία πτήσης*/
        HttpSession httpSession = request.getSession(false);
        User loggedUser = (httpSession != null) ? (User) httpSession.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "search";
        }

        try {
            switch (action) {
                case "search":
                    searchFlights(request, response);
                    break;
                case "delete":
                    deleteFlight(request, response, loggedUser);
                    break;
                case "editForm":
                    showEditForm(request, response, loggedUser);
                    break;
                case "view":
                    viewFlight(request, response);
                    break;
                default:
                    response.sendRedirect("dashboard.jsp");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            throw new ServletException(e);
        }
    }

    // Χειρίζεται τις ενέργειες HTTP POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Υπάρχουν 3 HTTP POST actions. insert είναι η εισαγωγή πτήσης,
         * update χρησιμοποιείται για την επεξεργασία πτήσης, import είναι
         * για την εισαγωγή πολλαπλών πτήσεων από τον διαχειριστή πτήσεων*/
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("insert".equals(action)) {
                insertFlight(request, response, request.getSession(false));
            } else if ("update".equals(action)) {
                updateFlight(request, response, request.getSession(false));
            } else if ("import".equals(action)) {
                addFileFlights(request, response);
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            throw new ServletException(e);
        }
    }

    // Εισαγωγή νέας πτήσης
    private void insertFlight(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null || loggedUser.getRole() != Role.FLIGHT_MANAGER) {
            request.setAttribute("errorMessage", "Δεν έχετε δικαίωμα δημιουργίας πτήσεων.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        String flightNumber = sanitize(request.getParameter("flight_number"));
        String airplane = sanitize(request.getParameter("airplane"));
        LocalDate departureDate = LocalDate.parse(request.getParameter("departure_date"));
        LocalTime departureTime = LocalTime.parse(request.getParameter("departure_time"));
        int seats = Integer.parseInt(request.getParameter("seats"));
        int rows = Integer.parseInt(request.getParameter("rows"));
        int seatsRow = Integer.parseInt(request.getParameter("seats_row"));
        int rowsBusiness = Integer.parseInt(request.getParameter("rows_business"));
        Flight flight = new Flight(flightNumber, airplane, departureDate, departureTime, seats, rows, seatsRow, rowsBusiness, FlightState.CREATED);
        if (flightDAO.insertFlight(flight)) {
            response.sendRedirect("FlightServlet?action=search&msg=insert_success");
        } else {
            request.setAttribute("errorMessage", "Η εισαγωγή απέτυχε.");
            request.getRequestDispatcher("addFlight.jsp").forward(request, response);
        }
    }

    // Δυναμική αναζήτηση πτήσεων
    private void searchFlights(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        flightDAO.updateFlightStates();
        String flightNumber = sanitize(request.getParameter("flight_number"));
        String airplane = sanitize(request.getParameter("airplane"));
        String dateParam = request.getParameter("departure_date");
        LocalDate departureDate = (dateParam != null && !dateParam.isEmpty()) ? LocalDate.parse(dateParam) : null;
        String stateParam = request.getParameter("state");
        FlightState state = (stateParam != null && !stateParam.isEmpty()) ? FlightState.valueOf(stateParam) : null;
        List<Flight> results = flightDAO.searchFlights(flightNumber, airplane, departureDate, state);
        request.setAttribute("flightsList", results);
        request.getRequestDispatcher("flightManagement.jsp").forward(request, response);
    }

    // Προβολή λεπτομερειών πτήσης
    private void viewFlight(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Flight flight = flightDAO.getFlight(id);
        if (flight == null) {
            request.setAttribute("errorMessage", "Η πτήση δεν βρέθηκε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        BookingDAO bookingDAO = new dao.BookingDAO();
        List<Booking> occupiedSeats = bookingDAO.getOccupiedSeats(id);
        request.setAttribute("flight", flight);
        request.setAttribute("occupiedSeats", occupiedSeats);
        request.getRequestDispatcher("viewFlight.jsp").forward(request, response);
    }

    // Εμφάνιση φόρμα επεξεργασίας πτήσης
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws ServletException, IOException {
        if (loggedUser.getRole() != Role.FLIGHT_MANAGER) {
            request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα επεξεργασίας πτήσεων.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        Flight flightToEdit = flightDAO.getFlight(id);
        request.setAttribute("flightToEdit", flightToEdit);
        request.getRequestDispatcher("editFlight.jsp").forward(request, response);
    }

    // Ενημέρωση πτήσης
    private void updateFlight(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null || loggedUser.getRole() != Role.FLIGHT_MANAGER) {
            request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα τροποποίησης πτήσεων.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        Flight existingFlight = flightDAO.getFlight(id);
        existingFlight.setAirplane(sanitize(request.getParameter("airplane")));
        existingFlight.setDepartureDate(LocalDate.parse(request.getParameter("departure_date")));
        existingFlight.setDepartureTime(LocalTime.parse(request.getParameter("departure_time")));
        existingFlight.setSeats(Integer.parseInt(request.getParameter("seats")));
        existingFlight.setRows(Integer.parseInt(request.getParameter("rows")));
        existingFlight.setSeatsRow(Integer.parseInt(request.getParameter("seats_row")));
        existingFlight.setRowsBusiness(Integer.parseInt(request.getParameter("rows_business")));
        existingFlight.setState(FlightState.valueOf(request.getParameter("state")));
        if (flightDAO.updateFlight(existingFlight)) {
            response.sendRedirect("FlightServlet?action=view&id=" + id + "&msg=update_success");
        } else {
            Flight originalFlight = flightDAO.getFlight(id);
            request.setAttribute("flight", originalFlight);
            request.setAttribute("errorMessage", "Η ενημέρωση απέτυχε. Όχι έγκυρη μετάβαση κατάστασης.");
            BookingDAO bookingDAO = new dao.BookingDAO();
            request.setAttribute("occupiedSeats", bookingDAO.getOccupiedSeats(id));
            request.getRequestDispatcher("viewFlight.jsp").forward(request, response);
        }
    }

    // Διαγραφή πτήσης
    private void deleteFlight(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        if (loggedUser.getRole() != Role.FLIGHT_MANAGER) {
            request.setAttribute("errorMessage", "Δεν έχεις δικαίωμα διαγραφής πτήσεων.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        if (flightDAO.deleteFlight(id)) {
            response.sendRedirect("FlightServlet?action=search&msg=delete_success");
        } else {
            request.setAttribute("errorMessage", "Η διαγραφή της πτήσης απέτυχε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Εισαγωγή πολλαπλών πτήσεων
    private void addFileFlights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null || loggedUser.getRole() != Role.FLIGHT_MANAGER) {
            response.sendRedirect("index.jsp");
            return;
        }

        Part filePart = request.getPart("csvFile");
        List<Flight> flightsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 8) {
                    continue;
                }

                String flightNumber = sanitize(data[0]);
                String airplane = sanitize(data[1]);
                LocalDate departureDate = LocalDate.parse(data[2].trim());
                LocalTime departureTime = LocalTime.parse(data[3].trim());
                int seats = Integer.parseInt(data[4].trim());
                int rows = Integer.parseInt(data[5].trim());
                int seatsRow = Integer.parseInt(data[6].trim());
                int rowsBusiness = Integer.parseInt(data[7].trim());
                flightsList.add(new Flight(flightNumber, airplane, departureDate, departureTime, seats, rows, seatsRow, rowsBusiness, FlightState.CREATED));
            }
        }
        int importedCount = flightDAO.importFlights(flightsList);
        response.sendRedirect("FlightServlet?action=search&msg=imported_" + importedCount);
    }

    // sanitize για αποφυγή xss επίθεσης
    private String sanitize(String input) {
        if (input == null) return null;
        return Encode.forHtml(input.trim());
    }
}