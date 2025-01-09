package com.kirillpolyakov.fileuploaderspringbootjavafx.controller;

import com.kirillpolyakov.fileuploaderspringbootjavafx.App;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.User;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.UserRepository;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class AuthorisationController {
    @FXML
    public Button buttonSignIn;
    @FXML
    public TextField textFieldLogin;
    @FXML
    public TextField textFieldPassword;
    @FXML
    public Button buttonSignUp;
    @FXML
    public void buttonSignIn(ActionEvent actionEvent) {
        String username = textFieldLogin.getText();
        String password = textFieldPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            App.showInfo("Error", "Все поля должны быть заполнены", Alert.AlertType.ERROR);
        } else {
            try {
                User user = new UserRepository(username, password).authenticate();
                Preferences preferences = Preferences.userNodeForPackage(App.class);
                long id = user.getId();
                preferences.putLong(Constants.PREFERENCE_KEY_ID, id);
                preferences.put(Constants.PREFERENCE_KEY_USERNAME, username);
                preferences.put(Constants.PREFERENCE_KEY_PASSWORD, password);
                if (user.getClass().getSimpleName().equals("SimpleUser")) {
                    App.openWindow("main.fxml", "Main", user);
                } else {
                    App.openWindow("users.fxml", "Users", null);
                }
                Stage stage = (Stage) buttonSignIn.getScene().getWindow();
                stage.close();
            } catch (IOException | IllegalArgumentException e) {
                App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void buttonSignUp(ActionEvent actionEvent) {
        try {
            App.openWindow("registration.fxml", "Registration", null);
            Stage stage = (Stage) buttonSignUp.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
