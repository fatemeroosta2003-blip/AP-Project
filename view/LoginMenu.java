package view;
import controller.LoginMenuController;
import controller.RegisterMenuController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Governance;
import model.ResourceEnum;
import model.TradeItem;
import model.User;
import view.controls.GameControlTest;
import view.controls.GameMenuControl;
import view.controls.LoginRegisterMenuControl;
import view.controls.ShopMenuControl;

import java.net.URL;

import static view.RegisterMenu.stayLogin;

public class LoginMenu extends Application {
    private static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginRegisterMenuControl loginMenuControl = new LoginRegisterMenuControl();
        stage = new Stage();
        RegisterMenuController registerMenuController = new RegisterMenuController();
        registerMenuController.setUpSloganDataBase();
        registerMenuController.setUpUserInfo();
        LoginMenuController.setUpStayedLogin();
        LoginMenuController.extractUserData();

        User user = new User("test","test","test","test","test",0,"test",0);
        User user1 = new User("test1","test","test","test","test",0,"test",0);
        ShopMenuControl.setCurentUser(user);
        ShopMenuControl shopMenuControl = new ShopMenuControl();

        User.getUsers().add(user);
        User.getUsers().add(user1);
        Governance.getAllTrades().add(new TradeItem("1234" , user , User.getUsers().get(0) , ResourceEnum.MEAT, 1 , 100 , "hi0", true, false));
        Governance.getAllTrades().add(new TradeItem("1200" , User.getUsers().get(0) , user  , ResourceEnum.BREAD, 1 , 0 , "hi0", true, true));
        Governance.getAllTrades().add(new TradeItem("1204" , User.getUsers().get(0) , user  , ResourceEnum.CHEESE, 3 , 50 , "hi0", true, false));
         //shopMenuControl.enterTrade();
        //LoginRegisterMenuControl.openAddress("/FXML/shopMenu.fxml");
        Governance.getEmpires().add(user);
        Governance.getEmpires().add(user1);
        new GameControlTest().start(LoginMenu.getStage(), user);


        if (stayLogin()) {
            //todo: enter main menu immediately
        }
        stage = new Stage();
        URL url = LoginMenu.class.getResource("/FXML/firstMenu.fxml");
        BorderPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        System.out.println(User.getUsers().size());
        //stage.setFullScreen(true);
        stage.show();
        //todo---------------------------------------------------------------


//        BorderPane borderPane = new BorderPane();
//        borderPane.getStylesheets().add(getClass().getResource("/CSS/login.css").toExternalForm());
//        borderPane.resize(1530, 800);
//        ImageView imageView = new ImageView(new Image(getClass().getResource("/Images/login.jpg").toExternalForm(), 1530, 960, false, false));
//        imageView.setPreserveRatio(true);
//        borderPane.getChildren().add(imageView);
//        VBox vBox = new VBox();
//        vBox.setAlignment(Pos.CENTER);
//        vBox.setSpacing(10);
//        addToVbox(vBox);
//
//        //exit button for our use only
//        Button closeButton = new Button("Close");
//        closeButton.setOnAction(event -> {
//            stage.close();
//        });
//        vBox.getChildren().add(closeButton);
//        vBox.getChildren().get(vBox.getChildren().size() - 1).setLayoutY(0);
//        vBox.getChildren().get(vBox.getChildren().size() - 1).setLayoutX(0);
//
//        borderPane.setCenter(vBox);
//
//        imageView.fitWidthProperty().bind(stage.widthProperty());
//        stage.setScene(new Scene(borderPane));
//        stage.setFullScreen(true);
//        stage.show();
    }

    private void addToVbox(VBox vBox){
        Label label = GetStyle.label("Welcome to StrongHold!");
        TextField username = GetStyle.textField("username");
        username.focusedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Boolean> arg0,
                            Boolean oldPropertyValue, Boolean newPropertyValue) {
                        if (newPropertyValue) {
                            username.setText("mine");
                            // Clearing message if any
//                            actiontarget.setText("");
//                            // Hiding the error message
//                            usernameValidator.hide();
                        }
                    }
                });
        TextField nickname = GetStyle.textField("nickname");
        TextField email = GetStyle.textField("email@email.com");
        PasswordField password = GetStyle.passwordField("password");
        PasswordField rePassword = GetStyle.passwordField("re-enter password");
        vBox.getChildren().addAll(label, username);
        vBox.getChildren().get(1).setLayoutY(100);
        vBox.getChildren().get(0).setLayoutY(200);
    }

    public static Stage getStage() {
        return stage;
    }
}
