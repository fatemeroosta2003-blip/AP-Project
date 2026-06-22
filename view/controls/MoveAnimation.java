package view.controls;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Map;
import model.Point;
import model.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class MoveAnimation extends Transition {
    //data on the screen
    Map map;
    Pane root;
    Unit unit;
    Pane[][] panes;
    int unitIndexInRoot;
    int[] finalDestination;

    //this is about to change
    VBox container;
    GridPane unitImages;

    //the road to success
    List<Point> pathTilePoints;
    ArrayList<Pane> pathPaneSequence;

    //current state
    int xSign;
    int ySign;
    int currentTileListIndex = 0;
    boolean firstTile = true;


    public MoveAnimation(Map map, Unit unit, List<Point> pathPoints, Pane root, Pane[][] panes, GridPane gridPane, int[] currentPaneIndex, ArrayList<Pane> paneSequence) {
        this.map = map;
        this.unit = unit;
        this.pathTilePoints = pathPoints;
        this.pathPaneSequence = paneSequence;
        this.root = root;
        this.unitImages = gridPane;
        this.panes = panes;

        ImageView cloud = new ImageView(new Image(GameMenuControl.class.getResource("/Images/cloud.png").toExternalForm()));
        cloud.setFitWidth(150);
        cloud.setFitHeight(60);
        cloud.setSmooth(true);
        cloud.setCache(true);

        container = new VBox();
        container.getChildren().addAll(unitImages, cloud);
        container.setSpacing(-50);

        root.getChildren().add(container);
        container.setLayoutX(panes[currentPaneIndex[0]][currentPaneIndex[1]].getLayoutX());
        container.setLayoutY(panes[currentPaneIndex[0]][currentPaneIndex[1]].getLayoutY());

        System.out.println("first, (from tiles), you should go to " + unit.getTargetTile().getX() + " , " + unit.getTargetTile().getX() + "\n" +
                unit.getOriginTile().getY() + " , " + unit.getOriginTile().getX());
        System.out.println("path:");
        for (Point pathTilePoint : pathTilePoints) {
            System.out.println(pathTilePoint.getX() + " , " + pathTilePoint.getY());
        }

        this.setCycleDuration(Duration.millis(1000));
        this.setCycleCount(-1);
    }


    @Override
    protected void interpolate(double v) {
        int height = 0, width = 0;
        switch ((int) (v * 10 % 5)) {
            case 0:
            case 4:
                height = 60;
                width = 150;
                break;
            case 1:
            case 3:
                height = 80;
                width = 130;
                break;
            case 2:
                height = 100;
                width = 170;
                break;
        }
        ((ImageView) container.getChildren().get(1)).setFitHeight(height);
        ((ImageView) container.getChildren().get(1)).setFitWidth(width);

        if (currentTileListIndex + 1 >= pathTilePoints.size()) {
            getReadyToFinish();
            this.stop();
            return;
        }

        Point currentPoint = pathTilePoints.get(currentTileListIndex);
        Point nextPoint = pathTilePoints.get(currentTileListIndex + 1);

        if (firstTile) {
            nextPoint = currentPoint;
            currentPoint = new Point(unit.getOriginTile().getY(), unit.getOriginTile().getX());
            currentTileListIndex--;
        }

        if (nextPoint.getY() - currentPoint.getY() > 0)
            xSign = 1;
        else xSign = nextPoint.getY() - currentPoint.getY() == 0 ? 0 : -1;

        if (nextPoint.getX() - currentPoint.getX() > 0)
            ySign = 1;
        else ySign = nextPoint.getX() - currentPoint.getX() == 0 ? 0 : -1;

        if (xSign == 0 && ySign == 0) {
            getReadyToFinish();
            this.stop();
            return;
        }

        System.out.println("new x: " + xSign * 5 + " new y: " + ySign * 5);
        //we change coordinates, then check if we have entered a new pane. if yes, we change curr pane and tile and examine finales.
        container.setLayoutX(container.getLayoutX() + xSign * 3);
        container.setLayoutY(container.getLayoutY() + ySign * 3);

        if (pathPaneSequence.get(currentTileListIndex + 1).getBoundsInParent().intersects(container.getBoundsInParent())) {
            currentTileListIndex++;
            firstTile = false;
        }

        if (currentTileListIndex >= pathTilePoints.size()) {
            getReadyToFinish();
            this.stop();
        }

        if (firstTile) currentTileListIndex = 0;
    }

    private void getReadyToFinish() {
        if (currentTileListIndex > 0)
            while (pathPaneSequence.get(currentTileListIndex - 1).getBoundsInParent().intersects(container.getBoundsInParent())) {
                container.setLayoutX(container.getLayoutX() + xSign * 5);
                container.setLayoutY(container.getLayoutY() + ySign * 5);
            }
        //System.out.println((int) Math.floor(root.getChildren().get(unitIndexInRoot).getLayoutX() / 120));
        System.out.println("I finish" + xSign + ", " + ySign);
        root.getChildren().remove(container);
    }
}
