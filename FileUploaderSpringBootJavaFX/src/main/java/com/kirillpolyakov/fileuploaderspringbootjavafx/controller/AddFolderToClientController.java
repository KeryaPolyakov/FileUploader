package com.kirillpolyakov.fileuploaderspringbootjavafx.controller;

import com.kirillpolyakov.fileuploaderspringbootjavafx.App;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.UserDirectoryRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class AddFolderToClientController implements ControllerData<String>{

    @FXML
    public TextField textFieldName;
    @FXML
    public Button buttonAddFolder;
    private UserDirectoryRepository userDirectoryRepository;
    private String path;

    @Override
    public void initData(String data) {
        path = data;
        Preferences preferences = Preferences.userNodeForPackage(App.class);
        userDirectoryRepository = new UserDirectoryRepository(preferences.get("username", ""),
                preferences.get("password", ""));
    }

    @FXML
    public void buttonAddFolder(ActionEvent actionEvent) {
        if (!textFieldName.getText().isEmpty()) {
            try {
                userDirectoryRepository.addToClient(path, this.textFieldName.getText());
                App.showInfo("info", "Folder is successfully added", Alert.AlertType.INFORMATION);
                Stage stage = (Stage) buttonAddFolder.getScene().getWindow();
                stage.close();
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
                App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            App.showInfo("info", "Filed can't be empty", Alert.AlertType.INFORMATION);
        }
    }
}
