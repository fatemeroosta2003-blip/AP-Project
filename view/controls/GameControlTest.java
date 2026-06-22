package view.controls;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.MapMenuController;
import controller.PatchFinding;
import controller.gameMenuControllers.GameController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import model.*;
import model.buildings.Building;
import model.buildings.BuildingEnum;
import model.units.Unit;
import model.units.UnitEnum;
import view.GetStyle;
import view.enums.GameControllerOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GameControlTest {
    private final int TILE_SIZE = 150;
    private Stage primaryStage;
    private Pane root;
    private User currentPlayer;
    private ImageView buildingImageView = new ImageView();
    private HashMap<ImageView, Building> buildings = new HashMap<>();
    private HashMap<Building , VBox> buildingAndFiresAddingToRoot = new HashMap<>();
    private HashMap<GridPane, Unit> units = new HashMap<>();

    private static final double SCALE_DELTA = 1.1;
    private final Scale scaleTransform = new Scale(1, 1);


    private VBox unitBar = new VBox();
    private VBox popularityFactorsBar = new VBox();
    private HBox mainBar = new HBox();
    private VBox changeFactorsBar = new VBox();
    private VBox barBook = new VBox();
    private ImageView barScene = new ImageView();

    private Button[] houses = new Button[4];
    private BuildingType currentSet = BuildingType.TOWN;

    private int xCenter = 50;
    private int yCenter = 50;
    private static Pane[][] panes = new Pane[10][5];
    private HashMap<Pane, Tile> linkedHouses = new HashMap<>();
    private Map map;
    private GameController gameController;
    private boolean tilesOccupied = false;
    private boolean isCopyActive = false;
    private boolean isPasteActive = false;
    private List<ImageView> savedImageViews = new ArrayList<>();
    private ArrayList<Building> copiedBuildings = new ArrayList<>();
    private boolean clipboardPaneUp = false;
    private Label navar;
    private int navarIndex = 1;
    private boolean isDeleteActive = false;

    private Tile lastTileUndo;
    private HashMap<Pane, Tile> lastLinkedHousesUndo = new HashMap<>();
    private ImageView lastBuildingImageViewUndo = new ImageView();
    private HashMap<ImageView, Building> lastBuildingsUndo = new HashMap<>();

    private boolean doWeHaveUndo = false;
    private HashMap<User, Building> selectedBuilding = new HashMap<>();
    private boolean selectActive = false;
    private boolean hoverActive = true;

    private Pane overlayPane = new Pane();

    private HashMap<ImageView, HashMap<Building, Pane>> fires = new HashMap<>();
    private GridPane miniMap = new GridPane();
    private Stage pauseWindow;

    boolean sickland = false;

    public void start(Stage primaryStage, User currentPlayer) {
        this.currentPlayer = currentPlayer;
        MapMenuController mapMenuController = new MapMenuController();
        mapMenuController.setUpATemplate();
        map = mapMenuController.selectedMap;
        this.gameController = new GameController(currentPlayer, map);

        Pane root = new Pane();
        root.getTransforms().add(scaleTransform);
        this.primaryStage = primaryStage;
        this.root = root;

        for (int i = 0; i < 50; i++) {
            addTile(root, i);
        }

        addBar(root);
        createGamePane(currentSet);

        navar = new Label();
        navar.setPrefHeight(633);
        navar.setPrefWidth(60);
        navar.setStyle("-fx-background-color: #2a0a0a");
        navar.setLayoutX(1480);
        navar.setLayoutY(0);
        root.getChildren().add(navar);
        navarIndex = root.getChildren().indexOf(navar);

        joyStick();
        selectArea();
        copySetUp();
        clipBoardSetUp();
        selectBuildingSetUp();
        nextTurnSetUp();
        addShortcutKeys();

        primaryStage.setScene(new Scene(root, 1530, 800));
        primaryStage.getScene().addEventFilter(ScrollEvent.ANY, this::handleMouseScroll);
        primaryStage.show();
    }

    private void nextTurnSetUp() {
        Button addButton = new Button();
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/next_turn.png").toExternalForm()));
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        addButton.setGraphic(imageView);
        addButton.setLayoutX(1485);
        addButton.setLayoutY(170);
        addButton.setOnAction(event -> {
            if (sickland) {
                currentPlayer.getGovernance().changePopularity(-10);
                sickland = false;
            }
            gameController.produce();
            gameController.setTargets();
            gameController.mapMotion();
            gameController.foodRateEffect();
            gameController.taxRateEffect();
            gameController.fearRateEffect();
            gameController.churchEffect();
            gameController.setOnFire();
            //set target, fight , move , update resources , govern functions lie here
            //soldier's damage should be set according to the fear rate at each turn
            this.currentPlayer = Governance.getNextPlayer(this.currentPlayer);
            gameController.setCurrentUser(this.currentPlayer);
            gameController.prepareForNextPlayer(this.currentPlayer);

            //reset the map and all of its components
            Label turnText = new Label("pLAyEr: " + this.currentPlayer.getUsername());
            turnText.setFont(Font.font("monospace", FontWeight.BOLD, 100));
            turnText.setTextFill(Color.DARKSLATEBLUE);
            root.getChildren().add(turnText);
            int index = root.getChildren().indexOf(turnText);
            turnText.setLayoutX(400);
            turnText.setLayoutY(300);

            Timeline twinkle = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(turnText.opacityProperty(), MAX_OPACITY)),
                    new KeyFrame(TWINKLE_DURATION.divide(2), new KeyValue(turnText.opacityProperty(), MIN_OPACITY)),
                    new KeyFrame(TWINKLE_DURATION, new KeyValue(turnText.opacityProperty(), MAX_OPACITY))
            );
            twinkle.setCycleCount((int) 1.2);
            AnimationManager.timers.add(twinkle);
            twinkle.play();
            twinkle.setOnFinished(eve -> {
                root.getChildren().get(index).setVisible(false);
                resetMapForNextTurn();
            });
        });
        root.getChildren().add(addButton);
    }

    private void resetMapForNextTurn() {
        resetBuffers();
        for (User empire : Governance.getEmpires()) {
            for (Building building : empire.getGovernance().getBuildings()) {
                if (building.isOnFire()) {
                    building.takeDamage(50);
                    building.addTurnsOnFire();
                }
            }
        }
        scroll(0, 0);
        clipBoardBox = new VBox();
        createBarBook();
        enterMainBar();
    }

    private void addShortcutKeys() {
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if ("Q".equals(event.getCode().getName())) {
                primaryStage.close();
                System.exit(0);
            } else if ("E".equals(event.getCode().getName())) {
                AnimationManager.freezeTime();
                root.getChildren().add(overlayPane);
                pauseWindow.showAndWait();
                root.getChildren().remove(overlayPane);
            } else if ("V".equals(event.getCode().getName())) {
                if (!clipboardPaneUp) {
                    clipBoardBox = extractClipBoard();
                    navar.setPrefWidth(160);
                    navar.setLayoutX(1380);
                } else {
                    if (clipBoardBox != null) root.getChildren().remove(clipBoardBox);
                    navar.setPrefWidth(80);
                    navar.setLayoutX(1480);
                    clipBoardBox = null;
                }
                clipboardPaneUp = !clipboardPaneUp;
            } else if ("P".equals(event.getCode().getName())) {
                if (popularityFactorsBar != null && popularityFactorsBar.getChildren().size() != 0 && popularityFactorsBar.getChildren().get(0).isVisible())
                    enterMainBar();
                else enterPopularityBar();
            }
        });
        pauseWindow.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if ("W".equals(event.getCode().getName())) {
                pauseWindow.close();
                AnimationManager.startTime();
            }
        });
    }

    private void selectBuildingSetUp() {
        Button addButton = new Button();
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/select.png").toExternalForm()));
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        addButton.setGraphic(imageView);
        addButton.setLayoutX(1485);
        addButton.setLayoutY(120);
        addButton.setOnMouseClicked(mouseEvent -> {
            selectActive = !selectActive;
            if (selectActive)
                imageView.setFitWidth(35);
            else
                imageView.setFitWidth(25);
            imageView.setPreserveRatio(true);
        });

        root.getChildren().add(addButton);
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getScreenX() > 1470) return;
            if (event.getScreenY() > 600) return;
            if (selectActive) {
                selectBuilding(event);
            }
        });
    }

    private void deleteButton(Button button) {
        if (button == null) return;
        isDeleteActive = !isDeleteActive;
        if (isDeleteActive) {
            button.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/selected_delete.png").toExternalForm())));
            Image cursorImage = new Image(GameMenuControl.class.getResource("/Images/delete_cursor_2.png").toExternalForm());
            primaryStage.getScene().setCursor(new ImageCursor(cursorImage));
        } else {
            button.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/delete.png").toExternalForm())));
            primaryStage.getScene().setCursor(null);
        }
    }

    private VBox clipBoardBox = null;

    private void clipBoardSetUp() {
        Button addButton = new Button();
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/clipboard.png").toExternalForm()));
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        addButton.setGraphic(imageView);
        addButton.setLayoutX(1485);
        addButton.setLayoutY(65);

        addButton.setOnMouseClicked(mouseEvent -> {
            if (!clipboardPaneUp) {
                clipBoardBox = extractClipBoard();
                navar.setPrefWidth(160);
                navar.setLayoutX(1380);
            } else {
                if (clipBoardBox != null) root.getChildren().remove(clipBoardBox);
                navar.setPrefWidth(80);
                navar.setLayoutX(1480);
                clipBoardBox = null;
            }
            clipboardPaneUp = !clipboardPaneUp;
        });
        root.getChildren().add(addButton);
    }

    private int counter = 0;
    private boolean imFromClipboard = false;

    private VBox extractClipBoard() {
        if (copiedBuildings == null || copiedBuildings.size() == 0) return null;
        counter = 0;
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        Label label = new Label("clipboard:");
        label.setStyle("-fx-alignment: baseline-left ; -fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-font-size: 25; -fx-font-weight: bold");
        vBox.getChildren().add(label);
        ImageView imageView;
        while (counter < 5) {
            if (copiedBuildings.size() - 1 - counter < 0) break;
            imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/buildings/" +
                            copiedBuildings.get(copiedBuildings.size() - 1 - counter).getType().getName() + ".png")
                    .toExternalForm()));
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setSmooth(true);
            imageView.setCache(true);
            imageView.setOnMouseClicked(mouseEvent -> {
                if (copiedBuildings == null || copiedBuildings.size() == 0) return;
                int index = (int) Math.floor((mouseEvent.getSceneY() - 180) / 90);
                System.out.println("copied number: " + index + " , " + mouseEvent.getScreenY() + " , " + mouseEvent.getY() + " , " + mouseEvent.getSceneY());
                if (copiedBuildings.size() - counter < 0) return;
                Building building = copiedBuildings.get(copiedBuildings.size() - 1 - index);
                copiedBuildings.remove(copiedBuildings.size() - 1 - index);
                copiedBuildings.add(building);
                isPasteActive = true;
                imFromClipboard = true;
            });
            vBox.getChildren().add(imageView);
            counter++;
        }
        vBox.setLayoutX(1400);
        vBox.setLayoutY(230);
        root.getChildren().add(vBox);
        return vBox;
    }

    private void copySetUp() {
        Button addButton = new Button();
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/copy.png").toExternalForm()));
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        addButton.setGraphic(imageView);
        addButton.setLayoutX(1485);
        addButton.setLayoutY(10);
        addButton.setOnAction(event -> {
            isCopyActive = !isCopyActive;
            if (isCopyActive) {
                imageView.setFitWidth(35);
                imageView.setPreserveRatio(true);
                if (imFromClipboard) {
                    isPasteActive = true;
                    imageView.setImage(new Image(GameMenuControl.class.getResource("/Images/paste.png").toExternalForm()));
                    imFromClipboard = false;
                }
            } else {
                if (!imFromClipboard) {
                    isPasteActive = false;
                } else {
                    imFromClipboard = false;
                }
                imageView.setImage(new Image(GameMenuControl.class.getResource("/Images/copy.png").toExternalForm()));
                imageView.setFitWidth(25);
                imageView.setPreserveRatio(true);
                imFromClipboard = false;
            }
        });
        root.getChildren().add(addButton);
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getScreenX() > 1470) return;
            if (event.getScreenY() > 600) return;
            if (isCopyActive) {
                if (!isPasteActive) {
                    getPaneIndex(event.getScreenX(), event.getScreenY(), imageView);
                } else {
                    paste(event.getScreenX(), event.getScreenY());
                    imageView.setImage(new Image(GameMenuControl.class.getResource("/Images/copy.png").toExternalForm()));
                }
            }
        });
    }

    private void selectBuilding(MouseEvent event) {
        int xIndex = (int) Math.floor(event.getScreenX() / TILE_SIZE);
        int yIndex = (int) Math.floor(event.getScreenY() / TILE_SIZE);
        ArrayList<Building> tileBuildings = linkedHouses.get(panes[xIndex][yIndex]).getBuildings();
        if (tileBuildings == null || tileBuildings.size() == 0) return;
        selectedBuilding.put(currentPlayer, tileBuildings.get(tileBuildings.size() - 1));
        if (selectedBuilding.get(currentPlayer).getType() == BuildingEnum.MARKET ||
                selectedBuilding.get(currentPlayer).getType() == BuildingEnum.BARRACKS ||
                selectedBuilding.get(currentPlayer).getType() == BuildingEnum.MERCENARY_POST) {
            for (java.util.Map.Entry<ImageView, Building> entry : buildings.entrySet()) {
                if (entry.getValue() == selectedBuilding.get(currentPlayer))
                    entry.getKey().setOnMouseClicked(mouseEvent -> {
                        if (selectedBuilding.get(currentPlayer).getType() == BuildingEnum.MARKET)
                            try {
                                ShopMenuControl.setCurentUser(currentPlayer);
                                LoginRegisterMenuControl.openAddress("/FXML/shopMenu.fxml");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        else
                            enterDropUnitBar(xIndex, yIndex);
                    });
            }
        }
    }

    private void paste(double screenX, double screenY) {
        if (screenX > 1470) return;
        if (screenY > 600) return;
        int xIndex = (int) Math.floor(screenX / TILE_SIZE);
        int yIndex = (int) Math.floor(screenY / TILE_SIZE);

        //saving for undo:
        lastTileUndo = linkedHouses.get(panes[xIndex][yIndex]).copy();
        lastLinkedHousesUndo = linkedHouses;
        lastBuildingImageViewUndo = buildingImageView;
        lastBuildingsUndo = buildings;
        doWeHaveUndo = true;

        linkedHouses.get(panes[xIndex][yIndex]).getBuildings().add(copiedBuildings.get(copiedBuildings.size() - 1));
        isPasteActive = false;
        copiedBuildings.remove(copiedBuildings.size() - 1);
        indexOfHoveringTilesTogether = -1;
        indexOfHoveringTile = -1;
        indexOfHoveringUnit = -1;
        indexOfHoveringBuilding = -1;
        for (int i = 0; i < 50; i++)
            addTile(root, i);
        for (int i = 0; i < 50; i++) {
            int[] coordinates = assignTileToScreen(i);
            setUpTile(panes[i % 10][i / 10], map.getTile(coordinates[1], coordinates[0]));
        }
    }

    private void getPaneIndex(double screenX, double screenY, ImageView imageView) {
        if (screenX > 1470) return;
        if (screenY > 600) return;
        int xIndex = (int) Math.floor(screenX / TILE_SIZE);
        int yIndex = (int) Math.floor(screenY / TILE_SIZE);
        System.out.println("this is the pane on which we copy form: " + xIndex + " and " + yIndex);
        Tile selected = linkedHouses.get(panes[xIndex][yIndex]);
        if (selected.getBuildings() == null || selected.getBuildings().size() == 0)
            System.out.println("no building on this tile");
        else
            System.out.println("I found this building: " + selected.getBuildings().get(0).getType());
        if (selected.getBuildings() == null || selected.getBuildings().size() == 0) return;
        copiedBuildings.add(selected.getBuildings().get(selected.getBuildings().size() - 1));
        imageView.setImage(new Image(GameMenuControl.class.getResource("/Images/paste.png").toExternalForm()));
        isPasteActive = true;
    }

    private int indexOfHoveringTilesTogether = -1;

    private void selectArea() {
        Rectangle selectionArea = new Rectangle();
        selectionArea.setFill(Color.TRANSPARENT);
        selectionArea.setStroke(Color.BLUE);
        root.setOnMousePressed(event -> {
            selectionArea.setX(event.getX());
            selectionArea.setY(event.getY());
        });

        root.setOnMouseDragged(event -> {
            if (selectionArea.getY() > 600)
                return;
            if (indexOfHoveringTile >= 0) {
                root.getChildren().remove(indexOfHoveringTile);
                indexOfHoveringTile = -1;
            }
            tilesOccupied = true;
            String infoStr = "";
            double width = event.getX() - selectionArea.getX();
            double height = event.getY() - selectionArea.getY();
            selectionArea.setWidth(width);
            selectionArea.setHeight(height);

            int buildingCounter = 0;
            int unitCounter = 0;
            int minRate = 0;
            int maxRate = 0;
            int sumRate = 0;
            int yourUnits = 0;
            int averageRate = 0;

            for (Node node : root.getChildren()) {
                if (node.getBoundsInParent().intersects(selectionArea.getBoundsInParent())) {
                    if (node instanceof Pane) {
                        Tile tile = findTileByPane((Pane) node);
                        infoStr += "Texture: " + tile.getTexture().getName() + "\n";
                        if (tile.getBuildings() != null) {
                            buildingCounter += tile.getBuildings().size();
                            for (Building building : tile.getBuildings()) {
                                maxRate = Math.max(maxRate, building.getType().getRate());
                                minRate = Math.min(minRate, building.getType().getRate());
                                sumRate += building.getType().getRate();
                            }
                        }
                        if (tile.getPlayersUnits() != null)
                            unitCounter += tile.getPlayersUnits().size();
                        if (tile.findYourUnits(currentPlayer) != null)
                            yourUnits += tile.findYourUnits(currentPlayer).size();
                    }
                }
            }

            averageRate = buildingCounter == 0 ? 0 : sumRate / buildingCounter;
            infoStr += "Building number: " + buildingCounter + "\nUnit number: " + unitCounter + "\nYour units: " + yourUnits
                    + "\nMin rate: " + minRate + "\nMax rate: " + maxRate + "\nAverage rate: " + averageRate;
            Label info = simpleLabelStyler(infoStr);
            info.setStyle("-fx-alignment: center; -fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-background-color: rgba(40,37,37,0.38);" +
                    "; -fx-font-size: 25; -fx-font-weight: bold");
            if (!hoverActive) return;
            if (indexOfHoveringTilesTogether >= 0)
                root.getChildren().set(indexOfHoveringTilesTogether, info);
            else {
                root.getChildren().add(info);
                indexOfHoveringTilesTogether = root.getChildren().indexOf(info);
            }
        });

        root.setOnMouseReleased(event -> {
            if (indexOfHoveringTilesTogether >= 0) root.getChildren().remove(indexOfHoveringTilesTogether);
            indexOfHoveringTilesTogether = -1;
            tilesOccupied = false;
        });

    }

    private int upBuffer = 0, downBuffer = 0, rightBuffer = 0, leftBuffer = 0;
    private final int lock = 5;

    private void joyStick() {
        Button button = new Button();
        button.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/joystick.png").toExternalForm(),
                100, 100, false, false)));
        button.setStyle("-fx-background-color: transparent");
        button.setLayoutX(20);
        button.setLayoutY(500);
        // Store initial coordinates for determination of direction
        AtomicReference<Double> initialX = new AtomicReference<>((double) 0);
        AtomicReference<Double> initialY = new AtomicReference<>((double) 0);

        button.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                initialX.set(event.getSceneX());
                initialY.set(event.getSceneY());
            }
        });

        button.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - initialX.get();
            double offsetY = event.getSceneY() - initialY.get();

            // Check for vertical or horizontal drag
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                if (offsetX > 0) {
                    // Right drag
                    if (++rightBuffer >= lock) {
                        scroll(1, 0);
                        resetBuffers();
                    }
                } else {
                    // Left drag
                    if (++leftBuffer >= lock) {
                        scroll(-1, 0);
                        resetBuffers();
                    }
                }
            } else {
                if (offsetY > 0) {
                    // Down drag
                    if (++downBuffer >= lock) {
                        scroll(0, -1);
                        resetBuffers();
                    }
                } else {
                    // Up drag
                    if (++upBuffer >= lock) {
                        scroll(0, 1);
                        resetBuffers();
                    }
                }
            }
        });
        root.getChildren().add(button);
    }

    private void scroll(int xChange, int yChange) {
        AnimationManager.freezeTime();
        xCenter += xChange;
        yCenter += yChange;
        for (int i = 0; i < 50; i++)
            addTile(root, i);
        for (int i = 0; i < 50; i++) {
            int[] coordinates = assignTileToScreen(i);
            setUpTile(panes[i % 10][i / 10], map.getTile(coordinates[1], coordinates[0]));
        }

        AnimationManager.startTime();
    }

    private void resetBuffers() {
        rightBuffer = 0;
        downBuffer = 0;
        upBuffer = 0;
        leftBuffer = 0;
    }

    private void createGamePane(BuildingType currentSet) {

    }

    private void handleMouseScroll(ScrollEvent event) {
        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
        double change = scaleTransform.getX();
        if (change > 2.5) change = 2.5;
        if (change < 0.5) change = 0.5;
        double secChange = scaleTransform.getY();
        if (secChange > 2.4) secChange = 2.5;
        if (secChange < 0.5) secChange = 0.5;
        scaleTransform.setX(change * scaleFactor);
        scaleTransform.setY(secChange * scaleFactor);
        event.consume();
    }

    private void setBarScene(String address) {
        barScene = new ImageView(new Image(GameMenuControl.class.getResource(address).toExternalForm()));
        barScene.setFitWidth(1530);
        barScene.setPreserveRatio(true);
        barScene.setSmooth(true);
        barScene.setCache(true);
        barScene.setY(567);
        root.getChildren().add(barScene);
    }

    private void addBar(Pane root) {
        setBarScene("/Images/governance.png"); //adds 1 child. we also have 50 children from addTile.
        createBarBook(); //adds 3 more children.
        enterMainBar();
    }

    private void createBarBook() {
        Label population = new Label(currentPlayer.getGovernance().getUnemployedPopulation() + " / " +
                currentPlayer.getGovernance().getMaximumPopulation());
        population.setStyle("-fx-alignment: center; -fx-font-family: x fantasy; -fx-font-style: italic; -fx-text-fill: #3d3535; -fx-padding: 20 0 0 8; -fx-font-weight: bold; -fx-font-size: 15");
        ImageView coins = new ImageView(new Image(GameControlTest.class.getResource("/Images/coin.png").toExternalForm(), 20, 20, false, false));
        Label coin = new Label("" + currentPlayer.getGovernance().getGold());
        coin.setStyle("-fx-alignment: center; -fx-font-family: x fantasy; -fx-font-style: italic; -fx-text-fill: #3d3535; -fx-font-weight: bold; -fx-font-size: 15");
        HBox treasure = new HBox();
        treasure.getChildren().addAll(coins, coin);
        treasure.setPadding(new Insets(20, 0, -20, 0));
        Label popularity = new Label("" + currentPlayer.getGovernance().getPopularity());
        popularity.setStyle("-fx-alignment: center; -fx-font-family: x fantasy; -fx-font-style: italic; -fx-text-fill: #3d3535; -fx-padding: 0 0 0 23; -fx-font-weight: bold; -fx-font-size: 25");

        Button mask = new Button();
        ImageView masks = new ImageView(new Image(GameControlTest.class.getResource("/Images/masks.png").toExternalForm(), 35, 35, false, false));
        mask.setStyle("-fx-background-color: transparent; -fx-padding: -6 -80 6 80;");
        mask.setGraphic(masks);
        mask.setOnMouseClicked(mouseEvent -> {
            if (popularityFactorsBar != null && popularityFactorsBar.getChildren().size() != 0 && popularityFactorsBar.getChildren().get(0).isVisible())
                enterMainBar();
            else enterPopularityBar();
        });

        Button changeFactorButton = new Button();
        ImageView change = new ImageView(new Image(GameControlTest.class.getResource("/Images/changeFactor.png").toExternalForm(), 25, 25, false, false));
        changeFactorButton.setStyle("-fx-background-color: transparent;");
        changeFactorButton.setGraphic(change);
        changeFactorButton.setOnMouseClicked(mouseEvent -> {
            System.out.println("playing with change");
            if (changeFactorsBar != null && changeFactorsBar.getChildren().size() != 0 && changeFactorsBar.getChildren().get(0).isVisible()) {
                currentPlayer.getGovernance().setFearRate((int) ((Slider) changeFactorsBar.getChildren().get(3)).getValue());
                currentPlayer.getGovernance().setFoodRate((int) ((Slider) changeFactorsBar.getChildren().get(4)).getValue());
                currentPlayer.getGovernance().setTaxRate((int) ((Slider) changeFactorsBar.getChildren().get(5)).getValue());
                enterMainBar();
            } else enterChangeFactorBar();
        });

        Button deleteButton = new Button();
        ImageView deleteImage = new ImageView(new Image(GameControlTest.class.getResource("/Images/delete.png").toExternalForm(), 25, 25, false, false));
        deleteButton.setStyle("-fx-background-color: transparent;");
        deleteButton.setGraphic(deleteImage);
        deleteButton.setOnMouseClicked(mouseEvent -> deleteButton(deleteButton));
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getScreenX() > 1470) return;
            if (event.getScreenY() > 600) return;
            if (isDeleteActive) {
                indexOfHoveringUnit = -1;
                indexOfHoveringBuilding = -1;
                indexOfHoveringTile = -1;
                indexOfHoveringTilesTogether = -1;
                deleteAndHeadBackIfNecessary(event, deleteButton);
            }
        });


        Button undoButton = new Button();
        ImageView undoImage = new ImageView(new Image(GameControlTest.class.getResource("/Images/undo.png").toExternalForm(), 25, 25, false, false));
        undoButton.setStyle("-fx-background-color: transparent;");
        undoButton.setGraphic(undoImage);
        undoButton.setOnMouseClicked(mouseEvent -> {
            if (doWeHaveUndo) {
                indexOfHoveringUnit = -1;
                indexOfHoveringBuilding = -1;
                indexOfHoveringTile = -1;
                indexOfHoveringTilesTogether = -1;
                doWeHaveUndo = false;
                map.getTile(lastTileUndo.getY(), lastTileUndo.getX()).setTrees(lastTileUndo.getTrees());
                map.getTile(lastTileUndo.getY(), lastTileUndo.getX()).setPlayersUnits(lastTileUndo.getPlayersUnits());
                map.getTile(lastTileUndo.getY(), lastTileUndo.getX()).setBuildings(lastTileUndo.getBuildings());
                linkedHouses = lastLinkedHousesUndo;
                buildingImageView = lastBuildingImageViewUndo;
                System.out.println("number of buildings: " + lastTileUndo.getBuildings().size());
                for (int i = 0; i < 50; i++)
                    addTile(root, i);
                for (int i = 0; i < 50; i++) {
                    int[] coordinates = assignTileToScreen(i);
                    setUpTile(panes[i % 10][i / 10], map.getTile(coordinates[1], coordinates[0]));
                }
            }
        });

        Button resourceButton = new Button();
        ImageView resource = new ImageView(new Image(GameControlTest.class.getResource("/Images/resources.png").toExternalForm(), 25, 25, false, false));
        resourceButton.setStyle("-fx-background-color: transparent;");
        resourceButton.setGraphic(resource);
        resourceButton.setOnMouseClicked(mouseEvent -> showResourceMenu());


        miniMap = new GridPane();
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/minimapFrame.jpg")
                .toExternalForm()));
        imageView.setFitWidth(TILE_SIZE + 22);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        resetAddMiniMapDetails();


        // Create the overlay pane with a dark shade
        overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        overlayPane.setPrefHeight(800);
        overlayPane.setPrefWidth(1530);


        // Create the pause window
        pauseWindow = createPauseWindow();
        Button optionButton = new Button();
        ImageView option = new ImageView(new Image(GameControlTest.class.getResource("/Images/option.png").toExternalForm(), 25, 25, false, false));
        optionButton.setStyle("-fx-background-color: transparent;");
        optionButton.setGraphic(option);
        optionButton.setOnMouseEntered(mouseEvent -> optionButton.setGraphic(new ImageView(new Image(GameControlTest.class.getResource("/Images/option_hover.png").toExternalForm(), 25, 25, false, false))));
        optionButton.setOnMouseExited(mouseEvent -> optionButton.setGraphic(new ImageView(new Image(GameControlTest.class.getResource("/Images/option.png").toExternalForm(), 25, 25, false, false))));
        optionButton.setOnMouseClicked(mouseEvent -> {
            AnimationManager.freezeTime();
            root.getChildren().add(overlayPane);
            pauseWindow.showAndWait();
            root.getChildren().remove(overlayPane);
        });

        // Create the brief window
        Stage briefWindow = createBriefWindow();
        Button briefButton = new Button();
        ImageView brief = new ImageView(new Image(GameControlTest.class.getResource("/Images/briefing.png").toExternalForm(), 25, 25, false, false));
        briefButton.setStyle("-fx-background-color: transparent;");
        briefButton.setGraphic(brief);
        briefButton.setOnMouseEntered(mouseEvent -> briefButton.setGraphic(new ImageView(new Image(GameControlTest.class.getResource("/Images/brefing_hover.png").toExternalForm(), 25, 25, false, false))));
        briefButton.setOnMouseExited(mouseEvent -> briefButton.setGraphic(new ImageView(new Image(GameControlTest.class.getResource("/Images/briefing.png").toExternalForm(), 25, 25, false, false))));
        briefButton.setOnMouseClicked(mouseEvent -> {
            AnimationManager.freezeTime();
            root.getChildren().add(overlayPane);
            briefWindow.showAndWait();
            root.getChildren().remove(overlayPane);
        });


        HBox buttons = new HBox(changeFactorButton, deleteButton, undoButton, optionButton, briefButton, resourceButton);
        buttons.setSpacing(15);
        buttons.getStylesheets().add(GameControlTest.class.getResource("/CSS/game.css").toExternalForm());
        buttons.getStyleClass().add("background");

        VBox sample = new VBox();
        sample.getChildren().addAll(popularity, population, mask, treasure);
        sample.setSpacing(-28);
        barBook.setSpacing(45);
        if (barBook != null && barBook.getChildren().size() != 0) {
            barBook.getChildren().set(0, sample);
            barBook.getChildren().set(1, buttons);
        } else
            barBook.getChildren().addAll(sample, buttons);
        barBook.setLayoutY(660);
        barBook.setLayoutX(1150);
        imageView.setLayoutY(450);
        imageView.setLayoutX(1300);
        miniMap.setLayoutY(474);
        miniMap.setLayoutX(1318);

        if (root.getChildren().contains(barBook)) {
            root.getChildren().set(51, barBook);
            root.getChildren().set(52, imageView);
            root.getChildren().set(53, miniMap);
        } else
            root.getChildren().addAll(barBook, imageView, miniMap);
    }

    private void showResourceMenu() {
        AnimationManager.freezeTime();
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: linear-gradient(rgba(101, 0, 0, 0.74) 10%, rgba(155, 2, 2, 0.74) 20%," +
                "rgba(155, 57, 2, 0.74) 35%, rgba(138, 89, 0, 0.74) 45%);");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ImageView[] imageViews = new ImageView[20];
        Image[] lively = new Image[20];
        Image[] deadLooking = new Image[20];
        ArrayList<ResourceEnum> resourceEnums = createResourceList();
        for (int i = 0; i < 20; i++) {
            Image image = new Image(GameMenuControl.class.getResource("/Images/newResources/" + resourceEnums.get(i).getName()
                    + "_hover.png").toExternalForm());
            Image image2 = new Image(GameMenuControl.class.getResource("/Images/newResources/" + resourceEnums.get(i).getName()
                    + ".png").toExternalForm());
            imageViews[i] = new ImageView(image);
            imageViews[i].setFitWidth(35);
            imageViews[i].setFitHeight(40);
            lively[i] = image2;
            deadLooking[i] = image;
        }

        Label[] labels = new Label[20];
        for (int i = 0; i < 20; i++) {
            labels[i] = new Label("" + currentPlayer.getGovernance().getResourceAmount(resourceEnums.get(i)));
            labels[i].setVisible(false);
            labels[i].setStyle("-fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-padding: 0 -0 0 0; -fx-font-size: 18");
        }

        for (int i = 0; i < 20; i++) {
            int index = i;
            imageViews[i].setOnMouseEntered(event -> {
                imageViews[index].setImage(lively[index]);
                labels[index].setVisible(true);
            });
            imageViews[i].setOnMouseExited(event -> {
                imageViews[index].setImage(deadLooking[index]);
                labels[index].setVisible(false);
            });
        }

        gridPane.snapSpaceX(-20);
        gridPane.snapSpaceY(-20);
        HBox hBox;
        for (int i = 0; i < 20; i++) {
            hBox = new HBox();
            hBox.setSpacing(8);
            hBox.getChildren().addAll(imageViews[i], labels[i]);
            gridPane.add(hBox, i % 5, i / 5);
        }

        Stage resourceWindow = new Stage();
        resourceWindow.initModality(Modality.APPLICATION_MODAL);
        resourceWindow.initOwner(primaryStage);
        Button resumeButton = buttonCreator("back");
        resumeButton.setOnMouseClicked(event -> {
            resourceWindow.close();
            AnimationManager.startTime();
        });
        gridPane.add(resumeButton, 2, 5);
        Scene sourceScene = new Scene(gridPane, 350, 300);
        root.getChildren().add(overlayPane);
        resourceWindow.setScene(sourceScene);
        resourceWindow.showAndWait();
        root.getChildren().remove(overlayPane);
    }

    private ArrayList<ResourceEnum> createResourceList() {
        ArrayList<ResourceEnum> result = new ArrayList<>();
        for (ResourceEnum value : ResourceEnum.values()) {
            if (value != ResourceEnum.NULL && value != ResourceEnum.HORSEANDBOW && value != ResourceEnum.OIL)
                result.add(value);
        }
        return result;
    }

    private Stage createBriefWindow() {
        Stage briefWindow = new Stage();
        briefWindow.initModality(Modality.APPLICATION_MODAL);
        briefWindow.initOwner(primaryStage);

        VBox briefContent = new VBox();
        briefContent.setStyle("-fx-background-color: linear-gradient(rgba(101, 0, 0, 0.74) 10%, rgba(155, 2, 2, 0.74) 20%," +
                "rgba(155, 57, 2, 0.74) 35%, rgba(138, 89, 0, 0.74) 45%);;");
        briefContent.setSpacing(10);

        Label label = new Label();
        label.setStyle("-fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-font-size: 22");
        String briefing = "Empires:";
        for (User empire : Governance.getEmpires()) {
            briefing += "\nname: " + empire.getUsername() + "(aka " + empire.getNickname() + ") , gold: " +
                    empire.getGovernance().getGold();
            if (empire.getSlogan() != null) briefing += "---" + empire.getSlogan();
        }

        Button resumeButton = buttonCreator("Resume Game");
        resumeButton.setOnMouseClicked(event -> {
            briefWindow.close();
            AnimationManager.startTime();
        });

        label.setText(briefing);
        label.setPadding(new Insets(0, -20, 0, 20));
        briefContent.getChildren().addAll(label, resumeButton);
        briefContent.setAlignment(Pos.CENTER);
        Scene briefScene = new Scene(briefContent, 400, 400);
        briefWindow.setScene(briefScene);
        return briefWindow;
    }

    private Stage createPauseWindow() {
        Stage pauseWindow = new Stage();
        pauseWindow.initModality(Modality.APPLICATION_MODAL);
        pauseWindow.initOwner(primaryStage);

        VBox pauseContent = new VBox();
        pauseContent.setStyle("-fx-background-color: linear-gradient(rgba(101, 0, 0, 0.74) 10%, rgba(155, 2, 2, 0.74) 20%," +
                "rgba(155, 57, 2, 0.74) 35%, rgba(138, 89, 0, 0.74) 45%);");
        pauseContent.setSpacing(10);
        Button helpButton = buttonCreator("help");
        Button quitButton = buttonCreator("Quit Mission");
        Button exitButton = buttonCreator("Exit Game");
        Button resumeButton = buttonCreator("Resume Game");
        Button backToMain = buttonCreator("back");
        Hyperlink hyperlink = new Hyperlink("Open Link");
        hyperlink.setStyle("-fx-font-size: 22");
        Label label = new Label("Looks like you need some help!\nClick the link below to get some\nguidelines. And" +
                " yes, I know; I'm\nlazy as a sloth on a hammock\nsipping margaritas :)");
        label.setStyle("-fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-padding: 0 -0 0 0; -fx-font-size: 22");
        Label label2 = new Label("Oh, forgot to tell you the shortcut keys:\nV -> open clipboard\n" +
                "P -> open popularity menu\nE -> enter pause window\nW -> exit pause window\nQ -> exit game (careful with that!)");
        label2.setStyle("-fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-padding: 10 -10 0 10; -fx-font-size: 22");
        helpButton.setOnMouseClicked(event -> {
            pauseContent.getChildren().removeAll(helpButton, quitButton, exitButton, resumeButton);
            pauseContent.getChildren().addAll(label, hyperlink, label2, backToMain);
        });
        hyperlink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://steamcommunity.com/app/40970/guides/#scrollTop=0"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        backToMain.setOnMouseClicked(event -> {
            pauseContent.getChildren().removeAll(label, hyperlink, label2, backToMain);
            pauseContent.getChildren().addAll(helpButton, quitButton, exitButton, resumeButton);
        });
        resumeButton.setOnMouseClicked(event -> {
            pauseWindow.close();
            AnimationManager.startTime();
        });
        exitButton.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Attention");
            alert.setHeaderText("Exit");
            alert.setContentText("Are you sure you want to leave?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                pauseWindow.close();
                javafx.application.Platform.exit();
            }
        });

        //todo: deal with exit mission

        pauseContent.getChildren().addAll(helpButton, quitButton, exitButton, resumeButton);
        pauseContent.setAlignment(Pos.CENTER);
        Scene pauseScene = new Scene(pauseContent, 400, 500);
        pauseWindow.setScene(pauseScene);
        return pauseWindow;
    }

    private Button buttonCreator(String name) {
        Button button = new Button(name);
        button.getStylesheets().add(GetStyle.class.getResource("/CSS/shopAndTrade.css").toExternalForm());
        button.getStyleClass().add("Button");
        return button;
    }

    private void resetAddMiniMapDetails() {
        miniMap.setHgap(0);
        miniMap.setVgap(-15);
        //first, we set tile boundaries.
        int minXIndex = Math.max(xCenter - 8, 0);
        int maxXIndex = Math.min(minXIndex + 7, map.getLength() - 1);
        if (maxXIndex == map.getLength() - 1) minXIndex = maxXIndex - 7;
        int minYIndex = Math.max(yCenter - 8, 0);
        int maxYIndex = Math.min(minYIndex + 7, map.getWidth() - 1);
        if (maxYIndex == map.getWidth() - 1) minYIndex = maxYIndex - 7;

        //now, we go through tiles and pick appropriate pictures.
        Image waterm = new Image(GameMenuControl.class.getResource("/Images/textures/sea.jpg").toExternalForm());
        Image treem = new Image(GameMenuControl.class.getResource("/Images/textures/tree.jpg").toExternalForm());
        Image peoplem = new Image(GameMenuControl.class.getResource("/Images/textures/people.jpg").toExternalForm());
        Image buildingm = new Image(GameMenuControl.class.getResource("/Images/textures/building.jpg").toExternalForm());
        Image landm = new Image(GameMenuControl.class.getResource("/Images/textures/earth.jpg").toExternalForm());
        Image selectedTile;

        Tile selected;
        int rowCounter = 0;
        int columnCounter = 0;
        for (int j = minYIndex; j <= maxYIndex; j++) {
            for (int i = minXIndex; i <= maxXIndex; i++) {
                selected = map.getTile(j, i);
                if (selected.getBuildings() != null && selected.getBuildings().size() != 0) {
                    selectedTile = buildingm;
                } else if (selected.getPlayersUnits() != null && selected.getPlayersUnits().size() != 0) {
                    selectedTile = peoplem;
                } else if (selected.getTrees() != null && selected.getTrees().size() != 0) {
                    selectedTile = treem;
                } else {
                    if (selected.getTexture().getTileGraphicTexture() == TileGraphicTexture.LAND)
                        selectedTile = landm;
                    else if (selected.getTexture().getTileGraphicTexture() == TileGraphicTexture.SEA)
                        selectedTile = waterm;
                    else
                        selectedTile = treem;
                }

                if (columnCounter++ == 0 || rowCounter == 0) continue;
                miniMap.add(createLittleImage(selectedTile), columnCounter, rowCounter);
                columnCounter++;
            }
            columnCounter = 0;
            rowCounter++;
        }
    }

    private ImageView createLittleImage(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(30);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    private void deleteAndHeadBackIfNecessary(MouseEvent event, Button button) {
        if (event.getScreenX() > 1470) return;
        if (event.getScreenY() > 600) return;
        int xIndex = (int) Math.floor(event.getScreenX() / TILE_SIZE);
        int yIndex = (int) Math.floor(event.getScreenY() / TILE_SIZE);
        Tile selected = linkedHouses.get(panes[xIndex][yIndex]);

        //saving for undo:
        lastTileUndo = selected.copy();
        lastLinkedHousesUndo = linkedHouses;
        lastBuildingImageViewUndo = buildingImageView;
        lastBuildingsUndo = buildings;
        doWeHaveUndo = true;

        boolean[] state = new boolean[3];

        if ((state[0] = (selected.getBuildings() == null || selected.getBuildings().size() == 0)) &&
                (state[1] = (selected.getTrees() == null || selected.getTrees().size() == 0)) &&
                (state[2] = (selected.findYourUnits(currentPlayer) == null || selected.findYourUnits(currentPlayer).size() == 0)))
            return;

        if (!state[0])
            selected.getBuildings().remove(selected.getBuildings().size() - 1);
        else if (!state[1])
            selected.getTrees().remove(selected.getTrees().size() - 1);
        else
            selected.removeAUnit(selected.findYourUnits(currentPlayer).get(selected.findYourUnits(currentPlayer).size() - 1));

        button.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/delete.png").toExternalForm(), 25, 25, false, false)));
        primaryStage.getScene().setCursor(null);
        isDeleteActive = false;
        for (int i = 0; i < 50; i++)
            addTile(root, i);
        for (int i = 0; i < 50; i++) {
            int[] coordinates = assignTileToScreen(i);
            setUpTile(panes[i % 10][i / 10], map.getTile(coordinates[1], coordinates[0]));
        }
    }

    private void enterChangeFactorBar() {
        System.out.println("trying to enter");
        if (popularityFactorsBar != null)
            for (Node child : changeFactorsBar.getChildren()) {
                child.setVisible(false);
            }
        if (mainBar != null)
            for (Node child : mainBar.getChildren()) {
                child.setVisible(false);
            }
        if (unitBar != null)
            for (Node child : unitBar.getChildren()) {
                child.setVisible(false);
            }

        boolean first = changeFactorsBar.getChildren().size() == 0;
        barScene.setImage(new Image(GameMenuControl.class.getResource("/Images/governance.png").toExternalForm()));
        System.out.println("here");
        Slider fear = sliderMaker("fear rate:", currentPlayer.getGovernance().getFearRate(), -5, 5, first, 0);
        Slider food = sliderMaker("food rate: ", currentPlayer.getGovernance().getFearRate(), -2, 2, first, 1);
        Slider tax = sliderMaker("tax rate: ", currentPlayer.getGovernance().getFearRate(), -3, 8, first, 2);
        fear.setPadding(new Insets(-95, -125, 95, 125));
        food.setPadding(new Insets(-95, -125, 95, 125));
        tax.setPadding(new Insets(-95, -125, 95, 125));

        changeFactorsBar.setLayoutX(670);
        changeFactorsBar.setLayoutY(650);
        System.out.println("almost");
        if (first) {
            System.out.println("here");
            changeFactorsBar.getChildren().addAll(fear, food, tax);
            root.getChildren().add(changeFactorsBar);
        } else {
            System.out.println("or there");
            changeFactorsBar.getChildren().set(3, fear);
            changeFactorsBar.getChildren().set(4, food);
            changeFactorsBar.getChildren().set(5, tax);
        }
    }

    private Slider sliderMaker(String text, int initialValue, int min, int max, boolean first, int index) {
        Slider slider = new Slider();
        slider.getStylesheets().add(GetStyle.class.getResource("/CSS/game.css").toExternalForm());
        //slider.getStyleClass().add("Label");
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(initialValue);
        slider.setAccessibleText(text);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setPrefWidth(300);
        slider.setPrefHeight(30);
        slider.setMinorTickCount(0);
        slider.setMajorTickUnit(1);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            slider.setValue(Math.round(newVal.doubleValue()));
        });
        Label valueLabel = new Label();
        valueLabel.textProperty().bind(Bindings.format(text + "%.0f", slider.valueProperty()));
        valueLabel.setStyle("-fx-text-fill: #282525; -fx-font-weight: bold; -fx-font-size: 20;");
        if (first)
            changeFactorsBar.getChildren().add(valueLabel);
        else
            changeFactorsBar.getChildren().set(index, valueLabel);
        return slider;
    }

    private void enterPopularityBar() {
        if (changeFactorsBar != null)
            for (Node child : changeFactorsBar.getChildren()) {
                child.setVisible(false);
            }
        if (mainBar != null)
            for (Node child : mainBar.getChildren()) {
                child.setVisible(false);
            }
        if (unitBar != null)
            for (Node child : unitBar.getChildren()) {
                child.setVisible(false);
            }


        boolean first = popularityFactorsBar.getChildren().size() == 0;
        barScene.setImage(new Image(GameMenuControl.class.getResource("/Images/popularityMenu.png").toExternalForm()));

        int previousPopularity = currentPlayer.getGovernance().getPopularity();

        gameController.taxRateEffect();
        int effectedPopularity = currentPlayer.getGovernance().getPopularity();
        Label tax = setFactorLabel(effectedPopularity - previousPopularity);
        tax.setPadding(new Insets(-30, 0, 30, 0));

        gameController.churchEffect();
        effectedPopularity = currentPlayer.getGovernance().getPopularity();
        Label religion = setFactorLabel(effectedPopularity - previousPopularity);
        religion.setPadding(new Insets(-20, -200, 20, 180));

        gameController.foodRateEffect();
        effectedPopularity = currentPlayer.getGovernance().getPopularity();
        Label food = setFactorLabel(effectedPopularity - previousPopularity);
        food.setPadding(new Insets(40, 0, -40, 0));

        gameController.fearRateEffect();
        effectedPopularity = currentPlayer.getGovernance().getPopularity();
        Label fear = setFactorLabel(effectedPopularity - previousPopularity);
        fear.setPadding(new Insets(30, -200, -30, 180));


        int wholeChange = Integer.parseInt(fear.getText()) + Integer.parseInt(religion.getText()) +
                Integer.parseInt(tax.getText()) + Integer.parseInt(food.getText());
        Label whole = new Label("" + wholeChange);
        whole.setPadding(new Insets(-25, -250, 25, 250));
        if (wholeChange > 0) {
            whole.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 30;");
            whole.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/green.jpg").toExternalForm(), 45, 45, false, false)));
        } else if (wholeChange < 0) {
            whole.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 30;");
            whole.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/red.jpg").toExternalForm(), 45, 45, false, false)));
        } else {
            whole.setStyle("-fx-text-fill: #c7ac00; -fx-font-weight: bold; -fx-font-size: 30;");
            whole.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/yellow.jpg").toExternalForm(), 45, 45, false, false)));
        }

        popularityFactorsBar.setLayoutX(700);
        popularityFactorsBar.setLayoutY(600);

        if (first) {
            popularityFactorsBar.getChildren().addAll(food, fear, religion, tax, whole);
            root.getChildren().add(popularityFactorsBar);
        } else {
            popularityFactorsBar.getChildren().set(0, food);
            popularityFactorsBar.getChildren().set(1, fear);
            popularityFactorsBar.getChildren().set(2, religion);
            popularityFactorsBar.getChildren().set(3, tax);
            popularityFactorsBar.getChildren().set(4, whole);
        }
    }

    private Label setFactorLabel(int difference) {
        Label result = new Label("" + difference);
        result.setStyle("-fx-text-fill: #ad7600; -fx-font-weight: bold; -fx-font-size: 20;");
        if (Integer.parseInt(result.getText()) > 0)
            result.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/green.jpg").toExternalForm(), 20, 20, false, false)));
        else if (Integer.parseInt(result.getText()) < 0)
            result.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/red.jpg").toExternalForm(), 20, 20, false, false)));
        else
            result.setGraphic(new ImageView(new Image(GameMenuControl.class.getResource("/Images/yellow.jpg").toExternalForm(), 20, 20, false, false)));
        currentPlayer.getGovernance().changePopularity(-1 * difference);
        return result;
    }


    private void enterDropUnitBar(int xIndex, int yIndex) {
        if (selectedBuilding == null) return;
        if (changeFactorsBar != null)
            for (Node child : changeFactorsBar.getChildren()) {
                child.setVisible(false);
            }
        if (popularityFactorsBar != null)
            for (Node child : popularityFactorsBar.getChildren()) {
                child.setVisible(false);
            }
        if (mainBar != null)
            for (Node child : mainBar.getChildren()) {
                child.setVisible(false);
            }

        boolean first = unitBar.getChildren().size() == 0;
        barScene.setImage(new Image(GameMenuControl.class.getResource("/Images/unitMenu.png").toExternalForm()));

        HBox unitList = new HBox();
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/green.jpg").toExternalForm(), 20, 20, false, false));
        Button[] troops = new Button[7];
        if (selectedBuilding.get(currentPlayer).getType() == BuildingEnum.MERCENARY_POST) {
            String preAddress = "/Images/units/arabian/";
            troops[0] = createTroopButton(preAddress + "slave.png", 550, UnitEnum.SLAVE, xIndex, yIndex);
            troops[1] = createTroopButton(preAddress + "slinger.png", 670, UnitEnum.SLINGER, xIndex, yIndex);
            troops[2] = createTroopButton(preAddress + "arabian swordsman.png", 790, UnitEnum.ARABIAN_SWORDSMAN, xIndex, yIndex);
            troops[3] = createTroopButton(preAddress + "archer bow.png", 910, UnitEnum.ARCHER_BOW, xIndex, yIndex);
            troops[4] = createTroopButton(preAddress + "assassin.png", 550, UnitEnum.ASSASSIN, xIndex, yIndex);
            troops[5] = createTroopButton(preAddress + "fire thrower.png", 550, UnitEnum.FIRE_THROWER, xIndex, yIndex);
            troops[6] = createTroopButton(preAddress + "horse archer.png", 550, UnitEnum.HORSE_ARCHER, xIndex, yIndex);
        } else {
            String preAddress = "/Images/units/european/";
            troops[0] = createTroopButton(preAddress + "archer.png", 550, UnitEnum.ARCHER, xIndex, yIndex);
            troops[1] = createTroopButton(preAddress + "crossbow_man.png", 670, UnitEnum.CROSSBOW_MAN, xIndex, yIndex);
            troops[2] = createTroopButton(preAddress + "knight.png", 790, UnitEnum.KNIGHT, xIndex, yIndex);
            troops[3] = createTroopButton(preAddress + "mace man.png", 910, UnitEnum.MACE_MAN, xIndex, yIndex);
            troops[4] = createTroopButton(preAddress + "pike man.png", 550, UnitEnum.PIKE_MAN, xIndex, yIndex);
            troops[5] = createTroopButton(preAddress + "spear man.png", 550, UnitEnum.SPEAR_MAN, xIndex, yIndex);
            troops[6] = createTroopButton(preAddress + "swords man.png", 550, UnitEnum.SWORDS_MAN, xIndex, yIndex);
        }

        unitList.setSpacing(20);

        ImageView imageView1 = new ImageView(new Image(GameControlTest.class.getResource("/Images/leftHand.png").toExternalForm()));
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView1.setOnMouseClicked(mouseEvent -> enterMainBar());
        imageView1.setOnMouseEntered(mouseEvent -> imageView1.setImage(new Image(GameControlTest.class.getResource("/Images/leftHandHover.png").toExternalForm())));
        imageView1.setOnMouseExited(mouseEvent -> imageView1.setImage(new Image(GameControlTest.class.getResource("/Images/leftHand.png").toExternalForm())));

        unitBar.setLayoutX(565);
        unitBar.setLayoutY(666);
        unitList.getChildren().addAll(troops[0], troops[1], troops[2], troops[3], troops[4], troops[5], troops[6]);

        if (first) {
            unitBar.getChildren().addAll(unitList, imageView1);
            root.getChildren().add(unitBar);
        } else {
            unitBar.getChildren().set(0, unitList);
            unitBar.getChildren().set(1, imageView1);
        }
    }

    private Button createTroopButton(String address, int i, UnitEnum unitEnum, int xIndex, int yIndex) {
        ImageView imageView = new ImageView(new Image(GameControlTest.class.getResource(address).toExternalForm()));
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        Button btn = new Button("");
        btn.setGraphic(imageView);
        btn.setStyle("-fx-background-color: transparent");
        btn.setOnMouseClicked(event -> setNumber(xIndex, yIndex, unitEnum));
        btn.setLayoutX(i);
        btn.setLayoutY(625);
        return btn;
    }

    private void enterMainBar() {
        if (changeFactorsBar != null)
            for (Node child : changeFactorsBar.getChildren()) {
                child.setVisible(false);
            }
        if (popularityFactorsBar != null)
            for (Node child : popularityFactorsBar.getChildren()) {
                child.setVisible(false);
            }
        if (unitBar != null)
            for (Node child : unitBar.getChildren()) {
                child.setVisible(false);
            }

        barScene.setImage(new Image(GameMenuControl.class.getResource("/Images/governance.png").toExternalForm()));

        boolean first = mainBar.getChildren().size() == 0;

        if (currentSet.equals(BuildingType.TOWN)) {
            houses[0] = createSourceButton("/Images/buildings/hovel.png", 550, 625, BuildingEnum.HOVEL, 100);
            houses[1] = createSourceButton("/Images/buildings/church.png", 670, 625, BuildingEnum.CHURCH, 100);
            houses[2] = createSourceButton("/Images/buildings/cathedral.png", 790, 625, BuildingEnum.CATHEDRAL, 100);
            houses[3] = createSourceButton("/Images/buildings/market.png", 910, 625, BuildingEnum.MARKET, 100);
        } else if (currentSet.equals(BuildingType.WEAPONS)) {
            houses[0] = createSourceButton("/Images/buildings/mercenary_post.png", 550, 625, BuildingEnum.MERCENARY_POST, 100);
            houses[1] = createSourceButton("/Images/buildings/barrack.png", 670, 625, BuildingEnum.BARRACKS, 100);
            houses[2] = createSourceButton("/Images/buildings/blacksmith.png", 790, 625, BuildingEnum.BLACKSMITH, 100);
            houses[3] = createSourceButton("/Images/buildings/fletcher.png", 910, 625, BuildingEnum.FLETCHER, 100);
        }


        Button armoury = new Button();
        ImageView armIcon = new ImageView(new Image(GameControlTest.class.getResource("/Images/i2.png").toExternalForm(), 35, 35, false, false));
        armoury.setStyle("-fx-background-color: transparent; -fx-padding: 0 30 -0 -30;");
        armoury.setGraphic(armIcon);
        armoury.setOnMouseClicked(mouseEvent -> {
            currentSet = BuildingType.WEAPONS;
            enterMainBar();
        });

        Button home = new Button();
        ImageView homeIcon = new ImageView(new Image(GameControlTest.class.getResource("/Images/i4.png").toExternalForm(), 40, 40, false, false));
        home.setStyle("-fx-background-color: transparent; -fx-padding: 0 20 -0 -20;");
        home.setGraphic(homeIcon);
        home.setOnMouseClicked(mouseEvent -> {
            currentSet = BuildingType.TOWN;
            enterMainBar();
        });


        mainBar.setLayoutX(600);
        mainBar.setLayoutY(640);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(armoury, home);
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(houses[0], houses[1], houses[2], houses[3]);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox1, hBox);

        if (first) {
            mainBar.getChildren().add(vBox);
            root.getChildren().add(mainBar);
        } else {
            mainBar.getChildren().set(0, vBox);
            mainBar.toFront();
        }
    }

    private Button createSourceButton(String address, int i, int j, BuildingEnum buildingEnum, int size) {
        Button btn = new Button("");
        btn.setGraphic(new ImageView(new Image(GameControlTest.class.getResource(address).toExternalForm(), size,
                size, false, false)));
        btn.setStyle("-fx-background-color: transparent");
        btn.setOnMouseClicked(event -> {
            createPicture(event, i, j, address, buildingEnum);
            ((Pane) primaryStage.getScene().getRoot()).getChildren().add(buildingImageView);
        });

        btn.setLayoutX(i);
        btn.setLayoutY(j);
        return btn;
    }

    private TextField troopNumberField;
    private int indexOfHoveringUnit = -1;

    private void setNumber(int xIndex, int yIndex, UnitEnum unitEnum) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setStyle("-fx-background-color: rgba(56,56,56,0.56)");

        Label troopNumberLabel = new Label("Troop Number:");
        troopNumberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 23");
        GridPane.setConstraints(troopNumberLabel, 0, 0);
        grid.getChildren().add(troopNumberLabel);

        troopNumberField = new TextField("1");
        GridPane.setConstraints(troopNumberField, 1, 0);
        grid.getChildren().add(troopNumberField);

        Button increaseButton = new Button("+");
        increaseButton.setOnAction(e -> {
            int currentValue = Integer.parseInt(troopNumberField.getText());
            troopNumberField.setText(String.valueOf(currentValue + 1));
        });
        GridPane.setConstraints(increaseButton, 2, 0);
        grid.getChildren().add(increaseButton);

        Button decreaseButton = new Button("-");
        decreaseButton.setOnAction(e -> {
            int currentValue = Integer.parseInt(troopNumberField.getText());
            if (currentValue > 1) {
                troopNumberField.setText(String.valueOf(currentValue - 1));
            }
        });
        GridPane.setConstraints(decreaseButton, 3, 0);
        grid.getChildren().add(decreaseButton);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button cancelButton = new Button("Cancel");
        Button okButton = new Button("OK");
        cancelButton.setOnAction(e -> root.getChildren().remove(grid));
        okButton.setOnAction(e -> {
            int[] properIndexes = adjustIndexes(xIndex, yIndex);
            Tile tile = linkedHouses.get(panes[properIndexes[0]][properIndexes[1]]);
            int troopNumber = Integer.parseInt(troopNumberField.getText());
            root.getChildren().remove(grid);
            if (tile.getPlayersUnits() != null && tile.getPlayersUnits().size() != 0) {
                showError("There is no empty space to add a new unit!", "Failed to create unit");
                return;
            }
            gameController.setSelectedBuilding(selectedBuilding.get(currentPlayer));
            gameController.setxOFSelectedBuilding(tile.getX());
            gameController.setyOFSelectedBuilding(tile.getY());
            GameControllerOut result = gameController.createUnit("createunit -x " + tile.getX() + " -y " +
                    tile.getY() + " -t " + unitEnum.getName() + " -c " +
                    troopNumber, true);

            System.out.println("real coordinate: " + tile.getX() + " , " + tile.getY());
            System.out.println("units addeded tp tile: " + tile.findYourUnits(currentPlayer).size());
            if (result != GameControllerOut.SUCCESSFULLY_CREATED_UNIT)
                showError(result.getContent(), "Failed to create unit");
            else {
                ImageView unitMember;
                Image image;
                if (troopNumber < 4) {
                    if (unitEnum.isArab())
                        image = new Image(GameControlTest.class.getResource("/Images/units/arabian/" +
                                unitEnum.getName() + ".png").toExternalForm());
                    else
                        image = new Image(GameControlTest.class.getResource("/Images/units/european/" +
                                unitEnum.getName() + ".png").toExternalForm());
                } else {
                    image = new Image(GameControlTest.class.getResource("/Images/unitCrowd/" +
                            unitEnum.getName() + ".png").toExternalForm());
                }

                GridPane gridPane = new GridPane();
                gridPane.maxHeight(TILE_SIZE - 30);
                gridPane.maxWidth(TILE_SIZE - 30);
                if (troopNumber < 4)
                    for (int j = 0; j < troopNumber; j++) {
                        unitMember = new ImageView(image);
                        unitMember.setSmooth(true);
                        unitMember.setCache(true);
                        gridPane.add(unitMember, j, 0);
                        units.put(gridPane, currentPlayer.getGovernance().getUnits().get
                                (currentPlayer.getGovernance().getUnits().size() - 1));
                    }
                else {
                    unitMember = new ImageView(image);
                    unitMember.setSmooth(true);
                    unitMember.setCache(true);
                    unitMember.setFitWidth(TILE_SIZE - 30);
                    unitMember.setFitHeight(TILE_SIZE - 30);
                    gridPane.add(unitMember, 0, 0);
                    units.put(gridPane, currentPlayer.getGovernance().getUnits().get
                            (currentPlayer.getGovernance().getUnits().size() - 1));
                }

                //saving the current pane index:
                gridPane.setUserData(new int[]{properIndexes[0], properIndexes[1]});

                gridPane.setOnMouseEntered(mouseEvent -> {
                    Unit unit = units.get(gridPane);
                    String infoStr = "Troop number: " + gridPane.getChildren().size() + "\nOwner: " + unit.getMaster().getUsername() +
                            "\nState: " + unit.getState() + "\nSpeed: " + unit.getSpeed() + "\nDamage: " + unit.getUnitDamage() +
                            "\nHp: " + unit.getUnitHp() + "\nTypes present:" + unit.getPresentTypes();
                    Label info = simpleLabelStyler(infoStr);
                    info.setStyle("-fx-alignment: center; -fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-background-color: rgba(40,37,37,0.25);" +
                            "; -fx-font-size: 25; -fx-font-weight: bold");
                    info.setLayoutX(mouseEvent.getScreenX() - TILE_SIZE);
                    info.setLayoutY(mouseEvent.getScreenY() - 20);
                    if (!hoverActive) return;

                    if (indexOfHoveringUnit >= 0)
                        root.getChildren().set(indexOfHoveringUnit, info);
                    else {
                        root.getChildren().add(info);
                        indexOfHoveringUnit = root.getChildren().indexOf(info);
                    }
                });

                gridPane.setOnMouseExited(mouseEvent -> {
                    if (indexOfHoveringUnit > -1)
                        root.getChildren().remove(indexOfHoveringUnit);
                    indexOfHoveringUnit = -1;
                });

                gridPane.setOnMouseClicked(mouseEvent -> {
                    if (!selectActive) return;
                    hoverActive = false;
                    openWindow(gridPane, tile, properIndexes);
                    closeHovers();
                });

                panes[properIndexes[0]][properIndexes[1]].getChildren().add(gridPane);
            }
        });
        buttonBox.getChildren().addAll(cancelButton, okButton);
        GridPane.setConstraints(buttonBox, 0, 1, 4, 1);
        grid.getChildren().add(buttonBox);
        grid.setLayoutX(520);
        grid.setLayoutY(300);
        root.getChildren().add(grid);
    }

    private void openWindow(GridPane gridPane, Tile tile, int[] properIndexes) {
        Stage window = new Stage();
        window.setTitle("Unit Options");

        VBox layout = new VBox(9);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(rgba(101, 0, 0, 0.74) 10%, rgba(155, 2, 2, 0.74) 20%," +
                " rgba(155, 57, 2, 0.74) 35%, rgba(138, 89, 0, 0.74) 45%); -fx-font-size: 20; -fx-font-family: Garamond");

        RadioButton defensiveBtn = new RadioButton("defensive");
        RadioButton aggressiveBtn = new RadioButton("aggressive");
        RadioButton standingBtn = new RadioButton("standing");
        ToggleGroup unitStateGroup = new ToggleGroup();
        defensiveBtn.setToggleGroup(unitStateGroup);
        aggressiveBtn.setToggleGroup(unitStateGroup);
        standingBtn.setToggleGroup(unitStateGroup);

        RadioButton moveBtn = new RadioButton("Move");
        RadioButton attackBtn = new RadioButton("Attack");
        ToggleGroup moveAttackGroup = new ToggleGroup();
        moveBtn.setToggleGroup(moveAttackGroup);
        attackBtn.setToggleGroup(moveAttackGroup);

        HBox buttons = new HBox();
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");
        buttons.getChildren().addAll(okBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        Label state = new Label("Choose unit state:");
        state.setStyle("-fx-font-size: 22; -fx-font-family: Garamond; -fx-font-weight: bold");
        Label move = new Label("Move unit:");
        move.setStyle("-fx-font-size: 22; -fx-font-family: Garamond; -fx-font-weight: bold");
        layout.getChildren().addAll(
                state,
                defensiveBtn, aggressiveBtn, standingBtn,
                move,
                moveBtn, attackBtn,
                buttons
        );

        Scene scene = new Scene(layout, 400, 320);
        window.setScene(scene);
        window.show();

        okBtn.setOnAction(event1 -> {
            if (unitStateGroup.getSelectedToggle() != null) {
                RadioButton selectedState = (RadioButton) unitStateGroup.getSelectedToggle();
                units.get(gridPane).setState(selectedState.getText());
                System.out.println("Selected unit state: " + selectedState.getText());
            }
            if (moveAttackGroup.getSelectedToggle() != null) {
                Unit unit = units.get(gridPane);
                unit.setOnMove(true);
                RadioButton selectedAction = (RadioButton) moveAttackGroup.getSelectedToggle();
                if (selectedAction.getText().equals("Attack"))
                    showBannerAndWait(unit);

                root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getScreenX() > 1470) return;
                    if (event.getScreenY() > 600) return;
                    if (!unit.isOnMove()) return;
                    int targetX = (int) Math.floor(event.getScreenX() / TILE_SIZE);
                    int targetY = (int) Math.floor(event.getScreenY() / TILE_SIZE);
                    if (((int[]) gridPane.getUserData())[0] == targetX && ((int[]) gridPane.getUserData())[1] == targetY)
                        return;
                    System.out.println("moving click detected");
                    Tile targetTile = linkedHouses.get(panes[targetX][targetY]);
                    int[] targetIndexes = new int[]{targetX, targetY};
                    //move the unit.
                    Tile primaryHolder = unit.getOriginTile() == null ? tile : unit.getOriginTile();
                    Tile secondaryHolder = unit.getTargetTile() == null ? targetTile : unit.getTargetTile();
                    List<Point> pathPoints = PatchFinding.findPath(map, new Point(primaryHolder.getX(), primaryHolder.getY()),
                            new Point(secondaryHolder.getX(), secondaryHolder.getY()), true);
                    moveUnitsOnScreen(pathPoints, unit, gridPane, properIndexes, targetIndexes, tile, targetTile);
                    unit.setOnMove(false);
                });
            }

            window.close();
        });

        cancelBtn.setOnAction(event -> {
            window.close();
            hoverActive = true;
        });
    }


    private static final Duration TWINKLE_DURATION = Duration.seconds(2);
    private static final double MIN_OPACITY = 0.0;
    private static final double MAX_OPACITY = 1.0;

    private void showBannerAndWait(Unit unit) {
        unit.setAttack(true);

        Label attackText = new Label("Attack");
        attackText.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        attackText.setTextFill(Color.RED);
        root.getChildren().add(attackText);
        attackText.setLayoutX(700);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(attackText.opacityProperty(), MAX_OPACITY)),
                new KeyFrame(TWINKLE_DURATION.divide(2), new KeyValue(attackText.opacityProperty(), MIN_OPACITY)),
                new KeyFrame(TWINKLE_DURATION, new KeyValue(attackText.opacityProperty(), MAX_OPACITY))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        AnimationManager.timers.add(timeline);
        timeline.play();

        attackText.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                timeline.stop();
            }
        });

        unit.attackProperty().addListener((observable, oldValue, newValue) -> attackText.setVisible(newValue));
    }


    private void closeHovers() {
        boolean disarrangement = false;
        if (indexOfHoveringUnit > -1) {
            disarrangement = true;
            root.getChildren().remove(indexOfHoveringUnit);
        }
        indexOfHoveringUnit = -1;
        if (indexOfHoveringTile > -1) {
            if (disarrangement) indexOfHoveringTile--;
            root.getChildren().remove(indexOfHoveringTile);
        }
        indexOfHoveringTile = -1;
        if (indexOfHoveringBuilding > -1)
            root.getChildren().remove(indexOfHoveringBuilding);
        indexOfHoveringBuilding = -1;
    }

    private void adjustUnitCurrentPosition(Tile primaryTile, Tile targetTile, Unit changingUnit) {
        map.getTile(primaryTile.getY(), primaryTile.getX()).removeAUnit(changingUnit);
        changingUnit.setxOrigin(targetTile.getX());
        changingUnit.setyOrigin(targetTile.getY());
        changingUnit.setOriginTile(targetTile);
        changingUnit.setTargetTile(null);
        map.getTile(targetTile.getY(), targetTile.getX()).addUnitToTile(changingUnit);
        changingUnit.setCurrentTile(map.getTile(targetTile.getY(), targetTile.getX()));
        if (!targetTile.areEnemiesHere(currentPlayer)) changingUnit.setAttack(false);
    }

    private void moveUnitsOnScreen(List<Point> pathPoints, Unit unit, GridPane gridPane, int[] properIndexes, int[] targetIndexes, Tile tile, Tile targetTile) {
        List<Point> correctedPathPoints = screenFormatter(pathPoints);
        ArrayList<Pane> paneSequence = createPaneSequence(correctedPathPoints);
        if (paneSequence.size() == 0) return;
        if (panes[properIndexes[0]][properIndexes[1]] == paneSequence.get(0) && paneSequence.size() == 1) return;

        panes[properIndexes[0]][properIndexes[1]].getChildren().remove(gridPane);
        if (unit.getOriginTile() == null)
            unit.setOriginTile(tile);
        if (unit.getTargetTile() == null)
            unit.setTargetTile(targetTile);

        System.out.println("this is the target tile: " + targetTile.getY() + " , " + targetTile.getX());
        System.out.println("original path");
        for (Point pathTilePoint : pathPoints) {
            System.out.println(unit.getTargetTile().getX() + " , " + unit.getTargetTile().getY());
        }
        MoveAnimation moveAnimation = new MoveAnimation(map, unit, correctedPathPoints, root, panes, gridPane, (int[]) gridPane.getUserData(), paneSequence);

        moveAnimation.statusProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == Animation.Status.STOPPED) {
                hoverActive = true;
                adjustUnitCurrentPosition(unit.getOriginTile(), unit.getTargetTile(), unit);
                gridPane.setUserData(new int[]{targetIndexes[0], targetIndexes[1]});
                panes[targetIndexes[0]][targetIndexes[1]].getChildren().add(gridPane);
                System.out.println("indexes of target pane: " + targetIndexes[0] + " , " + targetIndexes[1]);
//                if (--indexOfHoveringTile >= 0) {
//                    System.out.println("is our index really correct? " + (root.getChildren().get(indexOfHoveringTile) instanceof Label));
//                    root.getChildren().remove(indexOfHoveringTile);
//                    indexOfHoveringTile = -1;
//                }
//                if (--indexOfHoveringBuilding >= 0) {
//                    System.out.println("is our index really correct? " + (root.getChildren().get(indexOfHoveringBuilding) instanceof Label));
//                    root.getChildren().remove(indexOfHoveringBuilding);
//                    indexOfHoveringBuilding = -1;
//                }
            }
        });

        AnimationManager.animations.add(moveAnimation);
        moveAnimation.play();

    }


    private ArrayList<Pane> createPaneSequence(List<Point> correctedPathPoints) {
        ArrayList<Pane> paneArrayList = new ArrayList<>();
        for (Point point : correctedPathPoints) {
            paneArrayList.add(getPaneByTile(map.getTile(point.getX(), point.getY())));
        }
        return paneArrayList;
    }

    private Pane getPaneByTile(Tile tile) {
        for (java.util.Map.Entry<Pane, Tile> paneTileEntry : linkedHouses.entrySet()) {
            if (paneTileEntry.getValue().getX() == tile.getX() && paneTileEntry.getValue().getY() == tile.getY())
                return paneTileEntry.getKey();
        }
        return null;
    }

    private List<Point> screenFormatter(List<Point> pathPoints) {
        Tile tile;
        List<Point> path = new ArrayList<>();
        for (int i = 0; i < pathPoints.size(); i++) {
            if (getPaneByTile(tile = map.getTile(pathPoints.get(i).getX(), pathPoints.get(i).getY())) != null)
                path.add(new Point(tile.getY(), tile.getX()));
        }
        return path;
    }


    private int[] adjustIndexes(int xIndex, int yIndex) {
        int[] indexes = new int[2];
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                int Xindex = Math.max(i + xIndex, 0);
                int Yindex = Math.max(j + yIndex, 0);
                Xindex = Math.min(Xindex, 9);
                Yindex = Math.min(Yindex, 3);
                if (linkedHouses.get(panes[Xindex][Yindex]).getTexture().isWalkability()) {
                    if (i == 0 && j == 0) continue;
                    if (linkedHouses.get(panes[Xindex][Yindex]).getPlayersUnits() != null &&
                            linkedHouses.get(panes[Xindex][Yindex]).getPlayersUnits().size() != 0)
                        continue;
                    indexes[0] = Xindex;
                    indexes[1] = Yindex;
                    return indexes;
                } else {
                    yIndex = -1;
                    xIndex = -1;
                }
            }
        indexes[0] = xIndex;
        indexes[1] = yIndex;
        return indexes;
    }

    private void showError(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.show();
    }

    int indexOfHoveringTile = -1;

    private Tile findTileByPane(Pane section) {
        Tile tile = null;
        for (java.util.Map.Entry<Pane, Tile> entry : linkedHouses.entrySet()) {
            if (section == null) continue;
            if (entry.getKey().getLayoutX() == section.getLayoutX() &&
                    entry.getKey().getLayoutY() == section.getLayoutY()) {
                tile = entry.getValue();
                break;
            }
        }
        return tile;
    }

    private void addTile(Pane root, int index) {
        for (java.util.Map.Entry<ImageView, Building> entry : buildings.entrySet()) {
            for (Node child : root.getChildren()) {
                if (child.equals(entry.getKey())) {
                    root.getChildren().remove(child);
                    break;
                }
            }
        }

        for (java.util.Map.Entry<GridPane, Unit> gridPaneUnitEntry : units.entrySet()) {
            for (Node child : root.getChildren()) {
                if (child.equals(gridPaneUnitEntry.getKey())) {
                    root.getChildren().remove(child);
                    break;
                }
            }
        }

        buildings = new HashMap<>();
        Pane section = new Pane();
        section.setStyle("-fx-border-color: black;");
        section.setPrefHeight(TILE_SIZE);
        section.setPrefWidth(TILE_SIZE);
        double[] ranges = new double[2];
        ranges[0] = ((index % 10) * TILE_SIZE);
        ranges[1] = (index / 10) * TILE_SIZE;
        panes[index % 10][index / 10] = section;
        int[] coordinates = assignTileToScreen(index);
        linkedHouses.put(section, map.getTile(coordinates[1], coordinates[0]));

        addTileTexture(section, coordinates);
        section.getChildren().get(0).setOnMouseEntered(mouseEvent -> {
            if (tilesOccupied) return;
            Tile tile = findTileByPane(section);
            String infoStr = "Texture: " + tile.getTexture().getName() + "\nbuilding num: " + tile.getBuildings().size();
            Label info = simpleLabelStyler(infoStr);
            info.setStyle("-fx-alignment: center; -fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-background-color: rgba(40,37,37,0.25);" +
                    "; -fx-font-size: 25; -fx-font-weight: bold");
            if (!hoverActive) return;
            if (indexOfHoveringTile >= 0)
                root.getChildren().set(indexOfHoveringTile, info);
            else {
                root.getChildren().add(info);
                indexOfHoveringTile = root.getChildren().indexOf(info);
            }
        });


        section.getChildren().get(0).setOnMouseExited(mouseEvent -> {
            if (tilesOccupied) return;
            if (indexOfHoveringTile > 0) root.getChildren().remove(indexOfHoveringTile);
            indexOfHoveringTile = -1;
        });

        int fixedSize = root.getChildren().size();
        if (fixedSize <= index)
            root.getChildren().add(section);
        else root.getChildren().set(index, section);
        root.getChildren().get(index).setLayoutX(ranges[0]);
        root.getChildren().get(index).setLayoutY(ranges[1]);
    }

    private void addTileTexture(Pane section, int[] coordinates) {
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/textures/" + map.getTile(
                coordinates[1], coordinates[0]).getTexture().getName() + ".jpg").toExternalForm(), TILE_SIZE, TILE_SIZE, false
                , false));
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend blush = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        imageView.getImage().getWidth(),
                        imageView.getImage().getHeight(),
                        Color.RED
                )
        );
        blush.setOpacity(0.25);

        imageView.effectProperty().bind(
                Bindings
                        .when(imageView.hoverProperty())
                        .then((Effect) blush)
                        .otherwise((Effect) null)
        );

        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
        section.getChildren().add(imageView);

        ImageView sickness = new ImageView(new Image(GameMenuControl.class.getResource("/Images/sickness.png").toExternalForm(), TILE_SIZE, TILE_SIZE, false
                , false));
        int random = (int) (100 * Math.random());
        if (random == 5) {
//            ImageView fire = new ImageView(new Image(GameControlTest.class.getResource("/Images/fire.png").toExternalForm()));
//            fire.setFitWidth(TILE_SIZE - 10);
//            fire.setPreserveRatio(true);
//            fire.setSmooth(true);
//            fire.setCache(true);
//            section.getChildren().add(fire);
//            FireAnimation fireAnimation = new FireAnimation(fire);
//            AnimationManager.animations.add(fireAnimation);
//            fireAnimation.play();
            section.getChildren().add(sickness);
            sickland = true;
        }
    }


    private void setUpTile(Pane section, Tile tile) {
        if (tile.getBuildings() != null)
            for (Building building : tile.getBuildings()) {
                //System.out.println("this is going to be a building " + building.getType().getName());
                buildingImageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/buildings/" +
                        building.getType().getName() + ".png").toExternalForm()));
                buildingImageView.setFitWidth(TILE_SIZE - 10);
                buildingImageView.setPreserveRatio(true);
                buildingImageView.setSmooth(true);
                buildingImageView.setCache(true);
                VBox vBox = new VBox();
                vBox.getChildren().add(buildingImageView);
                buildingAndFiresAddingToRoot.put(building,vBox);
                section.getChildren().add(vBox);
                if (building.isOnFire()) {
//                    resetBuffers();
//                    ImageView fire = new ImageView(new Image(GameControlTest.class.getResource("/Images/fire.png").toExternalForm()));
//                    fire.setFitWidth(TILE_SIZE - 10);
//                    fire.setPreserveRatio(true);
//                    fire.setSmooth(true);
//                    fire.setCache(true);
//                    VBox vBox1 = buildingAndFiresAddingToRoot.get(building);
//                    vBox1.getChildren().add(fire);
//                    root.getChildren().set(root.getChildren().indexOf(buildingAndFiresAddingToRoot.get(building)),fire);
//                    FireAnimation fireAnimation = new FireAnimation(fire);
//                    AnimationManager.animations.add(fireAnimation);
//                    fireAnimation.play();
                }
            }

        for (java.util.Map.Entry<String, ArrayList<Unit>> stringArrayListEntry : tile.getPlayersUnits().entrySet()) {
            for (Unit unit : stringArrayListEntry.getValue()) {
                GridPane gridPane = findGridPaneByUnit(unit);
                if (gridPane != null)
                    section.getChildren().add(gridPane);
            }
        }
    }

    private GridPane findGridPaneByUnit(Unit unit) {
        for (java.util.Map.Entry<GridPane, Unit> gridPaneUnitEntry : units.entrySet()) {
            if (gridPaneUnitEntry.getValue() == unit)
                return gridPaneUnitEntry.getKey();
        }
        return null;
    }

    private int[] assignTileToScreen(int index) {
        int[] ans = new int[2];
        ans[0] = xCenter + (index % 10 - 3);
        ans[1] = yCenter + (index / 10 - 2);
        return ans;
    }

    private void addToTile(int[] index, int say) {
        double[] ranges = new double[2];
        ranges[0] = -550 + 150 * index[0] - say * 120;
        ranges[1] = -605 + 140 * index[1];
        System.out.println("final rang: " + index[0] + " , " + index[1]);
        buildingImageView.setLayoutY(ranges[1] + 12.5);
        buildingImageView.setLayoutX(ranges[0] - TILE_SIZE / 2 + 12.5);
    }

    private void createPicture(MouseEvent event, int i, int j, String address, BuildingEnum buildingEnum) {

        buildingImageView = new ImageView(new Image(GameMenuControl.class.getResource(address).toExternalForm()));
        buildingImageView.setFitWidth(TILE_SIZE - 25);
        buildingImageView.setPreserveRatio(true);
        buildingImageView.setSmooth(true);
        buildingImageView.setCache(true);

        buildingImageView.setOnMouseDragged(this::onMouseDragged);
        buildingImageView.setOnMouseReleased(mouseEvent -> onMouseReleased(mouseEvent, buildingEnum));
        buildingImageView.setUserData(new double[]{event.getY(), event.getY(), ((Button) event.getSource()).getLayoutX()
                , ((Button) event.getSource()).getLayoutY()});
        buildingImageView.setX(i + TILE_SIZE / 2);
        buildingImageView.setY(j);
    }


    private void onMouseDragged(MouseEvent event) {
        if (buildingImageView == null)
            return;
        // Update the position of the building while dragging
        double[] initialPosition = (double[]) buildingImageView.getUserData();
        buildingImageView.relocate(event.getScreenX() - initialPosition[0] - TILE_SIZE / 2 + 50, event.getScreenY() - initialPosition[1] - TILE_SIZE / 2 + 50);
    }

    private Label simpleLabelStyler(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-padding: 25 0 0 70; -fx-font-size: 25");
        label.setText(text);
        return label;
    }

    private int indexOfHoveringBuilding = -1;

    private void onMouseReleased(MouseEvent event, BuildingEnum buildingEnum) {
        if (buildingImageView == null) return;

        //saving for undo:
        int[] index = findTheNearestTile(say(event));
        Tile tile = linkedHouses.get(panes[index[0]][index[1]]);
        lastTileUndo = tile.copy();
        lastLinkedHousesUndo = linkedHouses;
        lastBuildingImageViewUndo = buildingImageView;
        lastBuildingsUndo = buildings;
        doWeHaveUndo = true;
        buildingImageView.setOnMouseDragged(null);
        buildingImageView.setOnMouseReleased(null);
        if (buildingEnum != null) {
            Building building = new Building(buildingEnum, currentPlayer, 0, true);
            buildings.put(buildingImageView, building);
            currentPlayer.getGovernance().getBuildings().add(building);
        }

        linkedHouses.get(panes[index[0]][index[1]]).getBuildings().add(buildings.get(buildingImageView));

        //todo: do the drop building thing here
        System.out.println("index: " + index[0] + " , " + index[1]);
        addToTile(index, say(event));

        AtomicReference<Building> building = new AtomicReference<>(buildings.get(buildingImageView));
        buildingImageView.setOnMouseEntered(mouseEvent -> {
            for (java.util.Map.Entry<ImageView, Building> entry : buildings.entrySet()) {
                if (buildingImageView == null) continue;
                if (entry.getKey().getLayoutX() == buildingImageView.getLayoutX() &&
                        entry.getKey().getLayoutY() == buildingImageView.getLayoutY()) {
                    building.set(entry.getValue());
                    break;
                }
            }
            String infoStr = "Name: " + building.get().getType().getName() + "\nHp: " + building.get().getHp() + "\nOwner: " +
                    building.get().getOwner().getUsername() + "\nActive: " + building.get().isActive();
            Label info = simpleLabelStyler(infoStr);
            info.setStyle("-fx-alignment: center; -fx-font-family: Garamond; -fx-text-fill: #EEE2BBFF; -fx-background-color: rgba(40,37,37,0.25);" +
                    "; -fx-font-size: 25; -fx-font-weight: bold");

            if (!hoverActive) return;
            if (indexOfHoveringBuilding >= 0)
                root.getChildren().set(indexOfHoveringBuilding, info);
            else {
                root.getChildren().add(info);
                indexOfHoveringBuilding = root.getChildren().indexOf(info);
            }
        });
        buildingImageView.setOnMouseExited(mouseEvent -> {
            if (indexOfHoveringBuilding > 0) root.getChildren().remove(indexOfHoveringBuilding);
            indexOfHoveringBuilding = -1;
        });

        buildingImageView = null;
    }

    private int say(MouseEvent event) {
        if (event == null) return -2;
        for (int i = 0; i < 4; i++) {
            if (houses[i].getLayoutX() == ((double[]) ((ImageView) event.getSource()).getUserData())[2])
                return i;
        }
        return -1;
    }

    private int[] findTheNearestTile(int number) {
        int[] coor = new int[2];
        double offset = number * 120;
        double currentX = buildingImageView.getLayoutX() + offset;
        double currentY = buildingImageView.getLayoutY();
        //System.out.println(buildingImageView.getX() + " , " + buildingImageView.getLayoutX() + " , " + buildingImageView.getTranslateX());
        if (currentX <= -480)
            coor[0] = 0;
        else {
            if (currentX >= 735)
                coor[0] = 9;
            else
                coor[0] = (int) (((currentX + 480) / TILE_SIZE) % 10 + 1);
        }

        currentY = currentY * -1;
        if (currentY <= 245)
            coor[1] = 3;
        else {
            if (currentY >= 540)
                coor[1] = 0;
            else
                coor[1] = (int) (((540 - currentY) / TILE_SIZE) + 1);
        }
        System.out.println("this is my result: " + coor[0] + " , " + coor[1]);
        return coor;
    }
}

