import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CentralDatabaseServer {

    private static final int PORT = 5000;
    private final ExecutorService pool = Executors.newFixedThreadPool(10); // Thread pool με 10 threads fixed

    public void start() throws IOException {
        FileManager.loadAll();
        // Εκκίνηση socket server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Central Database Server Started");

            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new ClientHandler(socket));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new CentralDatabaseServer().start();
    }
}