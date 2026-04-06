/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package client;

import network.Request;
import network.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {

    // Ροές και socket
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Δημιουργία σύνδεσης
    public ClientConnection(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to server", e);
        }
    }

    // Αποστολή request
    public Response send(Request request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        return (Response) in.readObject();
    }

    // Κλείσιμο σύνδεσης
    public void close() {
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {
        }
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {
        }
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }
}