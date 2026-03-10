package lab2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Help Server for CalcClient.
 * Calculation is done and it sends the result.
 */

public class CalcServer {
    private static final int PORT = 5556;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for client...");

            Socket sock = serverSocket.accept();
            System.out.println("Client connected!");

            DataInputStream in = new DataInputStream(sock.getInputStream());
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            int firstInteger;
            int secondInteger;
            float result;
            String operator;

            // Receive first number
            firstInteger = in.readInt();

            // Receive second number
            secondInteger = in.readInt();

            // Receive operator
            operator = in.readUTF();

            boolean valid = true;

            result = switch (operator) {
                case "+" -> firstInteger + secondInteger;
                case "-" -> firstInteger - secondInteger;
                case "*" -> firstInteger * secondInteger;
                case "/" -> {
                    if (secondInteger == 0) {
                        valid = false;
                        yield 0;
                    }
                    yield (float) firstInteger / secondInteger;
                }
                default -> {
                    valid = false;
                    yield 0;
                }
            };

            if (valid) {
                out.writeFloat(result);
                out.writeUTF("DONE");
            } else {
                out.writeFloat(0);
                out.writeUTF("ERROR");
            }

            in.close();
            out.close();
            sock.close();
            serverSocket.close();
            System.out.println("Connection closed.");
        } catch (Exception e) {
            System.out.println("Connection closed.");
        }
    }
}