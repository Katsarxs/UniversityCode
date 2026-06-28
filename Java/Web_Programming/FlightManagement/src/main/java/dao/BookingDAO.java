package dao;

import model.Booking;
import model.enums.BookingState;
import model.enums.TicketType;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDAO {
    private static final String UPDATE_SEAT_SQL = "UPDATE booking SET roww = ?, columnn = ?, update_date = CURRENT_DATE WHERE id = ?";

    // Εισαγωγή νέας κράτησης
    public boolean insertBooking(Booking booking) {
        if (booking.getClientId() <= 0 || booking.getFlightId() <= 0 || booking.getTicketType() == null) {
            System.out.println("Σφάλμα : Μη έγκυρες ή ελλιπείς τιμές στα υποχρεωτικά πεδία της κράτησης.");
            return false;
        }

        try (Connection connection = DBConnection.getConnection()) {
            return addBooking(connection, booking) > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα κατά την εισαγωγή της κράτησης: " + e.getMessage());
            return false;
        }
    }

    // Τροποποίηση τύπου κράτησης
    public boolean updateBookingType(int bookingId, TicketType newTicketType) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            System.out.println("Σφάλμα : Η κράτηση δεν βρέθηκε.");
            return false;
        }

        String flightState = getFlightState(booking.getFlightId());
        if (!"CREATED".equals(flightState)) {
            System.out.println("Σφάλμα : Δεν επιτρέπεται η τροποποίηση της κράτησης επειδή η πτήση είναι σε κατάσταση: " + flightState);
            return false;
        }

        String updateSQL = "UPDATE booking SET type = ?, update_date = CURRENT_DATE WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, newTicketType.name());
            preparedStatement.setInt(2, bookingId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }

    // Ακύρωση Κράτησης
    public boolean cancelBooking(int bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            System.out.println("Σφάλμα: Η κράτηση δεν βρέθηκε.");
            return false;
        }

        String flightState = getFlightState(booking.getFlightId());
        if ("CANCELLED".equals(flightState) || "COMPLETED".equals(flightState)) {
            System.out.println("Σφάλμα : Δεν επιτρέπεται η ακύρωση της κράτησης επειδή η πτήση είναι ήδη " + flightState);
            return false;
        }

        String cancelSQL = "UPDATE booking SET state = 'CANCELLED', roww = NULL, columnn = NULL, " + "update_date = CURRENT_DATE WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(cancelSQL)) {
            preparedStatement.setInt(1, bookingId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }

    // Εμφάνηση Κράτησης
    public Booking getBooking(int id) {
        String sql = "SELECT * FROM booking WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return newBoooking(resultSet);
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return null;
    }

    // Αναζήτηση Κρατήσεων
    public List<Booking> searchBookings(String role, int loggedUserId, LocalDate startDate, LocalDate endDate, String fullname, String afm, String idNumber, String bookingNumber, BookingState bookingState) {
        List<Booking> bookingsList = new ArrayList<>();
        StringBuilder searchSQL = new StringBuilder(
                "SELECT b.* FROM booking b " +
                        "JOIN flight f ON b.flight_id = f.id " +
                        "JOIN user u ON b.client_id = u.id " +
                        "LEFT JOIN client c ON u.id = c.user_id " +
                        "WHERE 1=1"
        );

        if ("CLIENT".equals(role)) {
            searchSQL.append(" AND b.client_id = ?");
        }

        boolean hasFilter = (startDate != null) || (endDate != null) || (fullname != null && !fullname.trim().isEmpty()) || (afm != null && !afm.trim().isEmpty()) || (idNumber != null && !idNumber.trim().isEmpty()) || (bookingNumber != null && !bookingNumber.trim().isEmpty()) || (bookingState != null);
        if (hasFilter) {
            if (startDate != null) {
                searchSQL.append(" AND b.book_date >= ?");
            }
            if (endDate != null) {
                searchSQL.append(" AND b.book_date <= ?");
            }
            if (fullname != null && !fullname.trim().isEmpty()) {
                searchSQL.append(" AND u.fullname LIKE ?");
            }
            if (afm != null && !afm.trim().isEmpty()) {
                searchSQL.append(" AND c.afm LIKE ?");
            }
            if (idNumber != null && !idNumber.trim().isEmpty()) {
                searchSQL.append(" AND u.id_number LIKE ?");
            }
            if (bookingNumber != null && !bookingNumber.trim().isEmpty()) {
                searchSQL.append(" AND b.booking_number LIKE ?");
            }
            if (bookingState != null) {
                searchSQL.append(" AND b.state = ?");
            }
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(searchSQL.toString())) {
            int parameterIndex = 1;
            if ("CLIENT".equals(role)) {
                preparedStatement.setInt(parameterIndex++, loggedUserId);
            }

            if (hasFilter) {
                if (startDate != null) {
                    preparedStatement.setDate(parameterIndex++, Date.valueOf(startDate));
                }
                if (endDate != null) {
                    preparedStatement.setDate(parameterIndex++, Date.valueOf(endDate));
                }
                if (isValidFilter(fullname)) {
                    preparedStatement.setString(parameterIndex++, "%" + fullname.trim() + "%");
                }
                if (isValidFilter(afm)) {
                    preparedStatement.setString(parameterIndex++, "%" + afm.trim() + "%");
                }
                if (isValidFilter(idNumber)) {
                    preparedStatement.setString(parameterIndex++, "%" + idNumber.trim() + "%");
                }
                if (isValidFilter(bookingNumber)) {
                    preparedStatement.setString(parameterIndex++, "%" + bookingNumber.trim() + "%");
                }
                if (bookingState != null) {
                    preparedStatement.setString(parameterIndex++, bookingState.name());
                }
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookingsList.add(newBoooking(resultSet));
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return bookingsList;
    }

    // Δέσμευση θέσης
    public boolean reserveSeat(int bookingId, int selectedRow, int selectedColumn) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            System.out.println("Σφάλμα : Η κράτηση δεν βρέθηκε.");
            return false;
        }

        if (booking.getTicketType() == TicketType.ECONOMY) {
            System.out.println("Σφάλμα : Οι επιβάτες με εισιτήριο ECONOMY δεν μπορούν να επιλέξουν θέση.");
            return false;
        }

        int rowsBusiness = 0;
        String flightSQL = "SELECT rows_business, state FROM flight WHERE id = ?";
        String flightState = "";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(flightSQL)) {
            preparedStatement.setInt(1, booking.getFlightId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    rowsBusiness = resultSet.getInt("rows_business");
                    flightState = resultSet.getString("state");
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }

        if (!"STAFFED".equals(flightState)) {
            System.out.println("Σφάλμα: Η δέσμευση θέσεων επιτρέπεται μόνο όταν η πτήση είναι ΣΤΕΛΕΧΩΜΕΝΗ.");
            return false;
        }

        if (booking.getTicketType() == TicketType.NORMAL && selectedRow <= rowsBusiness) {
            System.out.println("Σφάλμα: Τα εισιτήρια NORMAL δεν επιτρέπεται να δεσμεύσουν θέση σε BUSINESS σειρά.");
            return false;
        }

        String checkSeatSQL = "SELECT id FROM booking WHERE flight_id = ? AND roww = ? AND columnn = ? AND state != 'CANCELLED'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkSeatSQL)) {
            preparedStatement.setInt(1, booking.getFlightId());
            preparedStatement.setInt(2, selectedRow);
            preparedStatement.setInt(3, selectedColumn);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Σφάλμα: Η θέση είναι ήδη κατειλημμένη.");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SEAT_SQL)) {
            preparedStatement.setInt(1, selectedRow);
            preparedStatement.setInt(2, selectedColumn);
            preparedStatement.setInt(3, bookingId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }

    // Αλλαγή θέσης
    public boolean changeSeat(int bookingId, int newRow, int newColumn) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            System.out.println("Σφάλμα: Η κράτηση δεν βρέθηκε.");
            return false;
        }

        if (booking.getTicketType() == TicketType.ECONOMY) {
            System.out.println("Σφάλμα: Οι επιβάτες με εισιτήριο ECONOMY δεν έχουν θέση για να αλλάξουν.");
            return false;
        }

        int rowsBusiness = 0;
        String flightSQL = "SELECT rows_business, state FROM flight WHERE id = ?";
        String flightState = "";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(flightSQL)) {
            preparedStatement.setInt(1, booking.getFlightId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    rowsBusiness = resultSet.getInt("rows_business");
                    flightState = resultSet.getString("state");
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }

        if (!"STAFFED".equals(flightState)) {
            System.out.println("Σφάλμα: Η αλλαγή θέσεων επιτρέπεται μόνο όταν η πτήση είναι στελεχώμενη.");
            return false;
        }

        if (booking.getTicketType() == TicketType.NORMAL && newRow <= rowsBusiness) {
            System.out.println("Σφάλμα: Τα εισιτήρια NORMAL δεν επιτρέπεται να αλλάξουν θέση σε Business σειρά.");
            return false;
        }

        String checkSeatSQL = "SELECT id FROM booking WHERE flight_id = ? AND roww = ? AND columnn = ? AND id != ? AND state != 'CANCELLED'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkSeatSQL)) {
            preparedStatement.setInt(1, booking.getFlightId());
            preparedStatement.setInt(2, newRow);
            preparedStatement.setInt(3, newColumn);
            preparedStatement.setInt(4, bookingId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Σφάλμα: Η νέα θέση που επέλεξες είναι ήδη κατειλημμένη.");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SEAT_SQL)) {
            preparedStatement.setInt(1, newRow);
            preparedStatement.setInt(2, newColumn);
            preparedStatement.setInt(3, bookingId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }

    // Αποδέσμευση θέσης
    public boolean unreserveSeat(int bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            System.out.println("Σφάλμα: Η κράτηση δεν βρέθηκε.");
            return false;
        }

        if (booking.getTicketType() == TicketType.ECONOMY) {
            System.out.println("Σφάλμα: Οι επιβάτες με εισιτήριο ECONOMY δεν διαθέτουν επιλεγμένη θέση.");
            return false;
        }

        String flightSql = "SELECT state FROM flight WHERE id = ?";
        String flightState = "";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement PreparedStatement = connection.prepareStatement(flightSql)) {
            PreparedStatement.setInt(1, booking.getFlightId());
            try (ResultSet resultSet = PreparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    flightState = resultSet.getString("state");
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }

        if (!"STAFFED".equals(flightState)) {
            System.out.println("Σφάλμα: Η αποδέσμευση θέσης επιτρέπεται μόνο όταν η πτήση είναι στελεχώμενη.");
            return false;
        }

        String sql = "UPDATE booking SET roww = NULL, columnn = NULL, update_date = CURRENT_DATE WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookingId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }

    // Επιτρέφει κατειλημμένες θέσεις
    public List<Booking> getOccupiedSeats(int flightId) {
        List<Booking> occupied = new ArrayList<>();
        String sql = "SELECT roww, columnn FROM booking WHERE flight_id = ? AND roww IS NOT NULL AND columnn IS NOT NULL AND state != 'CANCELLED'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, flightId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking();
                    booking.setRow(resultSet.getInt("roww"));
                    booking.setColumn(resultSet.getInt("columnn"));
                    occupied.add(booking);
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return occupied;
    }

    // Αυτόματη επιλογή θέσης σε περίπτωση μη επιλογής
    public void assignAutomaticSeats(Connection connection, int flightId) {
        String unassignedSQL = "SELECT id FROM booking WHERE flight_id = ? AND roww IS NULL AND state != 'CANCELLED'";
        String airplaneSQL = "SELECT rowss, seats_row FROM flight WHERE id = ?";
        int totalRows = 0;
        int seatsPerRow = 0;
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(airplaneSQL)) {
                preparedStatement.setInt(1, flightId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        totalRows = resultSet.getInt("rowss");
                        seatsPerRow = resultSet.getInt("seats_row");
                    }
                }
            }

            if (totalRows == 0 || seatsPerRow == 0) {
                return;
            }

            List<Booking> occupied = getOccupiedSeats(flightId);
            // Βρίσκουμε τις κρατήσεις χωρίς θέση και μοιράζουμε τυχαία τις ελεύθερες
            try (PreparedStatement preparedStatement = connection.prepareStatement(unassignedSQL)) {
                preparedStatement.setInt(1, flightId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int bookingId = resultSet.getInt("id");
                        boolean seatFound = false;
                        for (int r = 1; r <= totalRows && !seatFound; r++) {
                            for (int c = 1; c <= seatsPerRow; c++) {
                                final int currentRow = r;
                                final int currentColumn = c;
                                boolean isTaken = occupied.stream().anyMatch(b -> b.getRow() != null && b.getColumn() != null && b.getRow() == currentRow && b.getColumn() == currentColumn);
                                if (!isTaken) {
                                    String updateSQL = "UPDATE booking SET roww = ?, columnn = ? WHERE id = ?";
                                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateSQL)) {
                                        preparedStatement2.setInt(1, r);
                                        preparedStatement2.setInt(2, c);
                                        preparedStatement2.setInt(3, bookingId);
                                        preparedStatement2.executeUpdate();
                                    }

                                    Booking newOccupied = new Booking();
                                    newOccupied.setRow(r);
                                    newOccupied.setColumn(c);
                                    occupied.add(newOccupied);
                                    seatFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
    }

    // Προσθήκη κράτησης
    private int addBooking(Connection connection, Booking booking) throws Exception {
        String sql = "INSERT INTO booking (booking_number, client_id, flight_id, type, roww, columnn, state) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (booking.getBookingNumber() == null || booking.getBookingNumber().trim().isEmpty()) {
                String uniqueCode = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                booking.setBookingNumber(uniqueCode);
            }
            preparedStatement.setString(1, booking.getBookingNumber());
            preparedStatement.setInt(2, booking.getClientId());
            preparedStatement.setInt(3, booking.getFlightId());
            preparedStatement.setString(4, booking.getTicketType().name());
            if (booking.getRow() != null && booking.getRow() > 0) {
                preparedStatement.setInt(5, booking.getRow());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }

            if (booking.getColumn() != null && booking.getColumn() > 0) {
                preparedStatement.setInt(6, booking.getColumn());
            } else {
                preparedStatement.setNull(6, Types.INTEGER);
            }

            preparedStatement.setString(7, booking.getState() != null ? booking.getState().name() : BookingState.CREATED.name());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    booking.setId(id);
                    return id;
                } else {
                    throw new Exception("Δεν βρέθηκε auto-increment booking ID.");
                }
            }
        }
    }

    // Επιτρέφει τη κατάστηαση της πτήσης
    private String getFlightState(int flightId) {
        String sql = "SELECT state FROM flight WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, flightId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getString("state");
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return "";
    }

    // Βοηθητική μέθοδος προσθήκης booking
    private Booking newBoooking(ResultSet resultSet) throws SQLException {
        return new Booking(resultSet.getInt("id"), resultSet.getString("booking_number"), resultSet.getInt("client_id"), resultSet.getInt("flight_id"), resultSet.getDate("book_date").toLocalDate(), resultSet.getDate("update_date").toLocalDate(), TicketType.valueOf(resultSet.getString("type")), resultSet.getObject("roww") != null ? resultSet.getInt("roww") : null, resultSet.getObject("columnn") != null ? resultSet.getInt("columnn") : null, BookingState.valueOf(resultSet.getString("state")));
    }

    // Βοηθητική μέθοδος ελέγχου φίλτρου
    private boolean isValidFilter(String value) {
        return value != null && !value.trim().isEmpty();
    }
}