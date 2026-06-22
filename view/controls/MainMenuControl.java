package view.controls;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
import view.LoginMenu;

import java.io.IOException;
import java.net.URL;

public class MainMenuControl {

    public static User currentUser;
    public void startGame(MouseEvent mouseEvent) {
    }

    public void setting(MouseEvent mouseEvent) {
    }

    public void showProfile(MouseEvent mouseEvent) throws IOException {
        URL url = LoginMenu.class.getResource("/FXML/profile.fxml");
        BorderPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        LoginMenu.getStage().setScene(scene);
        ProfileControl.currentUser = currentUser;
        //System.out.println("main menu: " + ProfileControl.currentUser.getUsername());
        LoginMenu.getStage().show();
    }

    public void exit(MouseEvent mouseEvent) {
        Platform.exit();
    }
    public static void openMenu(String s) throws IOException {
        URL url = LoginMenu.class.getResource(s);
        BorderPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        LoginMenu.getStage().setScene(scene);
        LoginMenu.getStage().show();
    }
}
