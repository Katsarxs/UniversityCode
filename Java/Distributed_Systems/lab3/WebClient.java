package lab3;

import java.io.*;
import java.net.*;

/*
 * Web Client.
 * Connects to localhost and sends HTTP Request 'GET'.
 * Then gets HTTP Response and prints it.
 */

public class WebClient {
    public static final String ADDRESS = "localhost";
    public static final int PORT = 81;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(ADDRESS, PORT);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Address : " + socket.getInetAddress() + " Port : " + socket.getPort());
            System.out.println("Local Address : " + socket.getLocalAddress() + " Local Port : " + socket.getLocalPort());

            String request = "GET / HTTP/1.1\nHost: localhost\n\n"; // 2 newlines to mark the end of the headers
            String response;
            out.write(request);
            out.flush();
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
            in.close();
            out.close();
            socket.close();
        } catch (IOException _) {
            System.out.println("Connection Refused!!!");
        }
    }
}
