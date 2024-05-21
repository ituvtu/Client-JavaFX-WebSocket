package ituvtu.client.xml.message;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ituvtu.client.xml.auxiliary.*;
import java.time.LocalDateTime;

@XmlRootElement
public class Message {
    private String from;
    private String to;
    private String content;
    private LocalDateTime timestamp;
    private int chatId;

    // Конструктор
    public Message() {}

    public Message(String from, String to, String content, int chatId) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.chatId = chatId;
    }

    // Геттери і сеттери
    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getChatId() {
        return chatId;
    }

}

