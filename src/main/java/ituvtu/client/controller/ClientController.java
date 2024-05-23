package ituvtu.client.controller;

import ituvtu.client.chat.*;
import ituvtu.client.model.*;
import ituvtu.client.util.*;
import ituvtu.client.view.*;
import ituvtu.client.xml.*;
import ituvtu.client.xml.auth.*;
import jakarta.xml.bind.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.util.*;
import ituvtu.client.xml.chat.*;
import ituvtu.client.xml.message.*;
import java.io.*;
import java.time.*;
import java.util.*;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class ClientController implements IClientObserver, IClientController {
    private static ClientController instance;
    @FXML
    private ListView<ChatDisplayData> chatListView;
    @FXML
    private VBox messagesArea;
    @FXML
    private TextField inputField;
    @FXML
    private TextArea logMessagesArea;
    @FXML
    private ScrollPane scrollPane;
    private Client client;
    public TextField newChatUsername;
    private LocalDate currentDisplayedDate = null;
    private int currentChatId = -1;

    public ClientController() {}
    @FXML
    public void initialize() {
        chatListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChatDisplayData> call(ListView<ChatDisplayData> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatDisplayData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });

        String stylesheet = Objects.requireNonNull(getClass().getResource("/ituvtu/client/client-styles.css")).toExternalForm();
        scrollPane.getStylesheets().add(stylesheet);
        chatListView.getStylesheets().add(stylesheet);
        logMessagesArea.getStylesheets().add(stylesheet);

        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentChatId = newSelection.chatId();
                loadChatMessages(newSelection.chatId());
            }
        });

        // Add scroll event listener to the scroll pane
        scrollPane.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vvalue = scrollPane.getVvalue();
            scrollPane.setVvalue(vvalue - deltaY / width);
        });

        // Optional: To make the scroll more smooth and natural
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });

    }

    private void loadChatMessages(int chatId) {
        messagesArea.getChildren().clear();
        try {
            ChatRequest request = new ChatRequest("getMessages", chatId, ClientApp.getUsername());
            String requestXml = XMLUtil.toXML(request);
            client.send(requestXml);
        } catch (JAXBException e) {
            displayLogMessage("Error requesting chat messages: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String xmlMessage) {
        Platform.runLater(() -> {
            if (xmlMessage.contains("<chatListResponse>")) {
                processChatListResponse(xmlMessage);
            } else if (xmlMessage.contains("<message>")) {
                processMessage(xmlMessage);
            } else if (xmlMessage.contains("<authResponse")) {
                processAuthResponse(xmlMessage);
            } else if (xmlMessage.contains("<messagesResponse>")) {
                processMessagesResponse(xmlMessage);
            } else {
                if (!xmlMessage.contains("<messagesResponse/>") && !xmlMessage.contains("<chatListResponse/>")) {
                    displayLogMessage(xmlMessage);
                }
            }
        });
    }

    private void processAuthResponse(String xmlMessage) {
        try {
            AuthResponse response = XMLUtil.fromXML(xmlMessage, AuthResponse.class);
            if (response.isAuthenticated()) {
                Platform.runLater(() -> {
                    try {
                        ClientApp.setUsername(response.getUsername());
                        ClientApp.showMainScreen();
                        client.sendConnectionInfo(ClientApp.getUsername());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                showAlert("Authentication Failed", "Please check your username and password.");
            }
        } catch (JAXBException e) {
            showAlert("Error", "Error parsing authentication response: " + e.getMessage());
        }
    }

    private void processChatListResponse(String xmlMessage) {
        try {
            ChatListResponse response = XMLUtil.fromXML(xmlMessage, ChatListResponse.class);
            updateChatList(response.getChats());
        } catch (Exception e) {
            displayLogMessage("Error parsing chat list: " + e.getMessage());
        }
    }

    public void updateChatList(List<Chat> chats) {
        Platform.runLater(() -> {
            chatListView.getItems().clear();
            for (Chat chat : chats) {
                chatListView.getItems().add(new ChatDisplayData(chat.getChat_id(), chat.getChatDisplayName(ClientApp.getUsername()), chat.getUsernameFirst(), chat.getUsernameSecond()));
            }
        });
    }

    private void processMessage(String xmlMessage) {
        try {
            Message message = XMLUtil.fromXML(xmlMessage, Message.class);
            ChatDisplayData selectedChat = chatListView.getSelectionModel().getSelectedItem();
            if (selectedChat != null && message.getChatId() == selectedChat.chatId()) {
                displayMessage(message);
            }
        } catch (Exception e) {
            displayLogMessage("Error parsing XML: " + e.getMessage());
        }
    }

    private void processMessagesResponse(String xmlMessage) {
        try {
            JAXBContext context = JAXBContext.newInstance(MessagesResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlMessage);
            MessagesResponse response = (MessagesResponse) unmarshaller.unmarshal(reader);
            updateMessagesArea(response.getMessages());
        } catch (Exception e) {
            displayLogMessage("Error parsing messages: " + e.getMessage());
        }
    }

    private void updateMessagesArea(List<Message> messages) {
        messagesArea.getChildren().clear();
        currentDisplayedDate = null;
        for (Message message : messages) {
            if (currentChatId == message.getChatId()) {
                displayMessage(message);
            }
        }
    }

    public void displayMessage(Message message) {
        LocalDateTime timestamp = message.getTimestamp();
        LocalDate messageDate = timestamp.toLocalDate();

        if (currentDisplayedDate == null || !currentDisplayedDate.equals(messageDate)) {
            currentDisplayedDate = messageDate;
            messagesArea.getChildren().add(UIFactory.createDateLabel(messageDate));
        }

        messagesArea.getChildren().add(UIFactory.createMessageBox(message));
    }

    public void displayLogMessage(String text) {
        logMessagesArea.appendText(text + "\n");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setChatList(List<String> chats) {
        Platform.runLater(() -> {
            chatListView.getItems().clear();
            int chatId = 1;
            for (String chat : chats) {
                chatListView.getItems().add(new ChatDisplayData(chatId++, chat, null, null));
            }
        });
    }

    public void connectToServer(String serverIp, int serverPort, String username, String password) throws Exception {
        String serverUrl = "ws://" + serverIp + ":" + serverPort;
        client = Client.getInstance(serverUrl);
        setClient(client);

        if (client.connectBlocking()) {
            client.sendAuthRequest(username, password);
        } else {
            throw new Exception("Failed to connect to the server.");
        }
    }

    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            client.addObserver(this);
        }
    }

    public void clearObservers() {
        client.clearObservers();
    }

    @FXML
    public void requestUserChats() {
        if (client != null && client.isOpen()) {
            try {
                ChatRequest request = new ChatRequest("getChats", ClientApp.getUsername(), null);
                String requestXml = XMLUtil.toXML(request);
                client.send(requestXml);
            } catch (JAXBException e) {
                displayLogMessage("Error creating chat list request: " + e.getMessage());
            }
        } else {
            displayLogMessage("Connection is not established. Please connect to the server first.");
        }
    }

    @FXML
    public void onSend() {
        if (client != null && client.isOpen()) {
            String messageContent = inputField.getText().trim();
            if (!messageContent.isEmpty()) {
                ChatDisplayData selectedChat = chatListView.getSelectionModel().getSelectedItem();
                if (selectedChat != null) {
                    int chatId = selectedChat.chatId();
                    Message message = new Message(ClientApp.getUsername(), selectedChat.displayName(), messageContent, chatId);
                    client.sendMessage(ClientApp.getUsername(), selectedChat.displayName(), messageContent, chatId);
                    inputField.clear();
                    displayMessage(message);
                } else {
                    displayLogMessage("Select a chat to send the message.");
                }
            } else {
                displayLogMessage("Message cannot be empty.");
            }
        } else {
            displayLogMessage("No client connected.");
        }
    }

    @FXML
    public void createNewChat() {
        System.out.println("Creating new chat");
        String username2 = newChatUsername.getText().trim();
        if (!username2.isEmpty()) {
            try {
                ChatRequest chatRequest = new ChatRequest("createChat", ClientApp.getUsername(), username2);
                String chatRequestXml = XMLUtil.toXML(chatRequest);
                if (chatRequestXml != null) {
                    client.send(chatRequestXml);
                    requestUserChats();
                } else {
                    displayLogMessage("Failed to create XML request.\n");
                }
            } catch (Exception e) {
                displayLogMessage("Error creating XML request: " + e.getMessage() + "\n");
            }
            newChatUsername.clear();
        } else {
            displayLogMessage("Please enter a valid username.\n");
        }
    }

    private void initiateNewChat(int chatId, String username) {
        ChatDisplayData newChat = new ChatDisplayData(chatId, username, null, null);
        Platform.runLater(() -> {
            chatListView.getItems().add(newChat);
            logMessagesArea.appendText("New chat started with " + username + ".\n");
        });
    }

    @FXML
    public void deleteChat() {
        final int selectedIdx = chatListView.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            ChatDisplayData selectedChat = chatListView.getItems().get(selectedIdx);
            try {
                ChatRequest deleteRequest = new ChatRequest("deleteChat", selectedChat.chatId());
                String requestXml = XMLUtil.toXML(deleteRequest);
                client.send(requestXml);
                chatListView.getItems().remove(selectedIdx);
                displayLogMessage("Request to delete chat with " + selectedChat.displayName() + " sent.\n");
            } catch (JAXBException e) {
                displayLogMessage("Error creating XML for delete chat request: " + e.getMessage());
            }
        } else {
            displayLogMessage("Please select a chat to delete.\n");
        }
    }

    public void sendAuthInfo(String username, String password) {
        client.sendAuthRequest(username, password);
    }

    public Client getClient() {
        return client;
    }
}
