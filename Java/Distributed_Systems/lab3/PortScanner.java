package lab3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/*
 * Simple port scanner.
 * Tries to connect to all ports 1-65535.
 * If successful, the port is open.
 */

public class PortScanner {
    private static final int PORT_RANGE = 65535;

    public static void main(String[] args) {
        System.out.println("Scanning for open ports in localhost");
        for(int port = 1; port <= PORT_RANGE; port++) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", port), 200);
                System.out.println("Port " + port + " is OPEN");
                socket.close();
            } catch (IOException _) {
            }
        }
    }
}
