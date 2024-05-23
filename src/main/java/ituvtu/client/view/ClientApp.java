package ituvtu.client.view;

import ituvtu.client.controller.IClientController;
import ituvtu.client.controller.ClientController;
import ituvtu.client.controller.LoginController;
import ituvtu.client.model.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class ClientApp extends Application {
    private static IClientController clientController;
    private static IClient client; // Використання інтерфейсу IClient
    private static Stage primaryStage;
    private static String username;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientApp.primaryStage = primaryStage;
        showLoginScreen();
    }

    public static void initializeClient(String serverIp, int serverPort, String username, String password) throws URISyntaxException, InterruptedException {
        String serverUrl = "ws://" + serverIp + ":" + serverPort;
        client = Client.getInstance(serverUrl); // Повертається інтерфейс IClient
        if (client.connectBlocking()) {
            if (clientController == null) {
                clientController = new ClientController();
            }
            clientController.setClient(client);
            client.addObserver(clientController);
            clientController.sendAuthInfo(username, password);
        } else {
            throw new InterruptedException("Failed to connect to the server.");
        }
    }

    public void showLoginScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ituvtu/client/Login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        System.out.println("Login: " + loginController);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainScreen() {
        Platform.runLater(() -> {
            try {
                clientController.clearObservers();
                FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/ituvtu/client/Client.fxml"));
                Parent root = loader.load();
                IClientController mainController = loader.getController();
                mainController.setClient(client);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(ClientApp.class.getResource("/ituvtu/client/client-styles.css")).toExternalForm());
                primaryStage.setTitle("Client of " + username);
                primaryStage.setScene(scene);
                primaryStage.show();
                clientController.requestUserChats();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void setUsername(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }

    @SuppressWarnings("unused")
    public IClient getClient() {
        return client;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Application is stopping.");
        if (client != null && client.isOpen()) {
            client.close();
        }
        super.stop();
    }
}
