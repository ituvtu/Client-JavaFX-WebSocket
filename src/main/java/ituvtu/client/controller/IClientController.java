package ituvtu.client.controller;

import ituvtu.client.model.IClient;
import ituvtu.client.xml.chat.Chat;
import ituvtu.client.xml.message.Message;
import java.util.List;

@SuppressWarnings("unused")
public interface IClientController extends IClientObserver {
    void setClient(IClient client);
    void clearObservers();
    void requestUserChats();
    void onSend();
    void createNewChat();
    void deleteChat();
    void sendAuthInfo(String username, String password);
    void displayMessage(Message message);
    void displayLogMessage(String text);
    void updateChatList(List<Chat> chats);
}

