package lab6.DateTimeServerRMI;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class TimeC {
    public static void main(String[] args) {
        try {
            String url = "//localhost/TimeServer";
            TimeOperations look_up = (TimeOperations) Naming.lookup(url);

            Scanner scn = new Scanner(System.in);

            while (true) {
                System.out.println("What do you want?[Date or Time Service]..\n" + "Type Exit to terminate connection.");
                String DateOrTime = scn.nextLine();
                if (DateOrTime.equals("Exit")) break;
                System.out.println(look_up.update(DateOrTime));
            }
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }
}