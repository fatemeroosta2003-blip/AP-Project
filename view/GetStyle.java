package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class GetStyle {
    public static Label label(String text){
        Label label = new Label(text);
        label.getStylesheets().add(GetStyle.class.getResource("/CSS/login.css").toExternalForm());
        label.getStyleClass().add("Label");
        return label;
    }
    public static Button button(String text){
        Button button = new Button(text);
        button.getStylesheets().add(GetStyle.class.getResource("/CSS/login.css").toExternalForm());
        button.getStyleClass().add("Button");
        return button;
    }
    public static TextField textField(String text){
        TextField textField = new TextField();
        textField.setPromptText(text);
        textField.getStylesheets().add(GetStyle.class.getResource("/CSS/login.css").toExternalForm());
        textField.getStyleClass().add("TextField");
        return textField;
    }
    public static PasswordField passwordField(String text){
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(text);
        passwordField.getStylesheets().add(GetStyle.class.getResource("/CSS/login.css").toExternalForm());
        passwordField.getStyleClass().add("TextField");
        return passwordField;
    }

}
