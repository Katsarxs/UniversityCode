package dao;

import model.Flight;
import model.enums.FlightState;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    // Eισαγωγή νέας πτήσης
    public boolean insertFlight(Flight flight) {
        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty() || flight.getAirplane() == null || flight.getAirplane().trim().isEmpty() || flight.getDepartureDate() == null || flight.getDepartureTime() == null || flight.getSeats() <= 0 || flight.getRows() <= 0 || flight.getSeatsRow() <= 0 || flight.getRowsBusiness() < 0) {
            System.out.println("Σφάλμα: Μη έγκυρες ή ελλιπείς τιμές στα πεδία της πτήσης.");
            return false;
        }

        if (!checkFlightNumber(flight.getFlightNumber())) {
            System.out.println("Σφάλμα: Ο αριθμός πτήσης '" + flight.getFlightNumber() + "' υπάρχει ήδη.");
            return false;
        }

        try (Connection connection = DBConnection.getConnection()) {
            int generatedID = addFlight(connection, flight);
            return generatedID > 0;
        } catch (Exception e) {
            System.out.println("Σφάλμα κατά την εισαγωγή της πτήσης: " + e.getMessage());
            return false;
        }
    }

    // Εισαγωγή πολλαπλών πτήσεων
    public int importFlights(List<Flight> flightsList) {
        int imported = 0;
        for (Flight flight : flightsList) {
            boolean success = insertFlight(flight);
            if (success) {
                imported++;
            } else {
                System.out.println("Διπλότυπη πτήση : " + (flight.getFlightNumber() != null ? flight.getFlightNumber() : "δεν βρέθηκε αριθμός πτήσης."));
            }
        }
        return imported;
    }

    // Εμφάνιση πτήσης
    public Flight getFlight(int id) {
        String selectFlightSQL = "SELECT * FROM flight WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectFlightSQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    FlightState state = FlightState.valueOf(resultSet.getString("state"));
                    return new Flight(resultSet.getInt("id"), resultSet.getString("flight_number"), resultSet.getString("airplane"), resultSet.getDate("departure_date").toLocalDate(), resultSet.getTime("departure_time").toLocalTime(), resultSet.getDate("insertion_date").toLocalDate(), resultSet.getDate("update_date").toLocalDate(), resultSet.getInt("seats"), resultSet.getInt("rowss"), resultSet.getInt("seats_row"), resultSet.getInt("rows_business"), state);
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return null;
    }

    // Ενημέρωση πτήσης
    public boolean updateFlight(Flight flight) {
        Flight currentFlight = getFlight(flight.getId());
        if (currentFlight == null) {
            System.out.println("Σφάλμα: Η πτήση δεν βρέθηκε για ενημέρωση.");
            return false;
        }

        if (flight.getAirplane() == null || flight.getAirplane().trim().isEmpty() || flight.getDepartureDate() == null || flight.getDepartureTime() == null || flight.getSeats() <= 0 || flight.getRows() <= 0 || flight.getSeatsRow() <= 0 || flight.getRowsBusiness() < 0 || flight.getState() == null) {
            System.out.println("Σφάλμα: Μη έγκυρες ή ελλιπείς τιμές στα πεδία της πτήσης.");
            return false;
        }

        if (!checkStateTransition(currentFlight.getState(), flight.getState())) {
            System.out.println("Σφάλμα : Δεν γίνεται μετάβαση από " + currentFlight.getState().name() + " σε " + flight.getState().name());
            return false;
        }

        String updateFlightSQL = "UPDATE flight SET airplane = ?, departure_date = ?, departure_time = ?, " + "seats = ?, rowss = ?, seats_row = ?, rows_business = ?, state = ?, " + "update_date = CURRENT_DATE WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateFlightSQL)) {
                preparedStatement.setString(1, flight.getAirplane());
                preparedStatement.setDate(2, java.sql.Date.valueOf(flight.getDepartureDate()));
                preparedStatement.setTime(3, java.sql.Time.valueOf(flight.getDepartureTime()));
                preparedStatement.setInt(4, flight.getSeats());
                preparedStatement.setInt(5, flight.getRows());
                preparedStatement.setInt(6, flight.getSeatsRow());
                preparedStatement.setInt(7, flight.getRowsBusiness());
                preparedStatement.setString(8, flight.getState().name());
                preparedStatement.setInt(9, flight.getId());
                preparedStatement.executeUpdate();
            }

            if (flight.getState() == FlightState.CANCELLED) {
                String updateBookingsSQL = "UPDATE booking SET state = 'CANCELLED', update_date = CURRENT_DATE WHERE flight_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookingsSQL)) {
                    preparedStatement.setInt(1, flight.getId());
                    preparedStatement.executeUpdate();
                }
            } else if (flight.getState() == FlightState.COMPLETED) {
                String updateBookingsSQL = "UPDATE booking SET state = 'COMPLETED', update_date = CURRENT_DATE " + "WHERE flight_id = ? AND state != 'CANCELLED'";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookingsSQL)) {
                    preparedStatement.setInt(1, flight.getId());
                    preparedStatement.executeUpdate();
                }

                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.assignAutomaticSeats(connection, flight.getId());
            }
            connection.commit();
            return true;

        } catch (Exception exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    System.out.println("Σφάλμα : " + rollbackException.getMessage());
                }
            }
            System.out.println("Σφάλμα : " + exception.getMessage());
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

    // Αναζήτηση Πτήσης
    public List<Flight> searchFlights(String flightNumber, String airplane, LocalDate departureDate, FlightState state) {
        List<Flight> flightsList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM flight WHERE 1=1");
        if (flightNumber != null && !flightNumber.trim().isEmpty()) {
            sql.append(" AND flight_number LIKE ?");
        }

        if (airplane != null && !airplane.trim().isEmpty()) {
            sql.append(" AND airplane LIKE ?");
        }

        if (departureDate != null) {
            sql.append(" AND departure_date = ?");
        }

        if (state != null) {
            sql.append(" AND state = ?");
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            int paramemterIndex = 1;
            if (flightNumber != null && !flightNumber.trim().isEmpty()) {
                preparedStatement.setString(paramemterIndex++, "%" + flightNumber.trim() + "%");
            }

            if (airplane != null && !airplane.trim().isEmpty()) {
                preparedStatement.setString(paramemterIndex++, "%" + airplane.trim() + "%");
            }

            if (departureDate != null) {
                preparedStatement.setDate(paramemterIndex++, java.sql.Date.valueOf(departureDate));
            }

            if (state != null) {
                preparedStatement.setString(paramemterIndex++, state.name());
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FlightState flightState = FlightState.valueOf(resultSet.getString("state"));
                    Flight flight = new Flight(resultSet.getInt("id"), resultSet.getString("flight_number"), resultSet.getString("airplane"), resultSet.getDate("departure_date").toLocalDate(), resultSet.getTime("departure_time").toLocalTime(), resultSet.getDate("insertion_date").toLocalDate(), resultSet.getDate("update_date").toLocalDate(), resultSet.getInt("seats"), resultSet.getInt("rowss"), resultSet.getInt("seats_row"), resultSet.getInt("rows_business"), flightState);
                    flightsList.add(flight);
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
        return flightsList;
    }

    // Διαγραφή πτήσης
    public boolean deleteFlight(int id) {
        String cancelFlightSQL = "UPDATE flight SET state = 'CANCELLED', update_date = CURRENT_DATE WHERE id = ?";
        String cancelBookingsSQL = "UPDATE booking SET state = 'CANCELLED', roww = NULL, columnn = NULL, update_date = CURRENT_DATE WHERE flight_id = ?";

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(cancelFlightSQL)) {
                preparedStatement.setInt(1, id);
                int affectedFlights = preparedStatement.executeUpdate();

                if (affectedFlights == 0) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(cancelBookingsSQL)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    System.out.println("Σφάλμα : " + rollbackException.getMessage());
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

    // Αυτόματη μετάβαση καταστάσεων πτήσεων
    public void updateFlightStates() {
        // Από ΔΗΜΙΟΥΡΓΗΜΕΝΗ σε ΣΤΕΛΕΧΩΜΕΝΗ σε 2 μέρες πριν
        String staffedSQL = "UPDATE flight SET state = 'STAFFED', update_date = CURRENT_DATE " + "WHERE state = 'CREATED' AND departure_date <= DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY)";
        // Από ΣΤΕΛΕΧΩΜΕΝΗ σε ΠΕΡΑΤΩΜΕΝΗ μία ώρα πριν την πτήση
        String completedSQL = "UPDATE flight SET state = 'COMPLETED', update_date = CURRENT_DATE " + "WHERE state = 'STAFFED' AND TIMESTAMP(departure_date, departure_time) <= DATE_ADD(NOW(), INTERVAL 1 HOUR)";
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement ps1 = connection.prepareStatement(staffedSQL)) {
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = connection.prepareStatement(completedSQL)) {
                ps2.executeUpdate();
            }

            String getJustCompletedSQL = "SELECT id FROM flight WHERE state = 'COMPLETED' AND update_date = CURRENT_DATE";
            try (PreparedStatement preparedStatement = connection.prepareStatement(getJustCompletedSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                BookingDAO bookingDAO = new BookingDAO();
                while (resultSet.next()) {
                    bookingDAO.assignAutomaticSeats(connection, resultSet.getInt("id"));
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
    }

    // Βοηθητική μέθοδο για εισαγωγή πτήσης
    private int addFlight(Connection connection, Flight flight) throws Exception {
        String sql = "INSERT INTO flight (flight_number, airplane, departure_date, departure_time, " + "seats, rowss, seats_row, rows_business, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, flight.getFlightNumber());
            preparedStatement.setString(2, flight.getAirplane());
            preparedStatement.setDate(3, java.sql.Date.valueOf(flight.getDepartureDate()));
            preparedStatement.setTime(4, java.sql.Time.valueOf(flight.getDepartureTime()));
            preparedStatement.setInt(5, flight.getSeats());
            preparedStatement.setInt(6, flight.getRows());
            preparedStatement.setInt(7, flight.getSeatsRow());
            preparedStatement.setInt(8, flight.getRowsBusiness());
            preparedStatement.setString(9, flight.getState() != null ? flight.getState().name() : "CREATED");
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    flight.setId(id);
                    return id;
                } else {
                    throw new Exception("Σφάλμα με το auto-incremented flight ID.");
                }
            }
        }
    }

    // Τσέκαρε κατάσταση πτήσης για μετάβαση
    private boolean checkStateTransition(FlightState current, FlightState next) {
        if (current == next) return true;
        return switch (current) {
            case CREATED -> next == FlightState.STAFFED || next == FlightState.CANCELLED;
            case STAFFED -> next == FlightState.COMPLETED || next == FlightState.CANCELLED;
            case CANCELLED, COMPLETED -> false;
            default -> false;
        };
    }

    // Τσέκαρε αριθμό πτήσης αν υπάρχει
    private boolean checkFlightNumber(String flightNumber) {
        String query = "SELECT id FROM flight WHERE flight_number = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, flightNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return !resultSet.next();
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
            return false;
        }
    }
}
