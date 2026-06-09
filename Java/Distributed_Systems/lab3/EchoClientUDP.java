package lab3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/*
 * Echo client for UDP echo server.
 * Inputs a message from a user, creates and sends a datagram packet.
 * Then creates a new packet and receives a response and prints it.
 * If input is bye, exits.
 */

public class EchoClientUDP {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);
            InetAddress host = InetAddress.getLocalHost();
            byte[] buffer = new byte[1024];
            while (true) {
                System.out.println("Enter a message : ");
                String message = scanner.nextLine();
                if (message.equals("bye")) {
                    break;
                }
                DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.length(), host, 1234);
                socket.send(outPacket);
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);
                String response = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println(response);
            }
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
