package com.kirillpolyakov.fileuploaderspringbootjavafx.controller;

import com.kirillpolyakov.fileuploaderspringbootjavafx.App;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.SimpleUser;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.SimpleUserRepository;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    @FXML
    public Button buttonSignUp;
    @FXML
    public Button buttonSignIn;
    @FXML
    public TextField textFieldPassword;
    @FXML
    public TextField textFieldUserName;
    @FXML
    public TextField textFieldFIO;
    @FXML
    public void buttonSignUp(ActionEvent actionEvent) {
        String userName = textFieldUserName.getText();
        String password = textFieldPassword.getText();
        String fio = textFieldFIO.getText();
        if (userName.isEmpty() || password.isEmpty() || fio.isEmpty()) {
            App.showInfo("Error", "Все поля должны быть заполнены", Alert.AlertType.ERROR);
        } else if (Util.notDigitsOrLetters(userName) || Util.notLetters(fio)){
            App.showInfo("Error", "Фамилия имя и отчество должны состоять только из букв, " +
                    "логин только из букв и цифр", Alert.AlertType.ERROR);
        } else {
            try {
                new SimpleUserRepository().post(new SimpleUser(userName, password, fio));
                App.showInfo("Info", "Пользователь успешно добавлен", Alert.AlertType.INFORMATION);
                Stage stage = (Stage) buttonSignUp.getScene().getWindow();
                stage.close();
                App.openWindow("authorisation.fxml", "Authorisation", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void buttonSignIn(ActionEvent actionEvent) {
        try {
            App.openWindow("authorisation.fxml", "Authorisation", null);
            Stage stage = (Stage) buttonSignIn.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
