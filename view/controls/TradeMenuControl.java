package view.controls;

import controller.LoginMenuController;
import controller.gameMenuControllers.TradeMenuController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import model.Governance;
import model.ResourceEnum;
import model.TradeItem;
import model.User;
import view.GetStyle;
import view.LoginMenu;
import view.enums.ShopAndTradeControllerOut;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static controller.CommonController.resourceFinder;

public class TradeMenuControl implements Initializable {
    public ScrollPane scrollPane;
    public ScrollPane historyScrollPane;
    public VBox historyBox;
    public Label promptingMenuTest;
    private HashMap<Label, TradeItem> linkedRecentTrades = new HashMap<>();
    private TradeMenuController tradeMenuController;
    private static User currentUser;
    public Label currentGold;
    public Label message1, message2, message3, message4, message5, message6, message7, currentNumber;
    public Separator sep1, sep2, sep3, sep4, sep5, sep6, sep7;
    private Label tradeItem = new Label();
    private TextField price = new TextField();
    private TextArea message = new TextArea();
    private int currentInt = 1;
    private User accepter;
    private MediaPlayer mediaPlayer;

    public static void setCurrentUser(User currentUser) {
        TradeMenuControl.currentUser = currentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentGold != null)
            currentGold.setText("" + currentUser.getGovernance().getGold());
        if (price != null)
            price.setVisible(false);
        tradeMenuController = new TradeMenuController(currentUser);
        if (message1 != null) {
            pairLabelsWithTrades();
        }
        if (scrollPane != null) {
            try {
                loadPlayers();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (historyBox != null) {
            showMyHistory();
        }
    }


    private void pairLabelsWithTrades() {
        String[] messages = tradeMenuController.popup().split("\n");
        TradeItem trade;
        linkedRecentTrades = new HashMap<>();
        for (int i = 0; i < messages.length; i++) {
            if (messages[i] == null || messages[i] == "")
                return;
            trade = tradeFinderByMessage(messages[i]);
            if (trade == null) {
                promptingMenuTest.setText(messages[i]);
                return;
            }
            switch (i + 1) {
                case 1:
                    message1.setVisible(true);
                    sep1.setVisible(true);
                    message1.setText(messages[i]);
                    linkedRecentTrades.put(message1, trade);
                    break;
                case 2:
                    message2.setVisible(true);
                    sep2.setVisible(true);
                    message2.setText(messages[i]);
                    linkedRecentTrades.put(message2, trade);
                    break;
                case 3:
                    message3.setVisible(true);
                    sep3.setVisible(true);
                    message3.setText(messages[i]);
                    linkedRecentTrades.put(message3, trade);
                    break;
                case 4:
                    message4.setVisible(true);
                    sep4.setVisible(true);
                    message4.setText(messages[i]);
                    linkedRecentTrades.put(message4, trade);
                    break;
                case 5:
                    message5.setVisible(true);
                    sep5.setVisible(true);
                    message5.setText(messages[i]);
                    linkedRecentTrades.put(message5, trade);
                    break;
                case 6:
                    message6.setVisible(true);
                    sep6.setVisible(true);
                    message6.setText(messages[i]);
                    linkedRecentTrades.put(message6, trade);
                    break;
                case 7:
                    message7.setVisible(true);
                    message7.setText(messages[i]);
                    linkedRecentTrades.put(message7, trade);
                    break;
                case 8:
                    return;
            }
        }
    }

    private TradeItem tradeFinderByMessage(String message) {
        String regex = " id: (?<wantedPart>.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find())
            return null;
        String id = matcher.group("wantedPart");
        for (TradeItem trade : Governance.getAllTrades()) {
            if (trade.getId().equals(id))
                return trade;
        }
        return null;
    }

    public void backToGame() {
        LoginMenu.getStage().close();
    }

    public void enterShop(MouseEvent mouseEvent) throws IOException {
        ShopMenuControl.setCurentUser(currentUser);
        openAddress("/FXML/shopMenu.fxml");
    }

    private void openAddress(String address) throws IOException {
        URL url = ShopMenuControl.class.getResource(address);
        Pane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        LoginMenu.getStage().setScene(scene);
        //LoginMenu.getStage().setFullScreen(true);
        LoginMenu.getStage().show();
    }

    public void enterMassage(MouseEvent mouseEvent) throws IOException {
        switch (((Label) mouseEvent.getSource()).idProperty().get()) {
            case "message1":
                setSeenAndOpenMessage(linkedRecentTrades.get(message1));
                break;
            case "message2":
                setSeenAndOpenMessage(linkedRecentTrades.get(message2));
                break;
            case "message3":
                setSeenAndOpenMessage(linkedRecentTrades.get(message3));
                break;
            case "message4":
                setSeenAndOpenMessage(linkedRecentTrades.get(message4));
                break;
            case "message5":
                setSeenAndOpenMessage(linkedRecentTrades.get(message5));
                break;
            case "message6":
                setSeenAndOpenMessage(linkedRecentTrades.get(message6));
                break;
            case "message7":
                setSeenAndOpenMessage(linkedRecentTrades.get(message7));
                break;
        }
    }

    private void setSeenAndOpenMessage(TradeItem tradeItem) throws IOException {
        if (currentUser.getUsername().equals(tradeItem.getOneWhoRequests().getUsername()))
            tradeItem.setSeenRequester(true);
        else
            tradeItem.setSeenAccepter(true);
        historyBox.getChildren().remove(0);
        VBox vBox = new VBox();
        vBox.setSpacing(-10);
        Label label = simpleLabelStyler("Trade Item");
        vBox.getChildren().add(label);

        Image image = null;
        ImageView imageView;
        if (tradeItem.getOneWhoRequests().getUsername().equals(currentUser.getUsername())) {
            if (tradeItem.getSeenAccepter())
                image = new Image(TradeMenuControl.class.getResource("/Images/seen.png").toExternalForm());
            else
                image = new Image(TradeMenuControl.class.getResource("/Images/unseen.png").toExternalForm());
        }
        if (image != null) {
            imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setPreserveRatio(true);
            HBox hbox = new HBox(imageView);
            hbox.setSpacing(10);
            hbox.setStyle("-fx-padding: -12 0 0 190");
            vBox.getChildren().add(hbox);
        }

        label = simpleLabelStyler("With: " + tradeItem.getTheOtherUser(currentUser).getUsername());
        vBox.getChildren().add(label);
        label = simpleLabelStyler("Id: " + tradeItem.getId());
        vBox.getChildren().add(label);
        label = simpleLabelStyler("Is active: " + tradeItem.getActive());
        vBox.getChildren().add(label);
        label = simpleLabelStyler("Resource type: " + tradeItem.getType().getName());
        vBox.getChildren().add(label);
        label = simpleLabelStyler("Amount: " + tradeItem.getAmount());
        vBox.getChildren().add(label);
        label = simpleLabelStyler("Last message: " + tradeItem.getMessage());
        vBox.getChildren().add(label);
        if (tradeItem.isDonation()) {
            label = simpleLabelStyler("This user decided to\ndonate to you.");
        } else {
            label = simpleLabelStyler("Offering price:\n" + tradeItem.getPrice());
        }
        vBox.getChildren().add(label);
        String status = "This trade was ";
        status += tradeItem.getAccepted() ? "accepted" : "rejected";
        label = simpleLabelStyler(status);
        if (!tradeItem.getActive() && !tradeItem.isDonation())
            vBox.getChildren().add(label);
        message = new TextArea();
        message.setStyle("-fx-max-height: 80; -fx-max-width: 180; -fx-padding: 10 -70 -10 70; -fx-background-insets: 10 -70 -10 70");
        message.setPromptText("If you want, you can leave a message here");
        if (tradeItem.getActive() && !currentUser.getUsername().equals(tradeItem.getOneWhoRequests().getUsername()))
            vBox.getChildren().add(message);
        if ((currentUser.getUsername().equals(tradeItem.getOneWhoAnswersTheCall().getUsername()) || tradeItem.isDonation())
                && tradeItem.getActive()) {
            HBox hbox = new HBox();
            Button accept = new Button();
            accept.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
            accept.getStyleClass().add("acceptButton");
            accept.setText("accept");
            accept.setOnMouseClicked(mouseEvent -> {
                if (tradeMenuController.doTheTrade(null, tradeItem) != ShopAndTradeControllerOut.SUCCESS_FOR_TRADE)
                    showError(tradeMenuController.doTheTrade(null, tradeItem).getContent());
                else {
                    tradeItem.setActive(false);
                    tradeItem.setSeenRequester(false);
                    tradeItem.setMessage(message.getText());
                    successPopup("Trade successful", "Your trade with user " +
                            tradeItem.getOneWhoRequests().getUsername() + " has been successfully done");
                    currentGold.setText("" + currentUser.getGovernance().getGold());
                    Media media = new Media(ShopMenuControl.class.getResource("/Music/cashDesk.mp3").toString());
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.play();
                    try {
                        enterHistory();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Button reject = new Button();
            reject.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
            reject.getStyleClass().add("rejectButton");
            reject.setText("reject");
            reject.setOnMouseClicked(mouseEvent -> {
                tradeItem.setActive(false);
                tradeItem.setSeenRequester(false);
                successPopup("Rejection", "You have rejected this offer. How hurtful!");
                try {
                    enterHistory();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            hbox.getChildren().addAll(accept, reject);
            hbox.setSpacing(20);
            hbox.setStyle("-fx-padding: 30 -80 -30 80; -fx-background-insets: 30 -80 -30 80;");

            if ((currentUser.getUsername().equals(tradeItem.getOneWhoRequests().getUsername()) && tradeItem.isDonation())) {
                tradeItem.setActive(false);
                currentUser.getGovernance().changeResourceAmount(tradeItem.getType(), tradeItem.getAmount());
                tradeItem.getOneWhoAnswersTheCall().getGovernance().changeResourceAmount(tradeItem.getType(), -1 * tradeItem.getAmount());
            } else
                vBox.getChildren().add(hbox);
        }
        HBox hbox = new HBox();
        hbox.getChildren().add(vBox);
        stickImage(tradeItem, hbox);
        historyBox.getChildren().add(hbox);
    }

    private void stickImage(TradeItem tradeItem, HBox hbox) {
        String address = "/Images/resources/" + tradeItem.getType().getName() + ".png";
        ImageView imageView = new ImageView(new Image(TradeMenuControl.class.getResource(address).toExternalForm()));
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        hbox.getChildren().add(imageView);
        //todo: move the picture a bit down?
        hbox.setSpacing(400);
    }

    public void backToTrade() throws IOException {
        openAddress("/FXML/tradeMenu.fxml");
    }

    public void newRequest() throws IOException {
        openAddress("/FXML/newTrade.fxml");
    }

    private void loadPlayers() throws FileNotFoundException {
        User.resetUsers();
        LoginMenuController.extractUserData();
        VBox vBox = new VBox();
        Label label;
        Separator separator;
        vBox.setStyle("-fx-background-color: transparent");
        vBox.setSpacing(5);
        label = new Label("Choose a user to trade with:");
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: EEE2BBFF; -fx-padding: 45 0 0 30; -fx-font-weight: bold; -fx-font-size: 15");
        vBox.getChildren().add(label);
        for (User user : User.getUsers()) {
            if (currentUser.getUsername().equals(user.getUsername())) continue;
            label = new Label();
            label.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
            label.getStyleClass().add("message");
            label.setStyle("-fx-padding: 25 0 0 45");
            label.maxWidth(960);
            label.setText("::uSeRnAme : " + user.getUsername() + "     ::nIcKnaME : " + user.getNickname());
            label.setOnMouseClicked(mouseEvent -> newRequestWithUser(user));
            separator = new Separator();
            separator.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
            separator.getStyleClass().add("my-separator-class");
            separator.setStyle("-fx-padding: 5 0 0 47");
            separator.setScaleX(1.05);
            vBox.getChildren().addAll(label, separator);
        }
        scrollPane.setContent(vBox);
    }

    private void newRequestWithUser(User accepter) {
        this.accepter = accepter;
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: transparent");
        Label label = new Label("Trade Request");
        label.setStyle("-fx-font-family: 'Old English Text MT'; -fx-font-size: 30; -fx-text-fill: #EEE2BBFF; -fx-padding: 45 0 0 400");
        vBox.getChildren().add(label);
        label = new Label("With: " + accepter.getUsername());
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: EEE2BBFF; -fx-padding: 15 0 0 450; -fx-font-size: 20");
        vBox.getChildren().add(label);
        label = new Label("Id: " + currentUser.getUsername() + currentUser.getGovernance().getUserTrades().size());
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: EEE2BBFF; -fx-padding: 15 0 0 450; -fx-font-size: 20");
        vBox.getChildren().add(label);
        label = new Label("Your message:");
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: EEE2BBFF; -fx-padding: 15 0 0 450; -fx-font-size: 20");
        message = new TextArea();
        message.setStyle("-fx-max-height: 80; -fx-max-width: 180; -fx-padding: 0 -425 0 425; -fx-background-insets: 0 -425 0 425");
        tradeItem.setText("choose item");
        tradeItem.setStyle("-fx-font-family: x fantasy; -fx-text-fill: EEE2BBFF; -fx-padding: 15 0 0 450; -fx-font-size: 20");


        HBox hBox = new HBox();
        ListView listView = new ListView<>();
        listView.setPrefHeight(120);
        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-background-color: transparent;");
                        } else {
                            setText(item);
                            int index = getIndex();
                            if (index % 2 == 0) {
                                setStyle("-fx-background-color: brown; -fx-text-fill: white;");
                            } else {
                                setStyle("-fx-background-color: white; -fx-text-fill: brown;");
                            }
                        }
                    }
                };
            }
        });


        listView.setStyle("-fx-background-radius: 20; -fx-max-width: 110;");
        for (ResourceEnum resourceEnum : ResourceEnum.values()) {
            if (resourceEnum.equals(ResourceEnum.NULL) || resourceEnum.equals(ResourceEnum.HORSEANDBOW)) continue;
            listView.getItems().add(resourceEnum.getName());
        }
        listView.setOnMouseClicked(event -> {
            String selectedItem = (String) listView.getSelectionModel().getSelectedItem();
            tradeItem.setText("Item: " + selectedItem);
            listView.setVisible(false);
        });

        tradeItem.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                listView.setVisible(!listView.isVisible());
            }
        });

        Button button1 = new Button();
        button1.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
        button1.getStyleClass().add("Button");
        button1.setMaxHeight(32);
        button1.setMinHeight(32);
        button1.setMaxWidth(32);
        button1.setMinWidth(32);
        button1.setGraphic(new ImageView(new Image(TradeMenuControl.class.getResource("/Images/plus.png").toExternalForm(), 25, 25, false, false)));
        button1.setOnMouseClicked(mouseEvent -> currentNumber.setText("" + ++currentInt));
        button1.setStyle("-fx-padding: 30 0 0 0; -fx-background-insets: 15 0 -15 0;");

        Button button2 = new Button();
        button2.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
        button2.getStyleClass().add("Button");
        button2.setMaxHeight(32);
        button2.setMinHeight(32);
        button2.setMaxWidth(32);
        button2.setMinWidth(32);
        button2.setGraphic(new ImageView(new Image(TradeMenuControl.class.getResource("/Images/minus.png").toExternalForm(), 25, 25, false, false)));
        button2.setOnMouseClicked(mouseEvent -> {
            if (currentInt == 1) return;
            currentNumber.setText("" + --currentInt);
        });
        button2.setStyle("-fx-padding: 30 0 0 0; -fx-background-insets: 15 0 -15 0;");
        currentNumber = new Label("" + currentInt);
        currentNumber.setStyle("-fx-font-family: x fantasy; -fx-text-fill: EEE2BBFF; -fx-font-size: 20; -fx-padding: 15 0 0 0");
        hBox = new HBox();
        hBox.getChildren().addAll(tradeItem, button1, currentNumber, button2);
        hBox.setSpacing(5);
        vBox.getChildren().add(hBox);
        hBox = new HBox();
        hBox.getChildren().addAll(listView);
        hBox.setStyle("-fx-padding: 0 0 0 450; -fx-min-height: 60");
        vBox.getChildren().addAll(hBox, label, message);

        RadioButton first = new RadioButton();
        RadioButton second = new RadioButton();
        hBox = new HBox();
        first.setGraphic(new ImageView(new Image(TradeMenuControl.class.getResource("/Images/titles/donate.png").toExternalForm(), 200, 51, false, false)));
        second.setGraphic(new ImageView(new Image(TradeMenuControl.class.getResource("/Images/titles/request.png").toExternalForm(), 200, 52, false, false)));
        first.setSelected(true);
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(first, second);

        first.setOnAction(event -> {
            price.setVisible(false);
        });

        second.setOnAction(event -> {
            price.getStylesheets().add(GetStyle.class.getResource("/CSS/login.css").toExternalForm());
            price.getStyleClass().add("TextField");
            price.setPromptText("How much would you be willing to pay?");
            price.setStyle("-fx-max-width: 700; -fx-background-position: center;-fx-text-fill: #EEE2BBFF; -fx-font-size: 15; -fx-border-insets: 0 -35 0 300; -fx-background-insets: 0 -150 0 300");
            price.setVisible(true);
        });

        Button submit = new Button("submit");
        submit.getStylesheets().add(GetStyle.class.getResource("/CSS/login.css").toExternalForm());
        submit.getStyleClass().add("Button");
        submit.setStyle("-fx-max-width: 100; -fx-padding: 0 -460 0 460; -fx-background-insets: 0 -460 0 460");
        submit.setOnMouseClicked(mouseEvent -> {
            try {
                submitNewRequest();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        hBox.getChildren().addAll(first, second);
        hBox.setStyle("-fx-padding: 20 0 0 300");
        hBox.setSpacing(20);
        vBox.getChildren().addAll(hBox, price, submit);
        scrollPane.setContent(vBox);
    }

    private void successPopup(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("success");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private void submitNewRequest() throws IOException {
        if (!checkInput()) return;
        ResourceEnum item = resourceFinder(tradeItem.getText().substring(6));
        String id = currentUser.getUsername() + currentUser.getGovernance().getUserTrades().size();
        TradeItem newTrade;
        if (price.isVisible()) {
            if (Integer.parseInt(price.getText()) > currentUser.getGovernance().getGold()) {
                showError(ShopAndTradeControllerOut.CANNOT_AFFORD_TRADE.getContent());
                return;
            }
            newTrade = new TradeItem(id, accepter, currentUser, item, currentInt,
                    Integer.parseInt(price.getText()), message.getText(), true, false);
        } else {
            newTrade = new TradeItem(id, currentUser, accepter, item, currentInt,
                    0, message.getText(), true, true);
        }
        newTrade.setSeenRequester(true);
        newTrade.setOneWhoAnswersTheCall(accepter);
        currentUser.getGovernance().addToUserTrades(newTrade);
        Governance.addToAllTrades(newTrade);
        notifyTheOtherPlayer();
        successPopup("New request added!", "Your request has been added successfully");
        backToTrade();
    }

    private void notifyTheOtherPlayer() {
        //todo
        //todo
        //todo
        //todo
    }

    private boolean checkInput() {
        if (tradeItem.getText().equals("choose item")) {
            showError("You have to choose an item before submitting a new trade.");
            return false;
        }
        if (price.isVisible() && (price.getText().equals("") || price.getText() == null)) {
            showError("There should be a price whenever you're filling in a request.");
            return false;
        } else if (price.isVisible()) {
            try {
                Integer.parseInt(price.getText());
            } catch (NumberFormatException e) {
                showError("Price format is not correct. It should be a valid, positive integer.");
                return false;
            }
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("error");
        alert.setHeaderText("failed to submit");
        alert.setContentText(message);
        alert.show();
    }

    public void enterTrade() throws IOException {
        openAddress("/FXML/tradeMenu.fxml");
    }

    public void enterHistory() throws IOException {
        openAddress("/FXML/tradeHistory.fxml");
    }


    private void showMyHistory() {
        VBox vBox = new VBox();
        Label label = new Label("Here are your trades. Click for more detailed info");
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: #EEE2BBFF; -fx-padding: 0 0 0 30; -fx-font-weight: bold; -fx-font-size: 15");
        vBox.getChildren().add(label);
        myTradeExtractor(vBox, false, true);
        historyBox.getChildren().add(vBox);
    }

    private void myTradeExtractor(VBox vBox, boolean receiver, boolean both) {
        String ans;
        for (TradeItem trade : Governance.getAllTrades()) {

            if(!both) {
                if(receiver) {
                    if((trade.getOneWhoAnswersTheCall().getUsername().equals(currentUser.getUsername()) && !trade.isDonation()) ||
                            (trade.getOneWhoRequests().getUsername().equals(currentUser.getUsername()) && trade.isDonation()));
                    else
                        continue;
                }
                else {
                    if((trade.getOneWhoRequests().getUsername().equals(currentUser.getUsername()) && !trade.isDonation()) ||
                            (trade.getOneWhoAnswersTheCall().getUsername().equals(currentUser.getUsername()) && trade.isDonation()));
                    else
                        continue;
                }
            }
             else {
                 if(!trade.getOneWhoRequests().getUsername().equals(currentUser.getUsername()) &&
                         !trade.getOneWhoAnswersTheCall().getUsername().equals(currentUser.getUsername()))
                     continue;
            }


            String state;
            if (!trade.getSeenAccepter())
                state = "haven't seen";
            else if (trade.getActive())
                state = "seen, not answered";
            else if (trade.getAccepted())
                state = "accepted";
            else
                state = "rejected";
            ans = trade.getLastDateUpdate() + "with: " + trade.getTheOtherUser(currentUser).getUsername() + " and id: " + trade.getId()
                    + " state: " + state;
            Label label = actionLabelStyler(ans);
            label.setOnMouseClicked(mouseEvent -> {
                try {
                    setSeenAndOpenMessage(trade);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            vBox.getChildren().add(label);
        }
    }

    private Label actionLabelStyler(String text) {
        Label label = new Label(text);
        label.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
        label.getStyleClass().add("message");
        label.setStyle("-fx-padding: 25 0 0 45");
        label.maxWidth(960);
        return label;
    }

    private Label simpleLabelStyler(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-padding: 25 0 0 70; -fx-font-size: 25");
        label.setText(text);
        return label;
    }

    public void enterReceived() {
        historyBox.getChildren().remove(0);
        VBox vBox = new VBox();
        Label label = new Label("Click on a request for more info:");
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: #EEE2BBFF; -fx-padding: 0 0 0 30; -fx-font-weight: bold; -fx-font-size: 15");
        vBox.getChildren().add(label);
        myTradeExtractor(vBox, true, false);
        historyBox.getChildren().add(vBox);
    }

    public void enterSend(MouseEvent mouseEvent) {
        historyBox.getChildren().remove(0);
        VBox vBox = new VBox();
        Label label = new Label("Here are your requests. click for more info:");
        label.setStyle("-fx-font-family: x fantasy; -fx-text-fill: #EEE2BBFF; -fx-padding: 0 0 0 30; -fx-font-weight: bold; -fx-font-size: 15");
        vBox.getChildren().add(label);
        myTradeExtractor(vBox, false, false);
        historyBox.getChildren().add(vBox);
    }
}
