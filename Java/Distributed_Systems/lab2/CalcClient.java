package lab2;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

/*
 * Client sends 2 integers and 1 operator by keyboard.
 * They are sent to the server, and it makes the calculation.
 * Reads from the server and print the appropriate message.
 */

public class CalcClient {
    private static final int PORT = 5556;

    public static void main(String[] args) {
        try {
            // Initialize variables
            int firstInteger, secondInteger;
            float result;
            String operator, response;
            Scanner scanner = new Scanner(System.in);

            // Connect to server
            Socket sock = new Socket("localhost", PORT);

            // Initialize to streams to read and write from the server's stream
            DataInputStream in = new DataInputStream(sock.getInputStream());
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            System.out.print("Write the first number: ");
            // Read first integer
            firstInteger = scanner.nextInt();

            // Send integer to the server
            out.writeInt(firstInteger);

            System.out.print("Write the second number: ");
            // Read second integer
            secondInteger = scanner.nextInt();

            // Send the integer to the server
            out.writeInt(secondInteger);

            System.out.print("Write the operator: ");
            // Read operator as String
            operator = scanner.next();

            // Send operator to the server
            out.writeUTF(operator);

            // Read responses from the server
            // Result of the calculation
            result = in.readFloat();

            // Message of success or failure
            response = in.readUTF();

            // If message = "DONE", write the answer
            if (Objects.equals(response, "DONE")) {
                System.out.println("Answer is : " + result);
            } else {
                // Else, print error message
                System.out.println("Calculation wasn't possible.");
            }

            // Close Streams and Socket
            in.close();
            out.close();
            sock.close();
            System.out.println("Connection Closing...");
        } catch (ConnectException e) {
            System.out.println("Connection Refused!!! : " + e);
        } catch (NullPointerException e) {
            System.out.println("NULL" + e.getMessage());
        } catch (IOException | InputMismatchException e) {
            System.out.println("You must enter integers!!! : " + e);
        }
    }
}