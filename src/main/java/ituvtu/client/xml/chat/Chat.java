package ituvtu.client.xml.chat;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Chat {
    private int chat_id;
    private String usernameFirst;
    private String usernameSecond;

    @SuppressWarnings("unused")
    public Chat() {
        // JAXB requires a constructor with no arguments
    }

    public Chat(int chat_id, String usernameFirst, String usernameSecond) {
        this.chat_id = chat_id;
        this.usernameFirst = usernameFirst;
        this.usernameSecond = usernameSecond;
    }

    @XmlElement
    public int getChat_id() {
        return chat_id;
    }

    @XmlElement
    public String getUsernameFirst() {
        return usernameFirst;
    }

    @XmlElement
    public String getUsernameSecond() {
        return usernameSecond;
    }

    public String getChatDisplayName(String currentUser) {
        if (usernameFirst.equals(currentUser)) {
            return usernameSecond;
        } else {
            return usernameFirst;
        }
    }

    @Override
    public String toString() {
        return "Chat between " + usernameFirst + " and " + usernameSecond;
    }
}

