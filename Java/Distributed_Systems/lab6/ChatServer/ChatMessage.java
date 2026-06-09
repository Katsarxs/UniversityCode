package lab6.ChatServer;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private final String name;
    private final String msg;
    private final Date date;

    public ChatMessage(String name, String msg) {
        // Variable assignment and date update
        this.name = name;
        this.msg = msg;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return msg;
    }

    public Date getDate() {
        return date;
    }
}