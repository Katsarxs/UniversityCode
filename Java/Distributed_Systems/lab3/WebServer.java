package lab3;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Web Server.
 * Gets connection from the client.
 * Reads and prints the requests.
 * Then sends the response.
 */

public class WebServer {
    public static final int PORT = 81;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Address : " + socket.getInetAddress() + " Port : " + socket.getPort());
            System.out.println("Local Address : " + socket.getLocalAddress() + " Local Port : " + socket.getLocalPort());

            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) { // If null, skip (so no headers)
                System.out.println(line); // Else, print the request
            }

            out.write("HTTP/1.0 200 OK" + "\n" +
                    "Content-Type: text/html" + "\n" +
                    "Date : " + new Date() + "\n" +
                    "Server: MyApache Java based" + "\n\n" + // 2 newlines to mark the end of the headers
                    "<title>My First Page</title>" + "\n" +
                    "<h1>Welcome to Distributed System Official page</h1>" + "\n"
            );

            out.flush();
            socket.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}