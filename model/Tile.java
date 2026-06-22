package model;

import controller.PatchFinding;
import controller.gameMenuControllers.GameController;
import model.buildings.*;
import model.units.Troop;
import model.units.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tile implements Comparable<Tile> {
    private int x;
    private int y;
    private Tile parent;
    private boolean hasTrap;
    private int gCost;
    private int hCost;
    private int distance;
    private String rockDirection = "#";
    private TileTexture texture = TileTexture.EARTH;
    private ArrayList<Tree> trees = new ArrayList<>();
    private HashMap<String, ArrayList<Unit>> playersUnits = new HashMap<>();
    //assuming a tile can have more than one tree. (since it can have multiple units)
    private ArrayList<Building> buildings = new ArrayList<>();

    public boolean isTrap() {
        return hasTrap;
    }

    public void setHasTrap(boolean hasTrap) {
        this.hasTrap = hasTrap;
    }

    public ArrayList<Tree> getTrees() {
        return trees;
    }

    public TileTexture getTexture() {
        return texture;
    }

    public void setTexture(TileTexture texture) {
        this.texture = texture;
    }


    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    public String getRockDirection() {
        return rockDirection;
    }

    public void setRockDirection(String rockDirection) {
        this.rockDirection = rockDirection;
    }

    public char getTileOccupation() {
        if (this.playersUnits.size() != 0)
            return 'S';
        else if (this.buildings.size() > 0)
            return 'B';
            //todo: after adding towers and walls, return W
        else if (trees.size() > 0)
            return 'T';
        else
            return '#';
    }

    public void clear() {
        this.buildings.clear();
        this.trees.clear();
        this.playersUnits.clear();
        this.texture = TileTexture.EARTH;
    }

    public String showBuildings() {
        String ans = "\nBuilding(s) here:";
        for (Building building : this.buildings) {
            ans += "\n" + building.getType().getName() + " -> hp: " + building.getHp();
        }
        return ans;
    }

    public ArrayList<Unit> findYourUnits(User master) {
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet()) {
            if (arrayListEntry.getKey().equals(master.getUsername()))
                return arrayListEntry.getValue();
        }
        return null;
    }

    public void setPetrolStatus(User master, int xorigin, int yorigin, Point[] des) {
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet()) {
            if (arrayListEntry.getKey().equals(master.getUsername())) {
                for (Unit unit : arrayListEntry.getValue())
                    if (unit.getxOrigin() == xorigin && unit.getyOrigin() == yorigin) {
                        unit.setPatrolDestinations(des);
                        unit.setOnPatrol(true);
                        //you might want to break here
                    }
            }
        }
    }

    public Unit findUnitByOrigin(User master, int xorigin, int yorigin) {
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet()) {
            if (arrayListEntry.getKey().equals(master.getUsername())) {
                for (Unit unit : arrayListEntry.getValue())
                    if (unit.getxOrigin() == xorigin && unit.getyOrigin() == yorigin)
                        return unit;
            }
        }
        return null;
    }

    public int getLongRangeDamage(User master, int xorigin, int yorigin, int distance) {
        Unit selected = findUnitByOrigin(master, xorigin, yorigin);
        int totalDamage = 0;
        for (Troop troop : selected.getTroops()) {
            if (troop.getType().getRange() >= distance)
                totalDamage += troop.getType().getDamage() * (master.getGovernance().getFearRate() * 5 + 100);
        }
        return totalDamage / 100;
    }

    public boolean changeState(String state, User master) {
        boolean exist = false;
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet())
            if (arrayListEntry.getKey().equals(master.getUsername())) {
                exist = true;
                for (Unit unit : arrayListEntry.getValue()) {
                    unit.setState(state);
                }
            }
        return exist;
    }

    public void unifyYourUnits(ArrayList<Unit> replacement, User master) {
        if (replacement == null || replacement.size() == 0)
            this.playersUnits.remove(master.getUsername());
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet()) {
            if (arrayListEntry.getKey().equals(master.getUsername()))
                arrayListEntry.setValue(replacement);
        }
    }

    public void addUnitToTile(Unit unit) {
        if (this.playersUnits.get(unit.getMaster().getUsername()) == null ||
                this.playersUnits.get(unit.getMaster().getUsername()).size() == 0) {
            ArrayList<Unit> addingUnit = new ArrayList<>();
            addingUnit.add(unit);
            this.playersUnits.put(unit.getMaster().getUsername(), addingUnit);
        } else {
            this.playersUnits.get(unit.getMaster().getUsername()).add(unit);
        }
    }

    public boolean areEnemiesHere(User current) {
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet()) {
            if (!arrayListEntry.getKey().equals(current.getUsername()))
                return true;
        }
        return false;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Tile getParent() {
        return parent;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setParent(Tile parent) {
        this.parent = parent;
    }

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getFCost() {
        return gCost + hCost;
    }

    public int getgCost() {
        return gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }

    public float getPrice(User currentForce) {
        if (!texture.isWalkability() || existTree()) return 0.0f;
        if (!checkPossibleBuilding(currentForce)) return 0.0f;
        return 1.0f;
    }

    public boolean existTree() {
        if (trees != null) return false;
        return true;
    }

    public boolean checkPossibleBuilding(User currentForce) {
        for (Building building : buildings) {
            if (building instanceof Gate || building.getType().equals(BuildingEnum.SMALL_WALL) || building.getType().equals(BuildingEnum.BIG_WALL)) {
                if (PatchFinding.isClearance()) {
                    PatchFinding.setClearance(false);
                    return true;
                }
            }
            if (building instanceof Gate) {
                PatchFinding.setClearance(false);
                if (!((Gate) building).isOpen()) return false;
            } else if (building instanceof Trap) {
                PatchFinding.setClearance(false);
                if (building.getOwner().getUsername().equals(currentForce.getUsername())) return false;
                if (((Trap) building).isVisible()) return false;
            } else {
                if (building.getType().equals(BuildingEnum.STAIR))
                    PatchFinding.setClearance(true);
                return false;
            }
        }
        return true;
    }


    public HashMap<String, ArrayList<Unit>> getPlayersUnits() {
        return playersUnits;
    }

    public void removeAUnit(Unit unit) {
        ArrayList<Unit> usersUnits = findYourUnits(unit.getMaster());
        ArrayList<Unit> replacement = new ArrayList<>();
        for (Unit usersUnit : usersUnits) {
            if (usersUnit.getxOrigin() != unit.getxOrigin() || usersUnit.getyOrigin() != unit.getyOrigin())
                replacement.add(usersUnit);
        }
        unifyYourUnits(replacement, unit.getMaster());
    }

    public void damageAllEnemies(User master, int damage) {
        for (Map.Entry<String, ArrayList<Unit>> arrayListEntry : this.playersUnits.entrySet()) {
            if (!arrayListEntry.getKey().equals(master.getUsername())) {
                for (Unit unit : arrayListEntry.getValue()) {
                    unit.takeSteadyDamageForAll(damage);
                    unit.clearTheDead();
                }
            }
        }
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(Tile other) {
        return Double.compare(distance, other.distance);
    }

    public boolean doWeHaveWallsOrGates(User currentPlayer) {
        for (Building building : buildings) {
            if (building.getOwner().getUsername().equals(currentPlayer.getUsername()))
                if (building.getType().equals(BuildingEnum.SMALL_WALL) || building.getType().equals(BuildingEnum.BIG_WALL) ||
                        building instanceof Gate)
                    return true;
        }
        return false;
    }

    public void setTrees(ArrayList<Tree> trees) {
        this.trees = trees;
    }

    public void setPlayersUnits(HashMap<String, ArrayList<Unit>> playersUnits) {
        this.playersUnits = playersUnits;
    }

    public Tile copy() {
        Tile tile = new Tile(this.getX(), this.getY());
        for (Tree tree : this.getTrees()) {
            tile.getTrees().add(tree);
        }
        for (Building buildnig : this.getBuildings()) {
            tile.getBuildings().add(buildnig);
        }
        for (Map.Entry<String, ArrayList<Unit>> stringArrayListEntry : this.playersUnits.entrySet()) {
            tile.getPlayersUnits().put(stringArrayListEntry.getKey(), stringArrayListEntry.getValue());
        }
        return tile;
    }
}
