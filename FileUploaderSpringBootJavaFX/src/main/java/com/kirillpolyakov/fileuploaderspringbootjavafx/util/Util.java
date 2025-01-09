package com.kirillpolyakov.fileuploaderspringbootjavafx.util;

import com.kirillpolyakov.fileuploaderspringbootjavafx.App;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class Util {

    public static boolean notDigitsOrLetters(String s) {
        return Arrays.stream(s.split("")).anyMatch(x -> !Character.isLetterOrDigit(x.charAt(0)));
    }

    public static boolean notLetters(String s) {
        return Arrays.stream(s.split("")).anyMatch(x -> !Character.isLetter(x.charAt(0)));
    }

    public static void exit(Button button) {
        try {
            Preferences preferences = Preferences.userNodeForPackage(App.class);
            preferences.remove("id");
            preferences.remove("username");
            preferences.remove("password");
            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();
            App.openWindow("authorisation.fxml", "Authorisation", null);
        } catch (IOException e) {
            App.showInfo("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
