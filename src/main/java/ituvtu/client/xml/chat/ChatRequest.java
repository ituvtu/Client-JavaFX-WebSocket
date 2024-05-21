package ituvtu.client.xml.chat;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Map;

@XmlRootElement
public class ChatRequest {
    private String action;
    private String username1;
    private String username2;
    private int chatId;
    private Map<String, String> parameters; // Options to update

    public ChatRequest(){
        // JAXB requires a constructor with no arguments
    }
    public ChatRequest(String action, int chatId, String username2){
        this.action = action;
        this.chatId = chatId;
        this.username1 = null;
        this.username2 = username2;

    }
    public ChatRequest(String action, String username1, String username2){
        this.action = action;
        this.username1 = username1;
        this.username2 = username2;

    }

    public ChatRequest(String deleteChat, int chatId) {
        this.action = deleteChat;
        this.chatId = chatId;
    }

}
