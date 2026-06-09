package lab3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
 * Calculator server for square using UDP packets.
 * Receives response, parses as int.
 * Does the square, parses as String, sends the result back.
 */

public class UDPServerCalc {
    private final static int PORT = 1234;
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("UDP Calc Server started...");
            while (true) {
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);
                int integer = Integer.parseInt(new String(inPacket.getData(), 0, inPacket.getLength()));
                String square = Integer.toString(integer * integer);
                DatagramPacket outPacket = new DatagramPacket(square.getBytes(), square.getBytes().length, inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket);
            }
        } catch (Exception _) {
            System.out.println("Error");
        }
    }
}