package com.kirillpolyakov.fileuploaderspringbootjavafx.controller;

import com.kirillpolyakov.fileuploaderspringbootjavafx.App;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.Type;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.User;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.UserFile;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.UserDirectoryRepository;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.UserFileRepository;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.UserRepository;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Util;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.prefs.Preferences;

public class MainController implements ControllerData<User> {
    @FXML
    public ListView<UserFile> listViewServerFilesAndDirs;
    @FXML
    public ListView<UserFile> listViewClientFilesAndDirs;
    @FXML
    public Label labelServerPath;
    @FXML
    public Label labelClientPath;
    @FXML
    public Button buttonBackToUsers;
    @FXML
    public ImageView buttonDownLoad;
    @FXML
    public ImageView buttonUpload;
    @FXML
    public Button buttonExit;

    private User user;

    private UserFileRepository userFileRepository;

    private UserDirectoryRepository userDirectoryRepository;

    private Preferences preferences;

    @FXML
    @Override
    public void initData(User data) {
        user = data;
        labelClientPath.setMaxWidth(300);
        preferences = Preferences.userNodeForPackage(App.class);
        try {
            if (!new UserRepository(preferences.get("username", ""),
                    preferences.get("password", "")).authenticate().getClass().getSimpleName().equals("Admin")) {
                buttonBackToUsers.setVisible(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userFileRepository = new UserFileRepository(preferences.get("username", ""),
                preferences.get("password", ""));
        userDirectoryRepository = new UserDirectoryRepository(preferences.get("username", ""),
                preferences.get("password", ""));
        labelClientPath.setText("C:\\fileUploaderClient");
        try {
            listViewServerFilesAndDirs.setItems(FXCollections.observableList(
                    userFileRepository.getInfo(String.valueOf(user.getId()))));
            listViewClientFilesAndDirs.setItems(FXCollections.observableList(userFileRepository.getInfoClient("C:\\fileUploaderClient")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        listViewServerFilesAndDirs.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                UserFile userFile = listViewServerFilesAndDirs.getSelectionModel().getSelectedItem();
                if (userFile != null) {
                    if (userFile.getName().equals("...")) {
                        String[] split = labelServerPath.getText().split("\\\\");
                        if (split.length > 1) {
                            StringJoiner stringJoiner = new StringJoiner("\\");
                            for (int i = 0; i < split.length - 1; i++) {
                                stringJoiner.add(split[i]);
                            }
                            try {
                                listViewServerFilesAndDirs.setItems(FXCollections.observableList(
                                        userFileRepository.getInfo(data.getId() + "\\" + stringJoiner)));
                                labelServerPath.setText(stringJoiner.toString());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        if (userFile.getType().equals(Type.DIRECTORY)) {
                            try {
                                listViewServerFilesAndDirs.setItems(FXCollections.observableList(
                                        userFileRepository.getInfo(data.getId() + "\\" + labelServerPath.getText()
                                                + "\\" + userFile.getName())));
                                labelServerPath.setText(labelServerPath.getText() + "\\" + userFile.getName());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });
        listViewClientFilesAndDirs.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                UserFile userFile = listViewClientFilesAndDirs.getSelectionModel().getSelectedItem();
                if (userFile != null) {
                    if (userFile.getName().equals("...")) {
                        String[] split = labelClientPath.getText().split("\\\\");
                        if (split.length > 2) {
                            StringJoiner stringJoiner = new StringJoiner("\\");
                            for (int i = 0; i < split.length - 1; i++) {
                                stringJoiner.add(split[i]);
                            }
                            try {
                                listViewClientFilesAndDirs.setItems(FXCollections.observableList(
                                        userFileRepository.getInfoClient(stringJoiner.toString())));
                                labelClientPath.setText(stringJoiner.toString());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        if (userFile.getType().equals(Type.DIRECTORY)) {
                            try {
                                listViewClientFilesAndDirs.setItems(FXCollections.observableList(
                                        userFileRepository.getInfoClient(labelClientPath.getText() + File.separator + userFile.getName())));
                                labelClientPath.setText(labelClientPath.getText() + "\\" + userFile.getName());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });
        ContextMenu contextMenu = new ContextMenu();

        MenuItem rename = new MenuItem("Переименовать");
        MenuItem delete = new MenuItem("Удалить");

        contextMenu.getItems().addAll(rename, delete);

        listViewServerFilesAndDirs.setContextMenu(contextMenu);

        rename.setOnAction(event -> {
            UserFile userFile = listViewServerFilesAndDirs.getSelectionModel().getSelectedItem();
            if (userFile != null && !userFile.getName().equals("...")) {
                if (userFile.getType().equals(Type.DIRECTORY)) {
                    try {
                        String pathToChange = user.getId() + "\\" + labelServerPath.getText() +
                                "\\" + userFile.getName();
                        App.openWindowAndWaitModal("renameFolder.fxml", "rename folder", pathToChange);
                        listViewServerFilesAndDirs.setItems(FXCollections.observableList(
                                userFileRepository.getInfo(user.getId() + "\\" + labelServerPath.getText())));
                    } catch (IOException e) {
                        e.printStackTrace();
                        App.showInfo("info", e.getMessage(), Alert.AlertType.INFORMATION);
                    }
                } else {
                    App.showInfo("info", "You can rename only directories",
                            Alert.AlertType.INFORMATION);
                }
            }
        });

        delete.setOnAction(event -> {
            UserFile userFile = listViewServerFilesAndDirs.getSelectionModel().getSelectedItem();
            if (userFile != null && !userFile.getName().equals("...")) {
                if (userFile.getType().equals(Type.DIRECTORY)) {
                    try {
                        userDirectoryRepository.delete(user.getId() + "\\" + labelServerPath.getText()
                                + "\\" + userFile.getName());
                        App.showInfo("info", "Directory is successfully deleted",
                                Alert.AlertType.INFORMATION);
                        listViewServerFilesAndDirs.setItems(FXCollections.observableList(
                                userFileRepository.getInfo(user.getId() + "\\" + labelServerPath.getText())));
                    } catch (IOException e) {
                        App.showInfo("info", e.getMessage(), Alert.AlertType.INFORMATION);
                    }
                } else {
                    App.showInfo("info", "You can delete only directories", Alert.AlertType.INFORMATION);
                }
            }
        });

        buttonDownLoad.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                UserFile userFile = listViewServerFilesAndDirs.getSelectionModel().getSelectedItem();
                if (userFile != null && !userFile.getName().equals("...")) {
                    try {
                        if (userFile.getType().equals(Type.DIRECTORY)) {
                            userDirectoryRepository.download(user.getId() + "\\" + labelServerPath.getText()
                                            + "\\" + userFile.getName(),
                                    "C:\\fileUploaderClient", userFile.getName());
                            App.showInfo("Info", "Directory is successfully downloaded",
                                    Alert.AlertType.INFORMATION);
                        } else {
                            userFileRepository.download(user.getId() + "\\" + labelServerPath.getText()
                                            + "\\" + userFile.getName(),
                                    "C:\\fileUploaderClient", userFile.getName());
                            App.showInfo("Info", "File is successfully downloaded",
                                    Alert.AlertType.INFORMATION);
                        }
                        listViewClientFilesAndDirs.setItems(FXCollections.observableList(userFileRepository
                                .getInfoClient(labelClientPath.getText())));

                    } catch (IOException e) {
                        e.printStackTrace();
                        App.showInfo("Info", e.getMessage(),
                                Alert.AlertType.ERROR);
                    }
                }
            }
        });

        ContextMenu contextMenuClient = new ContextMenu();

        MenuItem renameClient = new MenuItem("Переименовать");
        MenuItem deleteClient = new MenuItem("Удалить");

        contextMenuClient.getItems().addAll(renameClient, deleteClient);

        listViewClientFilesAndDirs.setContextMenu(contextMenuClient);

        renameClient.setOnAction(event -> {
            UserFile userFile = listViewClientFilesAndDirs.getSelectionModel().getSelectedItem();
            if (userFile != null && !userFile.getName().equals("...")) {
                if (userFile.getType().equals(Type.DIRECTORY)) {
                    try {
                        String pathToChange = labelClientPath.getText() + "\\" + userFile.getName();
                        App.openWindowAndWaitModal("renameFolderClient.fxml", "rename folder", pathToChange);
                        listViewClientFilesAndDirs.setItems(FXCollections.observableList(
                                userFileRepository.getInfoClient(labelClientPath.getText())));
                    } catch (IOException e) {
                        e.printStackTrace();
                        App.showInfo("info", e.getMessage(), Alert.AlertType.INFORMATION);
                    }
                } else {
                    App.showInfo("info", "You can rename only directories", Alert.AlertType.INFORMATION);
                }
            }
        });

        deleteClient.setOnAction(event -> {
            UserFile userFile = listViewClientFilesAndDirs.getSelectionModel().getSelectedItem();
            if (userFile != null && !userFile.getName().equals("...")) {
                if (userFile.getType().equals(Type.DIRECTORY)) {
                    try {
                        userDirectoryRepository.deleteClient(labelClientPath.getText()
                                + File.separator + userFile.getName());
                        App.showInfo("info", "Directory is successfully deleted", Alert.AlertType.INFORMATION);
                        listViewClientFilesAndDirs.setItems(FXCollections.observableList(
                                userFileRepository.getInfoClient(labelClientPath.getText())));
                    } catch (IOException e) {
                        App.showInfo("info", e.getMessage(), Alert.AlertType.INFORMATION);
                    }
                } else {
                    App.showInfo("info", "You can delete only directories", Alert.AlertType.INFORMATION);
                }
            }
        });

        buttonUpload.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                UserFile userFile = listViewClientFilesAndDirs.getSelectionModel().getSelectedItem();
                if (userFile != null && !userFile.getName().equals("...")) {
                    File root = new File("C:\\root");
                    root.mkdirs();
                    File file = new File(labelClientPath.getText() + "\\" + userFile.getName());
                    try {
                        if (userFile.getType().equals(Type.DIRECTORY)) {
                            try (ZipFile zipFile = new ZipFile(new File(root.getAbsolutePath(),
                                    file.getName() + ".zip"))) {
                                zipFile.addFolder(file);
                                userDirectoryRepository.upload(zipFile.getFile(), String.valueOf(user.getId()));
                                App.showInfo("Info", "Directory is successfully uploaded",
                                        Alert.AlertType.INFORMATION);
                                zipFile.getFile().delete();
                            }
                        } else {
                            userFileRepository.upload(String.valueOf(user.getId()), file);
                            App.showInfo("Info", "File is successfully uploaded", Alert.AlertType.INFORMATION);
                        }
                        listViewServerFilesAndDirs.setItems(FXCollections.observableList(
                                userFileRepository.getInfo(user.getId() + "\\" + labelServerPath.getText())));
                    } catch (IOException | IllegalArgumentException e) {
                        App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            }
        });
    }

    @FXML
    public void addClientFolder(ActionEvent actionEvent) {
        addFolder(labelClientPath, labelClientPath, listViewClientFilesAndDirs);

    }

    @FXML
    public void addServerFolder(ActionEvent actionEvent) {
        addFolder(labelServerPath, labelServerPath, listViewServerFilesAndDirs);
    }

    public void addFolder(Label label, Label labelPath, ListView<UserFile> listView) {
        try {
            if (listView.equals(listViewServerFilesAndDirs)) {
                App.openWindowAndWaitModal("addFolder.fxml", "add folder",
                        user.getId() + "\\" + labelPath.getText());
                listView.setItems(FXCollections.observableList(
                        userFileRepository.getInfo(user.getId() + "\\" + labelPath.getText())));
            } else {
                App.openWindowAndWaitModal("addFolderToClient.fxml", "add folder", labelPath.getText());
                listView.setItems(FXCollections.observableList(
                        userFileRepository.getInfoClient(label.getText() + "\\")));
            }
        } catch (IOException e) {
            App.showInfo("info", e.getMessage(), Alert.AlertType.INFORMATION);
        }
    }

    public void buttonBackToUsers(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) buttonBackToUsers.getScene().getWindow();
            stage.close();
            App.openWindow("users.fxml", "Users", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buttonExit(ActionEvent actionEvent) {
        Util.exit(buttonExit);
    }
}
