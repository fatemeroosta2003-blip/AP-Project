package view.controls;

import controller.gameMenuControllers.ShopMenuController;
import controller.gameMenuControllers.TradeMenuController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Resource;
import model.ResourceEnum;
import model.User;
import view.LoginMenu;
import view.enums.ShopAndTradeControllerOut;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

public class ShopMenuControl implements Initializable {
    MediaPlayer mediaPlayer;
    public Label breadstock;
    private static User curentUser;
    public Label meatstock;
    public Label applestock;
    public Label cheesestock;
    public Label stonestock;
    public Label alestock;
    public Label flourstock;
    public Label hopsstock;
    public Label ironstock;
    public Label woodstock;
    public Label wheatstock;
    public Label currentgold;

    public static void setCurentUser(User curentUser) {
        ShopMenuControl.curentUser = curentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setStock();
    }

    private void setStock() {
        if(breadstock != null)
            breadstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.BREAD));
        if(meatstock != null)
            meatstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.MEAT));
        if(alestock != null)
            alestock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.ALE));
        if(applestock != null)
            applestock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.APPLE));
        if(stonestock != null)
            stonestock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.STONE));
        if(flourstock != null)
            flourstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.FLOUR));
        if(wheatstock != null)
            wheatstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.WHEAT));
        if(woodstock != null)
            woodstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.WOOD));
        if(hopsstock != null)
            hopsstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.HOPS));
        if(ironstock != null)
            ironstock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.IRON));
        if(cheesestock != null)
            cheesestock.setText("" + curentUser.getGovernance().getResourceAmount(ResourceEnum.CHEESE));
        if(currentgold != null)
            currentgold.setText("" + curentUser.getGovernance().getGold());
    }

    public void buy(MouseEvent mouseEvent) {
        ShopMenuController shopMenuController = new ShopMenuController(curentUser);
        shopMenuController.setMerchandise(new Resource(resourceTypeSpecifier(((Button)mouseEvent.getSource()).getText()),1));
        ShopAndTradeControllerOut result =shopMenuController.buy(null,curentUser);
        if(result != ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_PURCHASE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to buy");
            alert.setContentText(result.getContent());
            alert.show();
            return;
        }
        shopMenuController.purchase();
        completeTransaction("buy");
    }

    public void sell(MouseEvent mouseEvent) {
        ShopMenuController shopMenuController = new ShopMenuController(curentUser);
        shopMenuController.setMerchandise(new Resource(resourceTypeSpecifier(((Button)mouseEvent.getSource()).getText()),1));
        ShopAndTradeControllerOut result =shopMenuController.sell(null,curentUser);
        if(result != ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_SELL) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to sell");
            alert.setContentText(result.getContent());
            alert.show();
            return;
        }
        shopMenuController.retail();
        completeTransaction("sell");
    }

    private void completeTransaction(String type) {
        playCashAudio();
        successNotifier(type);
        setStock();
    }
    private void successNotifier(String type) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Transaction complete");
        if (type.equals("sell")) {
            alert.setContentText("Successfully sold item");
        } else {
            alert.setContentText("Successfully bought item");
        }
        alert.show();
    }

    public static ResourceEnum resourceTypeSpecifier(String type) {
        EnumSet<ResourceEnum> unitEnums = EnumSet.allOf(ResourceEnum.class);
        for (ResourceEnum resourceEnum : unitEnums) {
            if (type.contains(resourceEnum.getName()) && !resourceEnum.getName().equals(""))
                return resourceEnum;
        }
        return null;
    }

    public void playCashAudio() {
        Media media = new Media(ShopMenuControl.class.getResource("/Music/cashDesk.mp3").toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public void backToGame() {
        LoginMenu.getStage().close();
    }

    public void enterTrade() throws IOException {
        TradeMenuControl.setCurrentUser(curentUser);
        URL url = ShopMenuControl.class.getResource("/FXML/tradeMenu.fxml");
        Pane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        LoginMenu.getStage().setScene(scene);
        //LoginMenu.getStage().setFullScreen(true);
        LoginMenu.getStage().show();
    }
}
