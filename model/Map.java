package model;

import java.util.ArrayList;
import java.util.List;

public class Map {
    Tile[][] selectedMap;
    private int width;
    private int length;

    public Map(int width, int length) {
        this.length = length;
        this.width = width;
        selectedMap = new Tile[width][length];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++) {
                selectedMap[i][j] = new Tile(j, i);
            }
    }

    public Tile getTile(int y, int x) {
        return this.selectedMap[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public List<Tile> get8Neighbours(Tile tile) {
        List<Tile> neighbours = new ArrayList<Tile>();

        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;

                int checkX = tile.getX() + x;
                int checkY = tile.getY() + y;

                if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < length)
                    neighbours.add(selectedMap[checkX][checkY]);
            }

        return neighbours;
    }

    public List<Tile> get4Neighbours(Tile tile) {
        List<Tile> neighbours = new ArrayList<Tile>();

        if (tile.getY() + 1 >= 0 && tile.getY() < length) neighbours.add(selectedMap[tile.getX()][tile.getY()]); // N
        if (tile.getY() - 1 >= 0 && tile.getY() - 1 < length)
            neighbours.add(selectedMap[tile.getX()][tile.getY()]); // S
        if (tile.getX() + 1 >= 0 && tile.getX() + 1 < width) neighbours.add(selectedMap[tile.getX()][tile.getY()]); // E
        if (tile.getX() - 1 >= 0 && tile.getX() - 1 < width) neighbours.add(selectedMap[tile.getX()][tile.getY()]); // W

        return neighbours;
    }

    public Tile[][] getSelectedMap() {
        return selectedMap;
    }

}
