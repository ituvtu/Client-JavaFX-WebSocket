module ituvtu.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.java_websocket;
    requires jakarta.xml.bind;
    requires javafx.graphics;
    requires javafx.base;

    opens ituvtu.client to javafx.fxml;
    opens ituvtu.client.xml.auth to javafx.fxml,jakarta.xml.bind;
    opens ituvtu.client.xml.chat to javafx.fxml,jakarta.xml.bind;
    opens ituvtu.client.xml.message to javafx.fxml,jakarta.xml.bind;
    opens ituvtu.client.xml.auxiliary to javafx.fxml,jakarta.xml.bind;
    opens ituvtu.client.xml to javafx.fxml,jakarta.xml.bind;
    opens ituvtu.client.controller to javafx.fxml;
    opens ituvtu.client.chat to javafx.fxml;
    opens ituvtu.client.view to javafx.fxml;
    exports ituvtu.client.xml;
    exports ituvtu.client.xml.chat;
    exports ituvtu.client.xml.auth;
    exports ituvtu.client.xml.auxiliary;
    exports ituvtu.client.xml.message;
    exports ituvtu.client.view;
    exports ituvtu.client.controller;
    exports ituvtu.client.model;
    exports ituvtu.client.chat;

}