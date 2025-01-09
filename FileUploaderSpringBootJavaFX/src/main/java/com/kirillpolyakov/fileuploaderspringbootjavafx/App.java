package com.kirillpolyakov.fileuploaderspringbootjavafx;

import com.kirillpolyakov.fileuploaderspringbootjavafx.controller.ControllerData;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.User;
import com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit.UserRepository;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.prefs.Preferences;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Preferences preferences = Preferences.userNodeForPackage(App.class);
        if (preferences.getLong("id", -1) != -1) {
            User user = new UserRepository(preferences.get("username", ""),
                    preferences.get("password", "")).authenticate();
            if (user.getClass().getSimpleName().equals("SimpleUser")) {
                App.openWindow("main.fxml", "Main", user);
            } else {
                App.openWindow("users.fxml", "Users", null);
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("authorisation.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 226, 242);
            stage.setTitle("FileUploaderSpring!");
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static <T> Stage getStage(String name, String title, T data) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(name));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(
                new Scene(loader.load())
        );

        stage.setTitle(title);

        if (data != null) {
            ControllerData<T> controller = loader.getController();
            controller.initData(data);
        }
        return stage;
    }

    public static <T> Stage openWindow(String name, String title, T data) throws IOException {
        Stage stage = getStage(name, title, data);
        stage.show();
        return stage;
    }

    public static <T> Stage openWindowAndWait(String name, String title, T data) throws IOException {
        Stage stage = getStage(name, title, data);
        stage.showAndWait();
        return stage;
    }

    public static <T> Stage openWindowAndWaitModal(String name, String title, T data) throws IOException {
        Stage stage = getStage(name, title, data);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return stage;
    }

    public static void closeWindow(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public static void showInfo(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}