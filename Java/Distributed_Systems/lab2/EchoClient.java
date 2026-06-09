package lab2;

import java.net.*;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/*
 * Client that sends messages to server and gets response
 * The client writes a message from keyboard and receives as respond the same message
 * To terminate the connection and the server, writes "bye" or "Not Welcomed"
 */

public class EchoClient {
    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            // Initialize variables
            String message;
            String response;
            Scanner scanner = new Scanner(System.in);

            // Connect to server
            Socket sock = new Socket("localhost", PORT);

            // Make streams to read and write from stream of the server
            BufferedReader instream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedWriter outstream = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

            // Show remote address and port
            System.out.println("Sending messages to the server...");
            System.out.println("Address : " + sock.getInetAddress() + " Port : " + sock.getPort());

            // Show local address and port
            System.out.println("Local Address " + sock.getLocalAddress() + " Local Port : " + sock.getLocalPort());

            while (true) {
                System.out.print("Write what the client will send: ");

                // Read message from keyboard
                message = scanner.nextLine();

                // Send message to server and newline
                outstream.write(message);
                outstream.newLine();
                outstream.flush();

                // Read message from server
                response = instream.readLine();

                // Print message
                System.out.println("The server says: " + response);

                // Break, if response from server is bye or Not welcomed
                if (Objects.equals(response, "bye") || Objects.equals(response, "Not welcomed")) {
                    System.out.println("Connection Closing...");
                    break;
                }
            }
            // Close streams and sockets
            instream.close();
            outstream.close();
            sock.close();

        } catch (ConnectException e) {
            System.out.println("Connection Refused!!! Server is not running.");
        } catch (IOException ex) {
            System.out.println("Connection Refused!!!");
        }
    }
}
