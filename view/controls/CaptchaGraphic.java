package view.controls;

import controller.RegisterMenuController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.User;
import view.LoginMenu;
import view.RegisterMenu;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ResourceBundle;

public class CaptchaGraphic  {

    public static User currentUser;
    public static Pane pane;
    private static int currentCaptcha;
    private static String currentPath;
    public TextField userCaptchaAnswer;
    private static RegisterMenuController registerMenuController;

    public static void enterCaptcha() throws IOException {
        int picNum = countPictures();
        int randomPick = (int) (picNum * Math.random());
        currentCaptcha = randomPick;
        getAPic();
    }


    public static int countPictures() {
        File directory = new File(System.getProperty("user.dir") + "/src/main/resources/Images/captcha/");
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpeg");
            }
        });
        return files.length;
    }

    private static void getAPic() throws IOException {
        File directory = new File(System.getProperty("user.dir") + "/src/main/resources/Images/captcha/");
        File[] files = directory.listFiles();
        Arrays.sort(files);
        File fourthFile = files[currentCaptcha];
        String pathToFile = fourthFile.getAbsolutePath();
        currentPath = pathToFile;
        InputStream inputStream = Files.newInputStream(Path.of(pathToFile));
        ImageView captchaPhoto = new ImageView(new Image(inputStream, 160, 60, false, false));
        if (pane != null) {
            VBox vBox = new VBox();
            vBox.getChildren().add(captchaPhoto);
            pane.getChildren().add(vBox);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutY(180);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutX(688);
        }
    }

    public static RegisterMenuController getRegisterMenuController() {
        return registerMenuController;
    }

    public void changeCaptcha() throws IOException {
        int picNum = countPictures();
        int randomPick = (int) (picNum * Math.random());
        while(randomPick == currentCaptcha)
            randomPick = (int) (picNum * Math.random());
        currentCaptcha = randomPick;
        pane.getChildren().remove(pane.getChildren().size() - 1);
        getAPic();
    }

    public void submit(MouseEvent mouseEvent) throws IOException {
        int index = currentPath.lastIndexOf("\\") + 1;
        String photoName = currentPath.substring(index, currentPath.length() - 4);
        if(userCaptchaAnswer == null || userCaptchaAnswer.getText().length() == 0 ||
                !userCaptchaAnswer.getText().trim().equals(photoName)) {
            System.out.println(userCaptchaAnswer.getText().trim());
            System.out.println(photoName);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Captcha");
            alert.setContentText("Captcha wasn't entered correctly.\nTry again!");
            alert.show();
            changeCaptcha();
        }
        else {
            registerMenuController.createUser();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Registered successfully");
            alert.setHeaderText("Welcome to the club!");
            alert.setContentText("Just wanted to let you know that the registration was successful!");
            alert.showAndWait();
            URL url = LoginMenu.class.getResource("/FXML/firstMenu.fxml");
            BorderPane pane = FXMLLoader.load(url);
            Scene scene = new Scene(pane);
            LoginMenu.getStage().setScene(scene);
            LoginMenu.getStage().show();
        }
    }

    public void backToSec(MouseEvent mouseEvent) throws IOException {
        LoginRegisterMenuControl.openAddress("/FXML/securityQuestion.fxml");
    }

    public static void setRegisterMenuController(RegisterMenuController registerMenuController) {
        CaptchaGraphic.registerMenuController = registerMenuController;
    }

    public void submitForgot(MouseEvent mouseEvent) throws IOException {
        int index = currentPath.lastIndexOf("\\") + 1;
        String photoName = currentPath.substring(index, currentPath.length() - 4);
        if(userCaptchaAnswer == null || userCaptchaAnswer.getText().length() == 0 ||
                !userCaptchaAnswer.getText().trim().equals(photoName)) {
            System.out.println(userCaptchaAnswer.getText().trim());
            System.out.println(photoName);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Captcha");
            alert.setContentText("Captcha wasn't entered correctly.\nTry again!");
            alert.show();
            changeCaptcha();
        }
        else {

            ForgotPassword.stage.close();
        }
    }

    public void backToForgot(MouseEvent mouseEvent) throws IOException {
        ForgotPassword.stage.close();
        ForgotPassword.stage = LoginMenu.getStage();
        LoginRegisterMenuControl.openAddress("/FXML/forgotPass.fxml");
    }

    public void submitLogin(MouseEvent mouseEvent) throws IOException {
        currentUser = LoginRegisterMenuControl.loginUser;
        int index = currentPath.lastIndexOf("\\") + 1;
        String photoName = currentPath.substring(index, currentPath.length() - 4);
        if(userCaptchaAnswer == null || userCaptchaAnswer.getText().length() == 0 ||
                !userCaptchaAnswer.getText().trim().equals(photoName)) {
            System.out.println(userCaptchaAnswer.getText().trim());
            System.out.println(photoName);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Captcha");
            alert.setContentText("Captcha wasn't entered correctly.\nTry again!");
            alert.show();
            changeCaptcha();
        }
        else {
            URL url = LoginMenu.class.getResource("/FXML/mainMenu.fxml");
            BorderPane pane = FXMLLoader.load(url);
            Scene scene = new Scene(pane);
            LoginMenu.getStage().setScene(scene);
            MainMenuControl.currentUser = currentUser;
            //System.out.println("captcha user: " + currentUser.getUsername());
            LoginMenu.getStage().show();
        }
    }

    public void backToLogin(MouseEvent mouseEvent) throws IOException {
        LoginRegisterMenuControl.openAddress("/FXML/loginMenu.fxml");
    }
}
