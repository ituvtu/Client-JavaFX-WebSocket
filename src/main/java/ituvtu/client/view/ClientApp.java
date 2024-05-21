package ituvtu.client.view;

import ituvtu.client.controller.*;
import ituvtu.client.model.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.util.Objects;

public class ClientApp extends Application {
    static ClientController controller;
    static Client client;
    private static Stage primaryStage;
    static String username;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientApp.primaryStage = primaryStage;
        showLoginScreen();
    }

    public static void initializeClient() throws URISyntaxException {
        if (client == null) {
            client = Client.getInstance("ws://localhost:12349");
            client.connect();
        } else if (!client.isOpen()) {
            client.reconnect();
        }
    }

    public void showLoginScreen() throws Exception {
        initializeClient();
        if (controller == null) {
            FXMLLoader mainLoader = new FXMLLoader(ClientApp.class.getResource("/ituvtu/client/Client.fxml"));
            mainLoader.load();
            controller = mainLoader.getController();
            controller.setClient(client);
            client.addObserver(controller);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ituvtu/client/Login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setController(controller);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/ituvtu/client/Client.fxml"));
                Parent root = loader.load();
                ClientController mainController = loader.getController();
                mainController.setClient(client);

                Scene scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(ClientApp.class.getResource("/ituvtu/client/client-styles.css")).toExternalForm());
                primaryStage.setTitle("Client of " + username);
                primaryStage.setScene(scene);
                primaryStage.show();
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

    public static ClientController getController() {
        return controller;
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
