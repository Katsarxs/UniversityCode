/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import model.Media;
import service.Library;

public final class Server {

    private static final int PORT = 5500;
    private final Library library;

    // Constructor server
    public Server() {
        this.library = new Library();
    }

    // Εκκίνηση server
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    while (true) {
                        Request request = (Request) in.readObject();
                        Response response = handleRequest(request);
                        out.reset();
                        out.writeObject(response);
                        out.flush();
                    }
                } catch (EOFException ignored) {
                    System.out.println("Client disconnected.");
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println("Client connection closed.");
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to start server: " + e.getMessage());
        }
    }

    // Διαχείριση request
    private Response handleRequest(Request request) {
        return switch (request.getType()) {
            case ADD_MEDIA -> {
                boolean added = library.addMedia(request.getMedia());
                yield new Response(added, added ? "Media added successfully." : "Title already exists.");
            }
            case UPDATE_MEDIA -> {
                boolean updated = library.updateMediaPartial(request.getMedia());
                yield new Response(updated, updated ? "Media updated successfully." : "Media not found.");
            }
            case DELETE_MEDIA -> {
                boolean deleted = library.removeMedia(request.getTitle());
                yield new Response(deleted, deleted ? "Media deleted successfully." : "Media not found.");
            }
            case ADD_EPISODE -> {
                boolean added = library.addEpisodeToSeries(request.getTitle(), request.getEpisode());
                yield new Response(added, added ? "Episode added successfully." : "Invalid season or duplicate episode.");
            }
            case REMOVE_EPISODE -> {
                var episode = request.getEpisode();
                boolean removed = library.removeEpisodeFromSeries(
                        request.getTitle(),
                        episode.getSeasonNumber(),
                        episode.getNumber()
                );
                yield new Response(removed, removed ? "Episode removed successfully." : "Episode not found.");
            }
            case SEARCH_TITLE -> {
                List<Media> results = library.searchByTitle(request.getQuery());
                yield new Response(true, "Search completed.", results);
            }
            case SEARCH_CATEGORY -> {
                List<Media> results = library.searchByCategory(request.getQuery());
                yield new Response(true, "Search completed.", results);
            }
            case SAVE_LIBRARY -> {
                boolean saved = library.saveToFile(request.getFilename());
                yield new Response(saved, saved ? "Library saved successfully." : "Failed to save library.");
            }
            case LOAD_LIBRARY -> {
                boolean loaded = library.loadFromFile(request.getFilename());
                yield new Response(loaded, loaded ? "Library loaded successfully." : "Failed to load library.");
            }
        };
    }

    // server
    public static void main(String[] args) {
        new Server().start();
    }
}