package model;

import model.enums.FlightState;
import java.time.LocalDate;
import java.time.LocalTime;

public class Flight {
    private int id, seats, rows, seatsRow, rowsBusiness;
    private String flightNumber, airplane;
    private LocalDate departureDate, insertionDate, updateDate;
    private LocalTime departureTime;
    private FlightState state;

    public Flight() {}

    public Flight(String flightNumber, String airplane, LocalDate departureDate, LocalTime departureTime, int seats, int rows, int seatsRow, int rowsBusiness, FlightState state) {
        this.flightNumber = flightNumber;
        this.airplane = airplane;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.seats = seats;
        this.rows = rows;
        this.seatsRow = seatsRow;
        this.rowsBusiness = rowsBusiness;
        this.state = state;
    }

    public Flight(int id, String flightNumber, String airplane, LocalDate departureDate, LocalTime departureTime, LocalDate insertionDate, LocalDate updateDate, int seats, int rows, int seatsRow, int rowsBusiness, FlightState state) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.airplane = airplane;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.insertionDate = insertionDate;
        this.updateDate = updateDate;
        this.seats = seats;
        this.rows = rows;
        this.seatsRow = seatsRow;
        this.rowsBusiness = rowsBusiness;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAirplane() {
        return airplane;
    }

    public void setAirplane(String airplane) {
        this.airplane = airplane;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getSeatsRow() {
        return seatsRow;
    }

    public void setSeatsRow(int seatsRow) {
        this.seatsRow = seatsRow;
    }

    public int getRowsBusiness() {
        return rowsBusiness;
    }

    public void setRowsBusiness(int rowsBusiness) {
        this.rowsBusiness = rowsBusiness;
    }

    public FlightState getState() {
        return state;
    }

    public void setState(FlightState state) {
        this.state = state;
    }
}
