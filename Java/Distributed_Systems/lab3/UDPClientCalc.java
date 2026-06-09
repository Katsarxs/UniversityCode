package lab3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/*
 * Client for calculator UDP server.
 * Inputs an integer as String, then sends it via a datagram packet.
 * Then receives the packet, gets the value of the String and prints it.
 */

public class UDPClientCalc {
    private static final int PORT = 1234;
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            byte[] buffer = new byte[1024];
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter an integer : ");
            String integer = scanner.nextLine();
            DatagramPacket outPacket = new DatagramPacket(integer.getBytes(), integer.getBytes().length, host, PORT);
            socket.send(outPacket);
            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(inPacket);
            String response = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println("Square of " + integer + " is " + response + ".");

            socket.close();
        } catch (Exception _) {
            System.out.println("Error");
        }
    }
}