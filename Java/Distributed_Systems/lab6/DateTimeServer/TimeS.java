package lab6.DateTimeServer;

import java.io.*;
import java.net.*;

public class TimeS {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5056);
        while (true) {
            Socket s = null;
            try {
                s = ss.accept();
                System.out.println("A new client is connected : " + s);
                DataInputStream is = new DataInputStream(s.getInputStream());
                DataOutputStream os = new DataOutputStream(s.getOutputStream());
                System.out.println("New thread for each client");
                Thread t = new ClientHandler(s, is, os);
                t.start();
            } catch (IOException e) {
                s.close();
            }
        }
    }
}