package view;

import controller.RegisterMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.User;
import view.controls.CaptchaGraphic;

import java.io.IOException;
import java.net.URL;

public class AvatarMenu extends Application {
    private static Stage stage;
    private RegisterMenuController registerMenuController = new RegisterMenuController();

    public AvatarMenu(RegisterMenuController registerMenuController) {
        this.registerMenuController = registerMenuController;
    }

    @Override
    public void start(Stage stage) throws Exception {
        AvatarMenu.stage = stage;
        URL url = getClass().getResource("/FXML/avater.fxml");
        Pane pane = new Pane();
//        ImageView backGround= new ImageView(new Image(AvatarMenu.class.getResource("/image/avatarMenu.png").toExternalForm(), 800, 4+600, false, false));
        BorderPane borderPane = FXMLLoader.load(url);
  //      pane.getChildren().addAll(backGround);
        borderPane.setCenter(avatars());
        pane.getChildren().addAll(borderPane);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    public Pane avatars(){
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        vBox.setSpacing(10);
        ImageView imageView;
        for (int i =1; i<10; i++){
            VBox hBox2 = new VBox();
            hBox2.setAlignment(Pos.CENTER);
            hBox2.setSpacing(5);
            imageView = new ImageView(new Image(AvatarMenu.class.getResource("/Images/avatar"+i+".png").toExternalForm(), 120, 80, false, false));
            imageView.setPickOnBounds(true);
            int finalI = i;
            imageView.setOnMouseClicked((MouseEvent e) -> {
                try {
                    selectedImage(finalI);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            Label label = new Label(i+"");
            label.setTextFill(Color.WHITE);
            hBox2.getChildren().addAll(imageView);
            hBox2.getChildren().addAll(label);
            hBox.getChildren().addAll(hBox2);
            if (i%3==0){
                vBox.getChildren().addAll(hBox);
                hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(10);
            }
        }
        return vBox;
    }
    public void selectedImage(int number) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Avatar Selected");
        alert.setContentText("Are you sure to choose avatar "+number+"?");
        if ( alert.showAndWait().get().getButtonData().isCancelButton()) return;
        selectedSuccessfully(AvatarMenu.class.getResource("/Images/avatar"+number+".png").toString(), null);
        openCaptcha();
    }
    public void openCaptcha() throws IOException {
        URL url = LoginMenu.class.getResource("/FXML/captcha.fxml");
        BorderPane pane = FXMLLoader.load(url);
        CaptchaGraphic.pane = pane;
        CaptchaGraphic.enterCaptcha();
        CaptchaGraphic.setRegisterMenuController(registerMenuController);
        Scene scene = new Scene(pane);
        LoginMenu.getStage().setScene(scene);
        //LoginMenu.getStage().setFullScreen(true);
        LoginMenu.getStage().show();
    }
    public void selectedSuccessfully(String avatarUrl, User user) throws Exception {
        //  System.out.println(avatarUrl);
//        saveUrlToJson(user, avatarUrl);
//        user.setAvatarUrl(avatarUrl);
//        if (!isProfile) {
//            MainMenu mainMenu = new MainMenu(MainMenu.onMusic, user);
//            mainMenu.start(LoginMenu.getStage());
//        }
//        else {
//            ProfileMenu profileMenu = new ProfileMenu(user);
//            profileMenu.start(LoginMenu.getStage());
//        }
        registerMenuController.url = avatarUrl;
        openCaptcha();
    };

}