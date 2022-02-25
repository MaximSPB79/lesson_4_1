package com.example.lesson_4;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;

public class HelloController {

    @FXML
    private TextArea message;

    @FXML
    private TextArea inputString;

    @FXML
    private ListView<String> users;


    @FXML
    private Button buttonPush;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void print(DragEvent event) {

    }

    @FXML
    void initialize() {

    }
    @FXML
    void sendingMessage() {
        message.appendText(inputString.getText() + "\n");
    }
    @FXML
    private TextArea listUsers;


    @FXML
    void printUser(MouseEvent event) {

    }


    @FXML
    void inputUsers() {

       listUsers.appendText("Петя \n");
       listUsers.appendText("Оля \n");
       listUsers.appendText("Саша \n");
    }

    @FXML
    private Button usersAll;
}
