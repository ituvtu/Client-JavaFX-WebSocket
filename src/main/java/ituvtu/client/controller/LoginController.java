package ituvtu.client.controller;

import ituvtu.client.view.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {
    @FXML
    private TextField serverIpField;
    @FXML
    private TextField serverPortField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;


    public void handleLoginButton() {
        String serverIp = serverIpField.getText().trim();
        String serverPort = serverPortField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Login fields must be filled.");
            return;
        }
        if (serverIp.isEmpty()) {
            serverIp="127.0.0.1";
        }
        if (serverPort.isEmpty()) {
            serverPort="12345";
        }

        try {
            int port = Integer.parseInt(serverPort);
            ClientApp.initializeClient(serverIp, port, username, password);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Server port must be a number.");
        } catch (Exception e) {
            showAlert("Connection Error", "Failed to connect to the server. Please check your IP and port.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
