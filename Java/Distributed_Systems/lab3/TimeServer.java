package lab3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/*
 * Simple time-server.
 * Accepts connection from the client and sends the real time.
 */

public class TimeServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5555)){
            while (true) {
                try (Socket client = serverSocket.accept()){
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    out.write(new Date().toString());
                    out.newLine();
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}
