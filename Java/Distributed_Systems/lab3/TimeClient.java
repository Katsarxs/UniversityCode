package lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * Simple time-client.
 * Connects to the server and receives the time.
 */

public class TimeClient {
    public static void main(String[] args) {
        try (Socket socket  = new Socket("localhost", 5555)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Time : " + in.readLine());
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}