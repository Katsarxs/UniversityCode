/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package network;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import model.Media;

public final class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final String message;
    private final List<Media> data;

    // Constructor απάντησης
    public Response(boolean success, String message) {
        this(success, message, List.of());
    }

    // Constructor με δεδομένα
    public Response(boolean success, String message, List<Media> data) {
        this.success = success;
        this.message = message;
        this.data = data == null ? List.of() : List.copyOf(data);
    }

    public boolean isSuccess() {
        return success;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public List<Media> getData() {
        return data;
    }
}