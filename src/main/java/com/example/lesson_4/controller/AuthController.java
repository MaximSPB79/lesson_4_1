package com.example.lesson_4.controller;

import com.example.lesson_4.StartClient;
import com.example.lesson_4.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    private Network network;
    private StartClient startClient;

    @FXML
    public void checkAuth() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if (login.length() == 0 || password.length() == 0) {
            // сделать всплывающее окошко (4 урок)
            startClient.showErrorAlert("Ошибка ввода при аутентификации", "Поля не должны быть пустыми");
            return;
        }
        String authErrorMessage = network.sendAuthMessage(login, password);
        if (authErrorMessage == null) {
            startClient.openChatDialog();
        } else {
            startClient.showErrorAlert("Ошибка аутентификации", authErrorMessage);
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }


    public void setStartClient(StartClient startClient) {
        this.startClient = startClient;
    }
}
