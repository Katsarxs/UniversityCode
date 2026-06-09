package lab6.DateTimeServer;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

class ClientHandler extends Thread {
    DateFormat fdate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat ftime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream is;
    final DataOutputStream os;
    final Socket s;

    public ClientHandler(Socket s, DataInputStream is, DataOutputStream os) {
        this.s = s;
        this.is = is;
        this.os = os;
    }

    public void run() {
        String accept;
        String tosend;

        while (true) {
            try {
                os.writeUTF("What do you want?[Date or Time Service]..\n" +
                        "Type Exit to terminate connection.");
                accept = is.readUTF();
                if (accept.equals("Exit")) {
                    System.out.println("Closing this connection.");
                    this.s.close();
                    break;
                }
                Date date = new Date();
                switch (accept) {
                    case "Date":
                        tosend = fdate.format(date);
                        os.writeUTF(tosend);
                        break;
                    case "Time":
                        tosend = ftime.format(date);
                        os.writeUTF(tosend);
                        break;
                    default:
                        os.writeUTF("Invalid input");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.is.close();
            this.os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}