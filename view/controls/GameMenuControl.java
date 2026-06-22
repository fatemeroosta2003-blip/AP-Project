package view.controls;

import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Point;

public class GameMenuControl {
    private ImageView buildingImageView;

    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        // Create 4 sections in the grid
        Pane section0 = createSection();
        Pane section1 = createSection();
        Pane section2 = createSection();
        Pane section3 = createSection();
        Pane section4 = createSection();

        Pane section5 = createSection();
        Pane section6 = createSection();
        Pane section7 = createSection();
        Pane section8 = createSection();
        Pane section9 = createSection();

        // Set column and row indexes for each section
        GridPane.setColumnIndex(section0, 0);
        GridPane.setRowIndex(section0, 0);
        GridPane.setColumnIndex(section1, 1);
        GridPane.setRowIndex(section1, 0);
        GridPane.setColumnIndex(section2, 2);
        GridPane.setRowIndex(section2, 0);
        GridPane.setColumnIndex(section3, 3);
        GridPane.setRowIndex(section3, 0);


        GridPane.setColumnIndex(section4, 4);
        GridPane.setRowIndex(section4, 0);
        GridPane.setColumnIndex(section5, 5);
        GridPane.setRowIndex(section5, 0);
        GridPane.setColumnIndex(section6, 6);
        GridPane.setRowIndex(section6, 0);

        GridPane.setColumnIndex(section7, 7);
        GridPane.setRowIndex(section7, 0);
        GridPane.setColumnIndex(section8, 8);
        GridPane.setRowIndex(section8, 0);
        GridPane.setColumnIndex(section9, 9);
        GridPane.setRowIndex(section9, 0);

        // Add sections to the grid
        gridPane.getChildren().addAll(section0,section1, section2, section3, section4,section5,section6,section7,section8);

        System.out.println(gridPane.getChildren().get(3).getLayoutX());

        // Add nodes to each section
        Button button1 = new Button("Button 1");
        section1.getChildren().add(button1);
        ImageView imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/hovel.png").toExternalForm()));
        imageView.setFitWidth(185);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        section1.getChildren().add(imageView);


        imageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/hovel.png").toExternalForm()));
        imageView.setFitWidth(185);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        Button button2 = new Button("Button 2");
        section2.getChildren().add(button2);
        section2.getChildren().add(imageView);

        //placeInTheMiddle(2, section2, section2.getChildren().indexOf(imageView));
        //placeInTheMiddle(2, section2, section2.getChildren().indexOf(button2));

        Button button3 = new Button("Button 3");
        section3.getChildren().add(button3);

        Button button4 = new Button("Button 4");
        section4.getChildren().add(button4);



//        AnchorPane root = new AnchorPane();
//        addButton(primaryStage, root);
//


        primaryStage.setScene(new Scene(gridPane, 1530, 800));
        primaryStage.show();
    }

    private double[] sectionRangeCalculator(int number) {
        double[] ranges = new double[4];

        //todo: bounds below aren't working.
//        Bounds section1Bounds = section.localToScene(section.getBoundsInLocal());
//        double section1X = section1Bounds.getMinX();
//        double section1Y = section1Bounds.getMinY();
//        double section1Width = section1Bounds.getWidth();
//        double section1Height = section1Bounds.getHeight();
//        double maxX = section1X + section1Width;
//        double maxY = section1Y + section1Height;
//        ranges = new double[]{section1X, maxX, section1Y, maxY};

        //assuming we have 9 * ... 190px sections.
        ranges[0] = (number % 9) * 190;
        ranges[1] = ranges[0] + 190;
        ranges[2] = (number / 9) * 190;
        ranges[3] = ranges[2] + 190;

        System.out.println("section " + number + " our range: " + ranges[0] + " , " + ranges[1] + " , " + ranges[2] + " , " + ranges[3]);

        return ranges;
    }

    private void placeInTheMiddle(int sectionNumber, Pane section, int index) {
        double[] ranges = sectionRangeCalculator(sectionNumber);
        double x = (ranges[0] + ranges[1]) / 2;
        double y = (ranges[2] + ranges[3]) / 2;
        section.getChildren().get(index).setLayoutX(0);
        section.getChildren().get(index).setLayoutY(0);
//        section.getChildren().get(index).setTranslateX(x);
//        section.getChildren().get(index).setTranslateY(y);
    }


    private void addButton(Stage primaryStage, AnchorPane root) {
        Button btn = new Button("Click Me");
        btn.setOnMouseEntered(event -> {


            // Create a new building image view
            buildingImageView = new ImageView(new Image(GameMenuControl.class.getResource("/Images/hovel.png").toExternalForm()));

            // Add mouse event handlers for dragging and dropping
            buildingImageView.setOnMouseDragged(this::onMouseDragged);
            buildingImageView.setOnMouseReleased(this::onMouseReleased);
            buildingImageView.setUserData(new double[]{event.getX(), event.getY()});

            VBox vBox = new VBox();
            vBox.getChildren().add(buildingImageView);

            // Add the building to the pane
            ((AnchorPane) primaryStage.getScene().getRoot()).getChildren().add(buildingImageView);
            buildingImageView.setX(50);
            buildingImageView.setY(700);
        });
        btn.setLayoutX(50);
        btn.setLayoutY(700);
        root.getChildren().add(btn);
    }

    private void onMousePressed(MouseEvent event) {
        // Save the initial position of the building
        buildingImageView.setUserData(new double[]{event.getX(), event.getY()});
    }

    private void onMouseDragged(MouseEvent event) {
        if(buildingImageView == null) return;
        // Update the position of the building while dragging
        double[] initialPosition = (double[]) buildingImageView.getUserData();
        buildingImageView.relocate(event.getScreenX() - initialPosition[0] + 50, event.getScreenY() - initialPosition[1]);
    }

    private void onMouseReleased(MouseEvent event) {
        // Clean up the building when dropped
        //buildingImageView.setOnMousePressed(null);
        if(buildingImageView == null) return;
        buildingImageView.setOnMouseDragged(null);
        buildingImageView.setOnMouseReleased(null);
        buildingImageView = null;
    }

    private Pane createSection() {
        Pane section = new Pane();
        section.setStyle("-fx-border-color: black;");
        section.setPrefHeight(170);
        section.setPrefWidth(170);
        return section;
    }
}