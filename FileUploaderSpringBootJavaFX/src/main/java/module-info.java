module com.kirillpolyakov.fileuploaderspringbootjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.fasterxml.jackson.annotation;
    requires okhttp3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires retrofit2;
    requires retrofit2.converter.jackson;
    requires java.prefs;
    requires zip4j;


    opens com.kirillpolyakov.fileuploaderspringbootjavafx to javafx.fxml;
    exports com.kirillpolyakov.fileuploaderspringbootjavafx;
    exports com.kirillpolyakov.fileuploaderspringbootjavafx.controller to javafx.fxml;
    opens com.kirillpolyakov.fileuploaderspringbootjavafx.controller to javafx.fxml;
    exports com.kirillpolyakov.fileuploaderspringbootjavafx.dto to com.fasterxml.jackson.databind;
    exports com.kirillpolyakov.fileuploaderspringbootjavafx.model to com.fasterxml.jackson.databind;
}