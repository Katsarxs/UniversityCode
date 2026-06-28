package model;

import model.enums.BookingState;
import model.enums.TicketType;

import java.time.LocalDate;

public class Booking {
    private int id, clientId, flightId;
    private String bookingNumber;
    private LocalDate bookDate, updateDate;
    private TicketType ticketType;
    private Integer row, column;
    private BookingState state;

    public Booking() {}

    public Booking(String bookingNumber, int clientId, int flightId, TicketType ticketType, Integer row, Integer column, BookingState state) {
        this.bookingNumber = bookingNumber;
        this.clientId = clientId;
        this.flightId = flightId;
        this.ticketType = ticketType;
        this.row = row;
        this.column = column;
        this.state = state;
    }

    public Booking(int id, String bookingNumber, int clientId, int flightId, LocalDate bookDate, LocalDate updateDate, TicketType ticketType, Integer row, Integer column, BookingState state) {
        this.id = id;
        this.bookingNumber = bookingNumber;
        this.clientId = clientId;
        this.flightId = flightId;
        this.bookDate = bookDate;
        this.updateDate = updateDate;
        this.ticketType = ticketType;
        this.row = row;
        this.column = column;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public int getClientId() {
        return clientId;
    }

    public int getFlightId() {
        return flightId;
    }

    public LocalDate getBookDate() {
        return bookDate;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public BookingState getState() {
        return state;
    }
}
