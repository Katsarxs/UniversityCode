import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Εκτέλεση socket client για να ακούει από τον Database Server
public class DatabaseClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public Response sendRequest(Request request) {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
        return null;
    }
}