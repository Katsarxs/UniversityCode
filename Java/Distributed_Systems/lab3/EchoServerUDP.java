package lab3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
 * Echo server using UDP packets.
 * 1) We create a socket on port 1234.
 * 2) We create a buffer and add it to a packet using DatagramPacket.
 * 3) We receive and the packet, we add the content to a new packet and then send it
 *    back to the same address and port.
 */

public class EchoServerUDP {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(1234)){
            byte[] buffer = new byte[1024];
            System.out.println("UDP Echo Server started...");
            while (true) {
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);
                System.out.println("Message received : " + new String(inPacket.getData(), 0, inPacket.getLength()));
                DatagramPacket outPacket = new DatagramPacket(inPacket.getData(), inPacket.getLength(), inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket);
            }
        } catch (Exception e) {
            System.out.println("Error occurred : " + e.getMessage());
        }
    }
}