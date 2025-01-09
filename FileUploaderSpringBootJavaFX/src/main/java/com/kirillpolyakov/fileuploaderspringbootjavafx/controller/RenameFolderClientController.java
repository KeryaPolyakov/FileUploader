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

public class RenameFolderClientController implements ControllerData<String>{

    @FXML
    public TextField textFieldName;
    @FXML
    public Button buttonRenameFolder;

    private UserDirectoryRepository userDirectoryRepository;

    private String path;

    @Override
    public void initData(String data) {
        path = data;
        Preferences preferences = Preferences.userNodeForPackage(App.class);
        userDirectoryRepository = new UserDirectoryRepository(preferences.get("username", ""),
                preferences.get("password", ""));
    }

    public void buttonRenameFolder(ActionEvent actionEvent) {
        if (!textFieldName.getText().isEmpty()) {
            try {
                userDirectoryRepository.updateClient(path, this.textFieldName.getText());
                App.showInfo("info", "Folder is successfully renamed", Alert.AlertType.INFORMATION);
                Stage stage = (Stage) buttonRenameFolder.getScene().getWindow();
                stage.close();
            } catch (IOException | IllegalArgumentException e) {
                App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            App.showInfo("info", "Filed can't be empty", Alert.AlertType.INFORMATION);
        }
    }
}
