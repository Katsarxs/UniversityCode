package lab6.DateTimeServer;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TimeC {
    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);
            InetAddress ip = InetAddress.getByName("localhost");
            Socket s = new Socket(ip, 5056);
            DataInputStream is = new DataInputStream(s.getInputStream());
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            while (true) {
                System.out.println(is.readUTF());
                String send = scn.nextLine();
                os.writeUTF(send);
                if (send.equals("Exit")) {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    break;
                }
                String received = is.readUTF();
                System.out.println(received);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}