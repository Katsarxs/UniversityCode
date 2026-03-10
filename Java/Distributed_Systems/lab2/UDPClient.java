package lab2;

import java.net.*;
import java.io.*;

/*
 * UDP Client sends message to server and receives the same back.
 * Usage: java UDPClient <Message>
 * For example : java UDPClient hello
 */

public class UDPClient {
    private static final int PORT = 5557;

    public static void main(String[] args) {
        // Check arguments
        if (args.length < 1) {
            System.out.println("Usage: java UDPClient <Message>");
            System.exit(0);
        }
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            // Initialize packet for request
            DatagramPacket request = new DatagramPacket(args[0].getBytes(), args[0].length(), InetAddress.getLocalHost(), PORT);

            // Send packet to server
            udpSocket.send(request);

            // Initialize packet to receive
            byte[] buffer = new byte[1024];
            DatagramPacket respond = new DatagramPacket(buffer, buffer.length);

            // Receive packet from server
            udpSocket.receive(respond);

            // Print the message
            System.out.println("Message from server : " + new String(respond.getData()));
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}