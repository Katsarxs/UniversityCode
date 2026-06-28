package servlet;

import com.google.gson.JsonObject;
import dao.BookingDAO;
import dao.FlightDAO;
import model.Booking;
import model.Flight;
import model.User;
import model.enums.Role;
import model.enums.BookingState;
import model.enums.TicketType;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.owasp.encoder.Encode;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;

    // Τρέχει για αρχικοποίηση
    @Override
    public void init() {
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
    }

    // Χειρίζεται τις ενέργειες HTTP GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Υπάρχουν 4 HTTP GET actions. view όπου δείχνει τα στοιχεία του booking,
         * search χρησιμοποιείται στην αναζήτηση του booking, cancel ακύρωση του booking,
         * και unreserveSeat αποδέσμευση θέσης για το booking*/
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
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
                    searchBookings(request, response, loggedUser);
                    break;
                case "view": // bookingManagement,
                    viewBooking(request, response, loggedUser);
                    break;
                case "cancel":
                    cancelBooking(request, response, loggedUser);
                    break;
                case "unreserveSeat":
                    unreserveSeat(request, response, loggedUser);
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
        /* Υπάρχουν 4 HTTP POST actions. insert εισαγωγή νέου booking,
         * updateType αλλαγή τύπου του booking, reserveSeat δέσμευση θέσης
         * για το booking, και changeSeat για αλλαγή θέσης*/
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("insert".equals(action)) {
                insertBooking(request, response, loggedUser);
            } else if ("updateType".equals(action)) {
                updateBookingType(request, response, loggedUser);
            } else if ("reserveSeat".equals(action)) {
                reserveSeat(request, response, loggedUser);
            } else if ("changeSeat".equals(action)) {
                changeSeat(request, response, loggedUser);
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            throw new ServletException(e);
        }
    }

    // Δυναμική αναζήτηση κρατήσεων
    private void searchBookings(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws ServletException, IOException {
        String roleString = loggedUser.getRole().name();
        int userId = loggedUser.getId();
        String startParam = request.getParameter("startDate");
        LocalDate startDate = (startParam != null && !startParam.isEmpty()) ? LocalDate.parse(startParam) : null;
        String endParam = request.getParameter("endDate");
        LocalDate endDate = (endParam != null && !endParam.isEmpty()) ? LocalDate.parse(endParam) : null;
        String fullname = sanitize(request.getParameter("fullname"));
        String afm = sanitize(request.getParameter("afm"));
        String idNumber = sanitize(request.getParameter("id_number"));
        String bookingNumber = sanitize(request.getParameter("bookingNumber"));
        String stateParam = request.getParameter("state");
        BookingState bookingState = (stateParam != null && !stateParam.isEmpty()) ? BookingState.valueOf(stateParam) : null;
        List<Booking> results = bookingDAO.searchBookings(roleString, userId, startDate, endDate, fullname, afm, idNumber, bookingNumber, bookingState);
        request.setAttribute("bookingsList", results);
        request.getRequestDispatcher("bookingManagement.jsp").forward(request, response);
    }

    // Εισαγωγή νέας κράτησης
    private void insertBooking(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int flightId = Integer.parseInt(request.getParameter("flight_id"));
        TicketType ticketType = TicketType.valueOf(request.getParameter("ticket_type"));
        int clientId = (loggedUser.getRole() == Role.CLIENT) ? loggedUser.getId() : Integer.parseInt(request.getParameter("client_id"));
        List<Booking> clientBookings = bookingDAO.searchBookings("CLIENT", clientId, null, null, null, null, null, null, null);
        for (Booking b : clientBookings) {
            if (b.getFlightId() == flightId && b.getState() != BookingState.CANCELLED) {
                request.setAttribute("errorMessage", "Δεν επιτρέπεται η διπλή κράτηση.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
        }

        Flight flight = flightDAO.getFlight(flightId);
        if (flight == null) {
            request.setAttribute("errorMessage", "Η πτήση δεν βρέθηκε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        Integer row;
        Integer column;
        if (ticketType == TicketType.ECONOMY) {
            List<Booking> occupiedSeats = bookingDAO.getOccupiedSeats(flightId);
            int startRow = flight.getRowsBusiness() + 1;
            int endRow = flight.getRows();
            int seatsPerRow = flight.getSeatsRow();
            List<int[]> availableEconomySeats = new ArrayList<>();
            for (int r = startRow; r <= endRow; r++) {
                for (int c = 1; c <= seatsPerRow; c++) {
                    boolean isTaken = false;
                    for (Booking occ : occupiedSeats) {
                        if (occ.getFlightId() == flightId && occ.getRow() != null && occ.getColumn() != null
                                && occ.getRow() == r && occ.getColumn() == c && occ.getState() != BookingState.CANCELLED) {
                            isTaken = true;
                            break;
                        }
                    }
                    if (!isTaken) {
                        availableEconomySeats.add(new int[]{r, c});
                    }
                }
            }

            if (availableEconomySeats.isEmpty()) {
                request.setAttribute("errorMessage", "Η κράτηση απέτυχε.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            Collections.shuffle(availableEconomySeats);
            int[] luckySeat = availableEconomySeats.getFirst();
            row = luckySeat[0];
            column = luckySeat[1];
        } else {
            String rowParam = request.getParameter("row");
            row = (rowParam != null && !rowParam.isEmpty()) ? Integer.parseInt(rowParam) : null;
            String colParam = request.getParameter("column");
            column = (colParam != null && !colParam.isEmpty()) ? Integer.parseInt(colParam) : null;
        }

        Booking booking = new Booking(null, clientId, flightId, ticketType, row, column, BookingState.CREATED);
        if (bookingDAO.insertBooking(booking)) {
            response.sendRedirect("BookingServlet?action=view&id=" + booking.getId() + "&msg=insert_success");
        } else {
            request.setAttribute("errorMessage", "Η εισαγωγή της κράτησης απέτυχε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Προβολή λεπτομερειών κράτησης
    private void viewBooking(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Booking booking = bookingDAO.getBooking(id);
        if (booking == null) {
            request.setAttribute("errorMessage", "Η κράτηση δεν βρέθηκε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        if (loggedUser.getRole() == Role.FLIGHT_MANAGER || loggedUser.getId() == booking.getClientId()) {
            Flight flight = flightDAO.getFlight(booking.getFlightId());
            if (flight != null) {
                request.setAttribute("flightState", flight.getState().name());
                request.setAttribute("flightRows", flight.getRows());
                request.setAttribute("flightSeatsPerRow", flight.getSeatsRow());
                request.setAttribute("flightRowsBusiness", flight.getRowsBusiness());
            }

            List<Booking> occupiedSeats = bookingDAO.getOccupiedSeats(booking.getFlightId());
            request.setAttribute("booking", booking);
            request.setAttribute("occupiedSeats", occupiedSeats);
            request.getRequestDispatcher("viewBooking.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Μη εξουσιοδοτημένη προβολή κράτησης.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }


    // Ενημέρωση τύπου εισιτηρίου
    private void updateBookingType(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int bookingId = Integer.parseInt(request.getParameter("id"));
        TicketType newType = TicketType.valueOf(request.getParameter("ticket_type"));
        Booking booking = bookingDAO.getBooking(bookingId);
        if (booking == null) {
            request.setAttribute("errorMessage", "Η κράτηση δεν βρέθηκε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        boolean isManager = loggedUser.getRole() == Role.FLIGHT_MANAGER || loggedUser.getRole() == Role.SYSTEM_MANAGER;
        boolean isOwner = loggedUser.getRole() == Role.CLIENT && booking.getClientId() == loggedUser.getId();
        if (!isManager && !isOwner) {
            request.setAttribute("errorMessage", "Μη εξουσιοδοτημένη τροποποίηση κράτησης.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        if (bookingDAO.updateBookingType(bookingId, newType)) {
            response.sendRedirect("BookingServlet?action=view&id=" + bookingId + "&msg=update_success");
        } else {
            request.setAttribute("errorMessage", "Η αλλαγή τύπου απέτυχε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Ακύρωση κράτησης
    private void cancelBooking(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int bookingId = Integer.parseInt(request.getParameter("id"));
        Booking booking = bookingDAO.getBooking(bookingId);
        if (booking == null || (loggedUser.getRole() != Role.FLIGHT_MANAGER && loggedUser.getId() != booking.getClientId())) {
            request.setAttribute("errorMessage", "Μη εξουσιοδοτημένη ενέργεια ακύρωσης.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        if (bookingDAO.cancelBooking(bookingId)) {
            response.sendRedirect("BookingServlet?action=search&msg=cancel_success");
        } else {
            request.setAttribute("errorMessage", "Η ακύρωση απέτυχε.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Δέσμευση Θέσης ajax gson
    private void reserveSeat(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int bookingId = Integer.parseInt(request.getParameter("id"));
        Booking booking = bookingDAO.getBooking(bookingId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();
        if (booking == null || (loggedUser.getRole() != Role.FLIGHT_MANAGER && loggedUser.getId() != booking.getClientId())) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Μη εξουσιοδοτημένη δέσμευση θέσης.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        int selectedRow = Integer.parseInt(request.getParameter("row"));
        int selectedColumn = Integer.parseInt(request.getParameter("column"));
        if (bookingDAO.reserveSeat(bookingId, selectedRow, selectedColumn)) {
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Επέλεξες θέση -> Σειρά " + selectedRow + " - Θέση " + selectedColumn + ".");
        } else {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Η δέσμευση απέτυχε.");
        }
        response.getWriter().write(jsonResponse.toString());
    }

    // Αλλαγή Θέσης ajax gson
    private void changeSeat(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int bookingId = Integer.parseInt(request.getParameter("id"));
        Booking booking = bookingDAO.getBooking(bookingId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();
        if (booking == null || (loggedUser.getRole() != Role.FLIGHT_MANAGER && loggedUser.getId() != booking.getClientId())) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Μη εξουσιοδοτημένη αλλαγή θέσης.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        int newRow = Integer.parseInt(request.getParameter("row"));
        int newColumn = Integer.parseInt(request.getParameter("column"));
        if (bookingDAO.changeSeat(bookingId, newRow, newColumn)) {
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Άλλαξες θέση -> Σειρά " + newRow + " - Θέση " + newColumn + ".");
        } else {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Η αλλαγή απέτυχε.");
        }
        response.getWriter().write(jsonResponse.toString());
    }

    // Αποδέσμευση Θέσης ajax gson
    private void unreserveSeat(HttpServletRequest request, HttpServletResponse response, User loggedUser) throws Exception {
        int bookingId = Integer.parseInt(request.getParameter("id"));
        Booking booking = bookingDAO.getBooking(bookingId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();
        if (booking == null || (loggedUser.getRole() != Role.FLIGHT_MANAGER && loggedUser.getId() != booking.getClientId())) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Μη εξουσιοδοτημένη αποδέσμευση θέσης.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        if (bookingDAO.unreserveSeat(bookingId)) {
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Η θέση σου αποδεσμεύτηκε.");
        } else {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Η αποδέσμευση θέσης απέτυχε.");
        }

        response.getWriter().write(jsonResponse.toString());
    }

    // sanitize για να αποφύγουμε xss
    private String sanitize(String input) {
        if (input == null) return null;
        return Encode.forHtml(input.trim());
    }
}