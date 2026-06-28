import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Object obj = in.readObject();

            if (!(obj instanceof Request request)) {
                out.writeObject(new Response(false, "Invalid request", null));
                out.flush();
                return;
            }

            Response response = DatabaseService.handle(request);

            out.writeObject(response);
            out.flush();

        } catch (EOFException e) {
            System.out.println("Client disconnected normally");
        } catch (Exception e) {
            System.out.println("ClientHandler error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}