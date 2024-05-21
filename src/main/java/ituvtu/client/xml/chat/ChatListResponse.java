package ituvtu.client.xml.chat;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class ChatListResponse {
    private List<Chat> chats;

    @SuppressWarnings("unused")
    public ChatListResponse() {
        // JAXB requires a constructor with no arguments
    }

    public ChatListResponse(List<Chat> chats) {
        this.chats = chats;
    }

    @XmlElement
    public List<Chat> getChats() {
        return chats;
    }

}
