package lab6.DateTimeServerRMI;

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeImplementation extends UnicastRemoteObject implements TimeOperations {
    DateFormat fdate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat ftime = new SimpleDateFormat("hh:mm:ss");

    public TimeImplementation() throws RemoteException {
        super();
    }

    @Override
    public String update(String mode) throws RemoteException {
        Date date = new Date();

        if (mode.equalsIgnoreCase("Date"))
            return fdate.format(date);
        else if (mode.equalsIgnoreCase("Time"))
            return ftime.format(date);
        else
            return "Invalid input";
    }
}