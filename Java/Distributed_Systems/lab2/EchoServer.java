package lab2;

import java.net.*;
import java.io.*;

/*
 * Help server in order to run EchoClient
 */

public class EchoServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            String message;

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            Socket client = serverSocket.accept();
            System.out.println("Client connected!");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            while ((message = in.readLine()) != null) {
                out.write(message); // echo back
                out.newLine();
                out.flush();

                if (message.equals("bye")) {
                    System.out.println("Connection Closing...");
                    break;
                }
            }

            client.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println("I/O Error");
        }
    }
}