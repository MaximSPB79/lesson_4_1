package com.example.lesson_4.controller;

import com.example.lesson_4.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ChatController {

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea chatHistory;

    @FXML
    private ListView<String> userList;

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    void inputUsers(MouseEvent event) {

    }

    @FXML
    void sendingMessage() {
        String message = inputField.getText().trim();
        inputField.clear();
        if (message.isBlank()) {
            return;
        }
        network.sendMessage(message);
       // appendMessage(message);
    }

    public void appendMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }

    @FXML
    public void initialise() {
        userList.setItems(FXCollections.observableArrayList("Алексей", "Михаил", "Ольга"));
        sendButton.setOnAction(event -> sendingMessage());
        inputField.setOnAction(event -> sendingMessage());
    }

}








