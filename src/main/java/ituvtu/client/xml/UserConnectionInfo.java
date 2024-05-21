package ituvtu.client.xml;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserConnectionInfo {
    private String username;
    private int port;

    public UserConnectionInfo() {
    }

    public UserConnectionInfo(String username, int port) {
        this.username = username;
        this.port = port;
    }

}



