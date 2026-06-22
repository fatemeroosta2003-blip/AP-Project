package view.controls;

import controller.PasswordReset;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.User;
import view.LoginMenu;
import view.enums.ProfisterControllerOut;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class ForgotPassword {
    public TextField username;
    public  Label usernameHandler;
    public Label passwordErrorHandler;
    private User user;
    public static Stage stage;
    public Label passwordErrorHandler2;
    public PasswordField newPass;
    public PasswordField reNewPass;
    public TextField answer;
    private PasswordReset passwordReset;
    @FXML
    public void initialize(){
        passwordReset = new PasswordReset();
        username.textProperty().addListener((observableValue, s, t1) -> {
            for (User u : User.getUsers()){
                if (!u.getUsername().equals(username.getText())) usernameHandler.setText("username doesnt exist");
                else {
                    user = u;
                    usernameHandler.setText("");
                }
            }
        });
        newPass.textProperty().addListener((observableValue, s, t1) -> {
            if (!passwordReset.checkPasswordFormat(newPass.getText()).equals(ProfisterControllerOut.VALID)){
                passwordErrorHandler.setText(passwordReset.checkPasswordFormat(newPass.getText()).getContent());
            }
            else  passwordErrorHandler.setText("");
        });
        reNewPass.textProperty().addListener((observableValue, s, t1) -> {
            System.out.println(reNewPass.getText());
            if (!passwordReset.checkPasswordFormat(reNewPass.getText()).equals(ProfisterControllerOut.VALID)){
                passwordErrorHandler2.setText(passwordReset.checkPasswordFormat(reNewPass.getText()).getContent());
            }
            else if (!newPass.getText().equals(reNewPass.getText())){
                passwordErrorHandler2.setText("Passwords are not equal");
            }
            else  passwordErrorHandler.setText("");
        });

    }

    public void forgotValidate(MouseEvent mouseEvent) throws NoSuchAlgorithmException, IOException {
        Alert alert;
        if (user==null){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Recovery Failed");
            alert.setContentText("Username doesnt exist");
            alert.showAndWait();
            return;
        };
        if (!user.getSecurityAnswer().equals(answer)){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Recovery Failed");
            alert.setContentText("Your answer is wrong");
            alert.showAndWait();
            return;
        }
        passwordReset = new PasswordReset();
        if (!newPass.getText().equals(reNewPass.getText()) || !passwordReset.checkPasswordFormat(newPass.getText()).equals(ProfisterControllerOut.VALID)){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Recovery Failed");
            alert.setContentText("Your Password is not in correct format");
            alert.showAndWait();
            return;
        }

        Stage stage = new Stage();
        ForgotPassword.stage = stage;
        String password = passwordReset.encryptPassword(newPass.getText());;
        URL url = LoginMenu.class.getResource("/FXML/ForgotCapcha.fxml");
        BorderPane pane = FXMLLoader.load(url);
        CaptchaGraphic.pane = pane;
        CaptchaGraphic.enterCaptcha();
        stage.setScene(new Scene(pane));
        stage.showAndWait();
        if (stage.isShowing());
              user.setPassword(password);
        if (ForgotPassword.stage!=LoginMenu.getStage()){
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Password changed");
            alert.setContentText("Your password recovered and changed");
            alert.showAndWait();
            passwordReset.changePassword(password);
        }

    }

    public void back(MouseEvent mouseEvent) throws IOException {
        LoginRegisterMenuControl.openAddress("/FXML/firstMenu.fxml");
    }
}
