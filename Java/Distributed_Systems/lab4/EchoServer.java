package lab4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Echo server using threads.
 * We create a server socket and wait for a connection.
 * If the server accepts the connection, runs EchoThread to handle the echo logic.
 */

public class EchoServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5555, 0);
            Thread echoThread;
            System.out.println("Address : " + serverSocket.getInetAddress() + "\nPort : " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.toString());
                System.out.println("Address : " + socket.getInetAddress() + "\nPort : " + socket.getLocalPort());
                echoThread = new Thread(new EchoThread(socket));
                echoThread.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class EchoThread implements Runnable {
    private Socket socket;

    public EchoThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter outstream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Local Address : " + socket.getLocalAddress() + "\nLocal Port : " + socket.getLocalPort());
            System.out.println("Address : " + socket.getInetAddress() + "\nPort : " + socket.getPort());

            String inString = instream.readLine();
            System.out.println("Client says : " + inString);
            if (inString.equals("Hello")) {
                outstream.write("Welcome\n");
                outstream.flush();

                do {
                    inString = instream.readLine();
                    System.out.println("Client says : " + inString);
                    outstream.write(inString + "\n");
                    outstream.flush();
                    System.out.println("The server says : " + inString);
                } while (!inString.equals("bye"));
            } else {
                outstream.write("Not welcomed...\n");
                outstream.flush();
            }
            instream.close();
            outstream.close();
            socket.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}