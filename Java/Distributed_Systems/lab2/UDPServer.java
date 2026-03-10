package lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
 * UDP server to receive message from client and send back.
 */

public class UDPServer {
    private static final int PORT = 5557;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(PORT);

            System.out.println("Server Running...");

            // Receive respond
            byte[] buffer = new byte[1024];
            DatagramPacket respond = new DatagramPacket(buffer, buffer.length);
            socket.receive(respond);

            String message = new String(respond.getData(), 0, respond.getLength());
            System.out.println("Received : " + message);

            // Send respond back
            DatagramPacket request = new DatagramPacket(message.getBytes(), message.length(), respond.getAddress(), respond.getPort());
            socket.send(request);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) socket.close();
        }
    }
}
