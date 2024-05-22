package ituvtu.client.model;

import ituvtu.client.controller.IClientObserver;
import ituvtu.client.xml.UserConnectionInfo;
import ituvtu.client.xml.XMLUtil;
import ituvtu.client.xml.auth.AuthRequest;
import ituvtu.client.xml.message.Message;
import jakarta.xml.bind.JAXBException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class Client extends WebSocketClient {
    private static Client instance;
    private final Set<IClientObserver> observers = new HashSet<>();

    public static Client getInstance(String url) throws URISyntaxException {
        if (instance == null) {
            instance = new Client(url);
        }
        return instance;
    }
    public void clearObservers(){
        observers.clear();
    }
    private Client(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server on port: " + getURI().getPort());
    }

    public void sendConnectionInfo(String username) {
        UserConnectionInfo info = new UserConnectionInfo(username, getURI().getPort());
        try {
            String xmlInfo = XMLUtil.toXML(info);
            send(xmlInfo);
        } catch (JAXBException e) {
            System.err.println("Error serializing connection info: " + e.getMessage());
        }
    }

    public void addObserver(IClientObserver observer) {
        observers.add(observer);

    }

    private void notifyObservers(String message) {
        observers.forEach(observer -> observer.onMessage(message));
    }

    @Override
    public void onMessage(String message) {
        notifyObservers(message);
    }
    public Set<IClientObserver> getObservers() {
        return observers;
    }
    public void sendMessage(String from, String recipient, String content, int chatId) {
        try {
            Message msg = new Message(from, recipient, content, chatId);
            String xmlMessage = XMLUtil.toXML(msg);
            send(xmlMessage);
        } catch (JAXBException e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from the server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error occurred: " + ex.getMessage());
    }

    public void sendAuthRequest(String username, String password) {
        try {
            AuthRequest authRequest = new AuthRequest(username, password);
            String xmlMessage = XMLUtil.toXML(authRequest);
            send(xmlMessage);
            System.out.println(xmlMessage);
        } catch (JAXBException e) {
            System.err.println("Error serializing auth request: " + e.getMessage());
        }
    }
}
