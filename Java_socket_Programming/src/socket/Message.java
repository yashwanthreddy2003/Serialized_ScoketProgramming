package socket;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures compatibility for serialization
    private String sender;
    private String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
