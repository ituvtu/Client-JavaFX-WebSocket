package ituvtu.client.model;

import ituvtu.client.controller.IClientObserver;

import java.util.Set;

@SuppressWarnings({"unused", "RedundantThrows"})
public interface IClient {
    void connect() throws InterruptedException;
    void addObserver(IClientObserver observer);
    void clearObservers();
    void sendMessage(String from, String recipient, String content, int chatId);
    void sendAuthRequest(String username, String password);
    void sendConnectionInfo(String username);
    Set<IClientObserver> getObservers();
    boolean connectBlocking() throws InterruptedException;
    boolean isOpen();
    void close();
    void send(String requestXml);
}

