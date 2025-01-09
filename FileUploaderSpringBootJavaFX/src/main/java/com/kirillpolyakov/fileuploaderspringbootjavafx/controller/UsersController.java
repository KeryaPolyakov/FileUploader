package com.kirillpolyakov.fileuploaderspringbootjavafx.controller;

import com.kirillpolyakov.fileuploaderspringbootjavafx.App;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.SimpleUser;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.User;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.SimpleUserRepository;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Util;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class UsersController {

    @FXML
    public ListView<SimpleUser> listViewUsers;
    @FXML
    public Button buttonExit;

    private Preferences preferences;

    public void initialize(){
        preferences = Preferences.userNodeForPackage(App.class);
        try {
            listViewUsers.setItems(FXCollections.observableList(
                    new SimpleUserRepository(preferences.get("username", ""), preferences.get("password", ""))
                            .get()));
            listViewUsers.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2) {
                    User user = listViewUsers.getSelectionModel().getSelectedItem();
                    try {
                        if (user != null) {
                            Stage stage = (Stage) listViewUsers.getScene().getWindow();
                            stage.close();
                            App.openWindow("main.fxml", "Main", user);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buttonExit(ActionEvent actionEvent) {
        Util.exit(buttonExit);
    }
}
