package controller;

import model.Map;
import model.Point;
import model.Tile;
import model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PatchFinding {
    private static User currentForce;
    private static boolean clearance = false;

    public static User getCurrentForce() {
        return currentForce;
    }

    public static boolean isClearance() {
        return clearance;
    }

    public static void setClearance(boolean clearance) {
        PatchFinding.clearance = clearance;
    }

    public static void setCurrentForce(User currentForce) {
        PatchFinding.currentForce = currentForce;
    }

    public static List<Point> findPath(Map map, Point startPos, Point targetPos, boolean allowDiagonals) {
        // Find path
        List<Tile> pathInTile = findPathNodes(map, startPos, targetPos, allowDiagonals);

        // Convert to a list of points and return
        List<Point> pathInPoints = new ArrayList<Point>();

        if (pathInTile != null)
            for (Tile tile : pathInTile)
                pathInPoints.add(new Point(tile.getX(), tile.getY()));

        return pathInPoints;
    }

    private static List<Tile> findPathNodes(Map map, Point startPos, Point targetPos, boolean allowDiagonals) {
        Tile startTile = map.getSelectedMap()[startPos.getX()][startPos.getY()];
        Tile targetTile = map.getSelectedMap()[targetPos.getX()][targetPos.getY()];

        List<Tile> openSet = new ArrayList<Tile>();
        HashSet<Tile> closedSet = new HashSet<Tile>();
        openSet.add(startTile);

        while (openSet.size() > 0) {
            Tile currentTile = openSet.get(0);

            for (int k = 1; k < openSet.size(); k++) {
                Tile open = openSet.get(k);

                if (open.getFCost() < currentTile.getFCost() ||
                        open.getFCost() == currentTile.getFCost() &&
                                open.gethCost() < currentTile.gethCost())
                    currentTile = open;
            }

            openSet.remove(currentTile);
            closedSet.add(currentTile);

            if (currentTile == targetTile)
                return retracePath(startTile, targetTile);

            List<Tile> neighbours;
            if (allowDiagonals) neighbours = map.get8Neighbours(currentTile);
            else neighbours = map.get4Neighbours(currentTile);

            for (Tile neighbour : neighbours) {
                if (!neighbour.getTexture().isWalkability() || closedSet.contains(neighbour)) continue;
                if (neighbour.existTree() || !neighbour.checkPossibleBuilding(currentForce)) continue;

                int newMovementCostToNeighbour = currentTile.getgCost() + getDistance(currentTile, neighbour) * (int) (10.0f * neighbour.getPrice(currentForce));
                if (newMovementCostToNeighbour < neighbour.getgCost() || !openSet.contains(neighbour)) {
                    neighbour.setgCost(newMovementCostToNeighbour);
                    neighbour.sethCost(getDistance(neighbour, targetTile));
                    neighbour.setParent(currentTile);

                    if (!openSet.contains(neighbour)) openSet.add(neighbour);
                }
            }
        }

        return null;
    }

    private static List<Tile> retracePath(Tile startTile, Tile endTile) {
        List<Tile> path = new ArrayList<Tile>();
        Tile currentNode = endTile;

        while (currentNode != startTile) {
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private static int getDistance(Tile tileA, Tile tileB) {
        int distanceX = Math.abs(tileA.getX() - tileB.getX());
        int distanceY = Math.abs(tileA.getY() - tileB.getY());
        if (distanceX > distanceY)
            return 5 * distanceY + 10 * (distanceX - distanceY);
        return 5 * distanceX + 10 * (distanceY - distanceX);
    }
}
