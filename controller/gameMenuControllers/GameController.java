package controller.gameMenuControllers;

import controller.CommonController;
import controller.PatchFinding;
import model.*;
import model.buildings.*;
import model.units.Troop;
import model.units.Unit;
import model.units.UnitEnum;
import view.enums.GameControllerOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {
    private User CurrentUser;
    private int xCoor;
    private int yCoor;
    private Map selectedMap;
    private Building selectedBuilding;
    private int xOFSelectedBuilding;
    private int yOFSelectedBuilding;
    private int indexOFSelectedBuilding;
    private int xOriginOFSelectedUnit = -1;
    private int yOriginOFSelectedUnit = -1;

    public void prepareForNextPlayer(User currentUser) {
        this.xOriginOFSelectedUnit = -1;
        this.yOriginOFSelectedUnit = -1;
        this.selectedBuilding = null;
        this.xOFSelectedBuilding = 0;
        this.yOFSelectedBuilding = 0;
        this.xCoor = 0;
        this.yCoor = 0;
        this.indexOFSelectedBuilding = 0;
        this.CurrentUser = currentUser;
    }


    public User getCurrentUser() {
        return CurrentUser;
    }

    public void setCurrentUser(User currentUser) {
        CurrentUser = currentUser;
    }

    public String showPopularityFactors() {
        String ans = "";
        ans += "Food diversity: " + getCurrentUser().getGovernance().getFoodDiversity() + "\n";
        ans += "Food rate:      " + this.CurrentUser.getGovernance().getFoodRate() + "\n";
        ans += "Fear rate       " + this.CurrentUser.getGovernance().getFearRate() + "\n";
        ans += "Tax rate:       " + this.CurrentUser.getGovernance().getTaxRate();
        return ans;
    }

    public String showPopularity() {
        return "This is your popularity: " + this.CurrentUser.getGovernance().getPopularity();
    }

    public int getPerformance(User ruler) {
        int fearRate = ruler.getGovernance().getFearRate();
        return fearRate * 5 + 100;
    }

    public GameControllerOut setFoodRate(String rateNumber) {
        if (this.CurrentUser.getGovernance().getResourceAmount(ResourceEnum.APPLE) == 0 &&
                this.CurrentUser.getGovernance().getResourceAmount(ResourceEnum.BREAD) == 0 &&
                this.CurrentUser.getGovernance().getResourceAmount(ResourceEnum.CHEESE) == 0 &&
                this.CurrentUser.getGovernance().getResourceAmount(ResourceEnum.MEAT) == 0)
            return GameControllerOut.NO_FOOD_NO_RATE_CHANGE;
        if (rateNumber == null || rateNumber.length() == 0 || rateNumber.trim().length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        int rate = Integer.parseInt(rateNumber.trim());
        if (rate > 2 || rate < -2) return GameControllerOut.INVALID_NUMBER_INPUT;
        this.CurrentUser.getGovernance().changeFoodRate(rate);
        return GameControllerOut.SUCCESSFULLY_CHANGED_FOODRATE;
    }

    public String showFoodRate() {
        return "This is the food rate: " + this.CurrentUser.getGovernance().getFoodRate();
    }

    public GameControllerOut setTaxRate(String rateNumber) {
        if (this.CurrentUser.getGovernance().getGold() <= 0)
            return GameControllerOut.NO_GOLD_NO_RATE_CHANGE;
        int rate = Integer.parseInt(rateNumber.trim());
        if (rate < -3 || rate > 8) return GameControllerOut.INVALID_NUMBER_INPUT;
        this.CurrentUser.getGovernance().changeTaxRate(rate);
        return GameControllerOut.SUCCESSFULLY_CHANGED_TAXRATE;
    }


    public String showTaxRate() {
        return "This is tax rate: " + this.CurrentUser.getGovernance().getTaxRate();
    }

    public boolean extractDataxandy(String data) {
        String x = CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        String y = CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        if (x.length() == 0 || y.length() == 0) return false;
        if (x.trim().length() == 0 || y.trim().length() == 0) return false;
        xCoor = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        yCoor = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        yCoor = selectedMap.getWidth() - 1 - yCoor;
        return true;
    }

    public boolean validateCoordinates(int mapLength, int mapWidth) {
        return xCoor >= 0 && xCoor <= mapLength - 1 && yCoor >= 0 && yCoor <= mapWidth - 1;
    }

    public String selectBuilding(String data) {
        if (!extractDataxandy(data))
            return GameControllerOut.INVALID_INPUT_FORMAT.getContent();
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return GameControllerOut.INVALID_COORDINATES.getContent();
        if (selectedMap.getTile(yCoor, xCoor).getBuildings().size() == 0)
            return GameControllerOut.NO_BUILDING.getContent();
        boolean exist = false;
        for (int i = 0; i < selectedMap.getTile(yCoor, xCoor).getBuildings().size(); i++) {
            if (selectedMap.getTile(yCoor, xCoor).getBuildings().get(i)
                    .getOwner().getUsername().equals(this.CurrentUser.getUsername())) {
                exist = true;
                this.selectedBuilding = selectedMap.getTile(yCoor, xCoor).getBuildings().get(i);
                this.xOFSelectedBuilding = xCoor;
                this.yOFSelectedBuilding = yCoor;
                this.indexOFSelectedBuilding = i;
                break;
            }
        }
        xCoor = -1;
        yCoor = -1;
        if (!exist)
            return GameControllerOut.NO_BUILDING.getContent();
        else
            return GameControllerOut.SUCCESSFULLY_SELECTED_BUILDING.manipulateSelectBuilding(selectedBuilding.getType());
    }

    public GameController(User currentUser, Map selectedMap) {
        CurrentUser = currentUser;
        this.selectedMap = selectedMap;
    }

    public void setSelectedMap(Map selectedMap) {
        this.selectedMap = selectedMap;
    }

    public GameControllerOut createUnit(String data, boolean isDrop) {
        String type = CommonController.dataExtractor(data, "((?<!\\S)-t\\s+(?<wantedPart>([^-]+))(?<!\\s))");
        String countStr = CommonController.dataExtractor(data, "((?<!\\S)-c\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        if (type.length() == 0 || countStr.length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (type.trim().length() == 0 || countStr.trim().length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        int count = Integer.parseInt(countStr.trim());
        type = type.trim();
        UnitEnum unitType = CommonController.unitTypeSpecifier(type);
        if (unitType == null)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (count == 0)
            return GameControllerOut.ZERO;
        if (getCurrentUser().getGovernance().getGold() < unitType.getCost() * count)
            return GameControllerOut.NOT_ENOUGH_GOLD;
        if (!unitType.getWeaponType().equals(ResourceEnum.NULL))
            if (getCurrentUser().getGovernance().getResourceAmount(unitType.getWeaponType()) < count)
                return GameControllerOut.NOT_ENOUGH_WEAPON;
        if (getCurrentUser().getGovernance().getUnemployedPopulation() < count)
            return GameControllerOut.NOT_ENOUGH_PEOPLE;
        if (!checkPlace(unitType, isDrop)) {
            if (!isDrop)
                return GameControllerOut.WRONG_LOCATION;
            else
                return GameControllerOut.DONT_HAVE_THE_BUILDING;
        }
        getCurrentUser().getGovernance().changeGold(-1 * unitType.getCost() * count);
        if (!unitType.getWeaponType().equals(ResourceEnum.NULL))
            getCurrentUser().getGovernance().changeResourceAmount(unitType.getWeaponType(), -1 * count);
        Unit addingUnit = new Unit(getCurrentUser(), unitType, count, yOFSelectedBuilding, xOFSelectedBuilding);
        selectedMap.getTile(yOFSelectedBuilding, xOFSelectedBuilding).addUnitToTile(addingUnit);
        addingUnit.setCurrentTile(selectedMap.getTile(yOFSelectedBuilding, xOFSelectedBuilding));
        getCurrentUser().getGovernance().addUnit(addingUnit);
        return GameControllerOut.SUCCESSFULLY_CREATED_UNIT;
    }


    public GameControllerOut dropUnit(String data) {
        int xtemp = xOFSelectedBuilding;
        int ytemp = yOFSelectedBuilding;
        int xcoortemp = xCoor;
        int ycoortemp = yCoor;
        if (!extractDataxandy(data)) {
            xOFSelectedBuilding = xtemp;
            yOFSelectedBuilding = ytemp;
            xCoor = xcoortemp;
            yCoor = ycoortemp;
            return GameControllerOut.INVALID_INPUT_FORMAT;
        }
        xOFSelectedBuilding = xCoor;
        yOFSelectedBuilding = yCoor;
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth())) {
            xOFSelectedBuilding = xtemp;
            yOFSelectedBuilding = ytemp;
            xCoor = xcoortemp;
            yCoor = ycoortemp;
            return GameControllerOut.INVALID_COORDINATES;
        }
        GameControllerOut result = createUnit(data, true);
        xOFSelectedBuilding = xtemp;
        yOFSelectedBuilding = ytemp;
        xCoor = xcoortemp;
        yCoor = ycoortemp;
        return result;
    }

    private boolean checkPlace(UnitEnum unitType, boolean isDrop) {
        if (!isDrop) {
            if (selectedBuilding == null)
                return false;
            if (unitType.getName().equals("engineer"))
                return selectedBuilding.getType().equals(BuildingEnum.ENGINEERS_GUILD);
            else if (unitType.isArab() && selectedBuilding.getType().equals(BuildingEnum.MERCENARY_POST))
                return true;
            else if (!unitType.isArab() && selectedBuilding.getType().equals(BuildingEnum.BARRACKS))
                return true;
        } else {
            if (unitType.getName().equals("engineer"))
                return doesBuildingTypeExists(getCurrentUser().getGovernance().getBuildings(), BuildingEnum.ENGINEERS_GUILD);
            else if (unitType.isArab() && doesBuildingTypeExists(getCurrentUser().getGovernance().getBuildings(), BuildingEnum.MERCENARY_POST))
                return true;
            else if (!unitType.isArab() && doesBuildingTypeExists(getCurrentUser().getGovernance().getBuildings(), BuildingEnum.BARRACKS))
                return true;
        }
        return false;
    }

    private boolean doesBuildingTypeExists(ArrayList<Building> buildings, BuildingEnum type) {
        for (Building building : buildings) {
            if (building.getType().equals(type))
                return true;
        }
        return false;
    }

    public GameControllerOut repair() {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (selectedMap.getTile(yOFSelectedBuilding + i, xOFSelectedBuilding + j)
                        .areEnemiesHere(getCurrentUser()))
                    return GameControllerOut.ENEMIES_NEAR;
        Resource neededResource = selectedBuilding.getType().getResource();
        if (getCurrentUser().getGovernance().getResourceAmount(neededResource.getType()) <
                neededResource.getAmount())
            return GameControllerOut.NOT_ENOUGH_RESOURCES;
        if (selectedBuilding.getHp() == selectedBuilding.getType().getOriginalHp())
            return GameControllerOut.FULL_HP;
        getCurrentUser().getGovernance().changeResourceAmount(neededResource.getType(), -1 * neededResource.getAmount());
        selectedMap.getTile(yOFSelectedBuilding, xOFSelectedBuilding).getBuildings().get(indexOFSelectedBuilding).resetHp();
        return GameControllerOut.SUCCESSFULLY_REPAIRED;
    }

    public GameControllerOut selectUnit(String data) {
        ArrayList<Integer> xOrigins = new ArrayList<>();
        ArrayList<Integer> yOrigins = new ArrayList<>();
        if (!extractDataxandy(data))
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return GameControllerOut.INVALID_COORDINATES;
        ArrayList<Unit> separate = selectedMap.getTile(yCoor, xCoor).findYourUnits(getCurrentUser());
        if (separate == null)
            return GameControllerOut.NO_UNIT;
        Unit combined = separate.get(0);
        xOrigins.add(combined.getxOrigin());
        yOrigins.add(combined.getyOrigin());
        for (int i = 1; i < separate.size(); i++) {
            combined.addByUnit(separate.get(i));
            xOrigins.add(combined.getxOrigin());
            yOrigins.add(combined.getyOrigin());
        }
        //adding troops which are in a different tile, but are part of the unit:
//        for (int i = 0; i < selectedMap.getWidth(); i++)
//            for (int j = 0; j < selectedMap.getLength(); j++) {
//                ArrayList<Unit> seperatedUnits = selectedMap.getTile(i, j).findYourUnits(getCurrentUser());
//                for (Unit unit : seperatedUnits) {
//                    if (matchPrimaryCoordinate(xOrigins, yOrigins, unit)) {
//                        unit.setxOrigin(xCoor);
//                        unit.setyOrigin(yCoor);
//                    }
//                }
//            }
        combined.setxOrigin(xCoor);
        combined.setyOrigin(yCoor);
        xOriginOFSelectedUnit = xCoor;
        yOriginOFSelectedUnit = yCoor;
        ArrayList<Unit> replacement = new ArrayList<>();
        replacement.add(combined);
        selectedMap.getTile(yCoor, xCoor).unifyYourUnits(replacement, getCurrentUser());
        return GameControllerOut.SUCCESSFULLY_SELECTED_UNIT;
    }

    public boolean matchPrimaryCoordinate(ArrayList<Integer> x, ArrayList<Integer> y, Unit unit) {
        for (int i = 0; i < x.size(); i++)
            if (unit.getxOrigin() == x.get(i) && unit.getyOrigin() == y.get(i))
                return true;
        return false;
    }

    public Building getSelectedBuilding() {
        return selectedBuilding;
    }

    public void setSelectedBuilding(Building selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
    }

    public GameControllerOut setFearRate(String rateNumber) {
        if (rateNumber == null || rateNumber.length() == 0 || rateNumber.trim().length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        int rate = Integer.parseInt(rateNumber.trim());
        if (rate < -5 || rate > 5)
            return GameControllerOut.INVALID_FEAR_INPUT;
        getCurrentUser().getGovernance().changeFearRate(rate);
        //more fear, less popularity:
        return GameControllerOut.SUCCESSFULLY_CHANGED_FEAR_RATE;
    }


    public String showFoodList() {
        String ans = "";
        int count = getCurrentUser().getGovernance().getResourceAmount(ResourceEnum.MEAT);
        if (count > 0)
            ans += "Meat   -> " + count + "\n";
        count = getCurrentUser().getGovernance().getResourceAmount(ResourceEnum.BREAD);
        if (count > 0)
            ans += "Bread  -> " + count + "\n";
        count = getCurrentUser().getGovernance().getResourceAmount(ResourceEnum.CHEESE);
        if (count > 0)
            ans += "Cheese -> " + count + "\n";
        count = getCurrentUser().getGovernance().getResourceAmount(ResourceEnum.APPLE);
        if (count > 0)
            ans += "Apple  -> " + count + "\n";
        return ans;
    }

    public GameControllerOut setState(String data) {
        if (!extractDataxandy(data))
            return GameControllerOut.INVALID_INPUT_FORMAT;
        String state = CommonController.dataExtractor(data, "((?<!\\S)-s\\s+(?<wantedPart>\\S+)(?<!\\s))");
        if (state == null || state.length() == 0 || state.trim().length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        state = state.trim();
        if (!state.equals("standing") && !state.equals("defensive") && !state.equals("offensive")) ;
        if (!this.selectedMap.getTile(yCoor, xCoor).changeState(state, getCurrentUser()))
            return GameControllerOut.NO_UNIT;
        return GameControllerOut.SUCCESSFULLY_CHANGRD_UNIT_STATE;
    }

    public Map getSelectedMap() {
        return selectedMap;
    }

    public GameControllerOut buildGateHouse(int ans, int direction, String coordinateInput) {
        if (!extractDataxandy(coordinateInput))
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return GameControllerOut.INVALID_COORDINATES;
        if (ans == 2 && getCurrentUser().getGovernance().getGold() < 20)
            return GameControllerOut.NOT_ENOUGH_GOLD;
        if (!selectedMap.getTile(yCoor, xCoor).getTexture().isConstructiblity())
            return GameControllerOut.NOT_A_SPOT;
        //assuming more than one building can be in a single spot.
        Gate addingGate = ans == 1 ? new Gate(BuildingEnum.SMALL_STONE_GATEHOUSE, getCurrentUser(), direction, true) :
                new Gate(BuildingEnum.BIG_STONE_GATEHOUSE, getCurrentUser(), direction, true);
        selectedMap.getTile(yCoor, xCoor).getBuildings().add(addingGate);
        if (ans == 1)
            getCurrentUser().getGovernance().changeMaximumPopulation(8);
        else
            getCurrentUser().getGovernance().changeMaximumPopulation(10);
        getCurrentUser().getGovernance().setHaveGateHouse(true);
        if (ans == 2)
            getCurrentUser().getGovernance().changeGold(-20);
        return GameControllerOut.SUCCESSFULLY_ADDED_GATEHOUSE;
    }


    private int[] findUnit(User master, int xOrigin, int yOrigin, Map map) {
        int[] ans = new int[2];
        for (int i = 0; i < map.getWidth(); i++)
            for (int j = 0; j < map.getLength(); j++)
                if (map.getTile(i, j).findYourUnits(master) != null)
                    for (Unit unit : map.getTile(i, j).findYourUnits(master))
                        if (unit.getxOrigin() == xOrigin && unit.getyOrigin() == yOrigin) {
                            ans[0] = i;
                            ans[1] = j;
                        }
        //first   one is y
        //second  one is x
        return ans;
    }

    public GameControllerOut moveUnit(String data) {
        if (!extractDataxandy(data))
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return GameControllerOut.INVALID_COORDINATES;
        if (xOriginOFSelectedUnit == -1)
            return GameControllerOut.SELECT_A_UNIT_FIRST;
        PatchFinding.setCurrentForce(getCurrentUser());
        int[] currentLocation = findUnit(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit, selectedMap);
        List<Point> patchPoints = PatchFinding.findPath(selectedMap, new Point(currentLocation[1], currentLocation[0]),
                new Point(xCoor, yCoor), true);
        if (patchPoints.size() == 0)
            return GameControllerOut.CANT_MOVE;
        selectedMap.getTile(currentLocation[0], currentLocation[1]).findYourUnits(getCurrentUser()).get(0)
                .setxDestination(xCoor);
        selectedMap.getTile(currentLocation[0], currentLocation[1]).findYourUnits(getCurrentUser()).get(0)
                .setyDestination(yCoor);
        moveForwardThePath(selectedMap.getTile(currentLocation[0], currentLocation[1]).findYourUnits(getCurrentUser()).get(0),
                patchPoints, currentLocation[1], currentLocation[0]);
        return GameControllerOut.BEGIN_TO_MOVE;
    }

    private void moveForwardThePath(Unit unit, List<Point> patchPoints, int xOrigin, int yOrigin) {
//        boolean bumer = false;
//        for (Point patchPoint : patchPoints) {
//            if (bumer)
//                System.out.println(patchPoint.getX() + " , " + patchPoint.getY());
//            else System.out.println(patchPoint.getY() + " , " + patchPoint.getX());
//            bumer = !bumer;
//        }
        boolean jumper = false;
        int xPresent = 0;
        int yPresent = 0;
        int previousX = xOrigin;
        int previousY = yOrigin;
        int counter = 0;
        for (Point point : patchPoints) {

            if (jumper) {
                xPresent = point.getY();
                yPresent = point.getX();
            } else {
                xPresent = point.getX();
                yPresent = point.getY();
            }
            jumper = !jumper;

            counter += Math.abs(previousX - xPresent) + Math.abs(previousY - yPresent);
            if (counter > unit.getSpeed())
                return;
//            System.out.println("previous: " + previousX + "," + previousY);
//            System.out.println("now: " + xPresent + "," + yPresent);
//            System.out.println("unit: " + unit.getxDestination() + "," + unit.getyDestination());

            selectedMap.getTile(previousY, previousX).removeAUnit(unit);
            selectedMap.getTile(yPresent, xPresent).addUnitToTile(unit);
            unit.setCurrentTile(selectedMap.getTile(yPresent, xPresent));

            //battle and traps:
            getCaughtByTraps(selectedMap.getTile(yPresent, xPresent), unit);
            fight(selectedMap.getTile(yPresent, xPresent));
            damageBuildings(selectedMap.getTile(yPresent, xPresent), unit);

            if (unit.getTroops().size() == 0)
                return;
            previousX = xPresent;
            previousY = yPresent;
        }
        if (xPresent != unit.getxDestination() || yPresent != unit.getyDestination())
            return;
        if (unit.isOnPatrol()) {
            if (unit.getPatrolDestinations()[0].getX() == xPresent &&
                    unit.getPatrolDestinations()[0].getY() == yPresent) {
                unit.setxDestination(unit.getPatrolDestinations()[1].getX());
                unit.setyDestination(unit.getPatrolDestinations()[1].getY());
            } else {
                unit.setxDestination(unit.getPatrolDestinations()[0].getX());
                unit.setyDestination(unit.getPatrolDestinations()[0].getY());
            }
        } else {
            unit.setxDestination(-1);
            unit.setyDestination(-1);
        }
    }

    private void damageBuildings(Tile tile, Unit unit) {
        int totalDamage = 0;
        int performance = getPerformance(getCurrentUser());
        for (Troop troop : unit.getTroops()) {
            if (!troop.isDead())
                totalDamage += troop.getType().getDamage() * performance;
        }
        for (Building building : tile.getBuildings()) {
            if (!building.getOwner().getUsername().equals(unit.getMaster().getUsername())) {
                if (unit.getPresentTypes().contains("fire thrower")) {
                    building.resetTurnsOnFire();
                }
                building.takeDamage(totalDamage / 100);
            }
        }
    }

    public void setOnFire() {
        for (int i = 0; i < selectedMap.getWidth(); i++)
            for (int j = 0; j < selectedMap.getLength(); j++) {
                outer:
                for (Building building : selectedMap.getTile(i, j).getBuildings()) {
                    for (java.util.Map.Entry<String, ArrayList<Unit>> stringArrayListEntry : selectedMap.getTile(i, j).getPlayersUnits().entrySet()) {
                        if (!stringArrayListEntry.getKey().equals(building.getOwner().getUsername())) {
                            boolean isInDanger = smellsDanger(stringArrayListEntry.getValue());
                            if (isInDanger) {
                                System.out.println("I'm seeing fire");
                                building.resetTurnsOnFire();
                                continue outer;
                            }
                        }
                    }
                }
            }
    }

    private void fight(Tile tile) {
        String havaFought = "|";
        for (java.util.Map.Entry<String, ArrayList<Unit>> arrayListEntry : tile.getPlayersUnits().entrySet())
            for (java.util.Map.Entry<String, ArrayList<Unit>> arrayListEntry2 : tile.getPlayersUnits().entrySet())
                if (!arrayListEntry.getKey().equals(arrayListEntry2.getKey())) {
                    String history = arrayListEntry.getKey() + "+" + arrayListEntry2.getKey() + "|";
                    if (havaFought.contains("|" + history))
                        continue;
                    for (Unit unit : arrayListEntry.getValue())
                        for (Unit unit1 : arrayListEntry2.getValue()) {
                            fightOfTwoUnits(unit, unit1);
                            unit.clearTheDead();
                            unit1.clearTheDead();
                        }
                    havaFought += history;
                }
        for (java.util.Map.Entry<String, ArrayList<Unit>> arrayListEntry : tile.getPlayersUnits().entrySet()) {
            ArrayList<Unit> units = arrayListEntry.getValue();
            for (Unit unit : units)
                shootNeighbors(tile, unit);
        }
    }

    private void shootNeighbors(Tile tile, Unit unit) {
        if (unit.getTroops().size() == 0) return;
        int performance = getPerformance(unit.getMaster());
        for (Troop troop : unit.getTroops()) {
            int range = troop.getType().getRange();
            for (int i = -1 * range; i < range; i++)
                for (int j = -1 * range; j < range; j++)
                    if (i + j <= range) {
                        int tempxCoor = xCoor;
                        int tempyCoor = yCoor;
                        xCoor = tile.getX() + j;
                        yCoor = tile.getY() + i;
                        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth())) {
                            xCoor = tempxCoor;
                            yCoor = tempyCoor;
                            continue;
                        }
                        if (tile.areEnemiesHere(unit.getMaster()))
                            tile.damageAllEnemies(unit.getMaster(), troop.getType().getDamage() * performance / 100);
                        xCoor = tempxCoor;
                        yCoor = tempyCoor;
                    }
        }
    }

    private void fightOfTwoUnits(Unit unit, Unit unit1) {
        if (unit.getTroops().size() == 0 || unit1.getTroops().size() == 0) return;
        for (Troop troop : unit.getTroops()) {
            for (Troop unit1Troop : unit1.getTroops())
                if (!unit1.getMaster().getUsername().equals(unit.getMaster().getUsername())) {
                    if (!unit1Troop.isDead())
                        troop.takeDamage(unit1Troop.getType().getDamage() * getPerformance(unit1.getMaster()) / 100);
                    if (!troop.isDead())
                        unit1Troop.takeDamage(troop.getType().getDamage() * getPerformance(unit.getMaster()) / 100);
                }
        }
    }

    private void getCaughtByTraps(Tile tile, Unit unit) {
        if (tile.isTrap()) {
            for (Building building : tile.getBuildings())
                if (building instanceof Trap && !building.getOwner().getUsername().equals(unit.getMaster().getUsername())) {
                    unit.takeSteadyDamageForAll(((Trap) building).getDamage());
                    unit.clearTheDead();
                }
        }
    }

    public GameControllerOut patrolUnit(String data) {
        if (!extractDataxandy(data))
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return GameControllerOut.INVALID_COORDINATES;
        if (CommonController.dataExtractor(data, "((?<!\\S)-x2\\s+(?<wantedPart>(\\d+))(?<!\\s))").length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-y2\\s+(?<wantedPart>(\\d+))(?<!\\s))").length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (CommonController.dataExtractor(data, "((?<!\\S)-x2\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim().length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-y2\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim().length() == 0)
            return GameControllerOut.INVALID_INPUT_FORMAT;
        int x2Coor = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-x2\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        int y2Coor = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-y2\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        y2Coor = selectedMap.getWidth() - 1 - y2Coor;
        if (x2Coor < 0 || x2Coor > selectedMap.getLength() - 1 || y2Coor < 0 || yCoor > selectedMap.getWidth() - 1)
            return GameControllerOut.INVALID_COORDINATES;
        Point[] patrol = new Point[2];
        patrol[0] = new Point(xCoor, yCoor);
        patrol[1] = new Point(x2Coor, y2Coor);
        int[] currentLocation = findUnit(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit, selectedMap);
        selectedMap.getTile(currentLocation[0], currentLocation[1]).setPetrolStatus(getCurrentUser(),
                xOriginOFSelectedUnit, yOriginOFSelectedUnit, patrol);
        return GameControllerOut.PATROL_SET_SUCCESSFULLY;
    }

    public GameControllerOut attack(String data) {
        boolean shooting = false;
        Pattern pattern = Pattern.compile("-e");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find())
            shooting = true;
        if (!extractDataxandy(data))
            return GameControllerOut.INVALID_INPUT_FORMAT;
        if (!validateCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return GameControllerOut.INVALID_COORDINATES;
        if (!selectedMap.getTile(yCoor, xCoor).areEnemiesHere(getCurrentUser()))
            return GameControllerOut.NO_ENEMIES_HERE;
        int[] currentLocation = findUnit(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit, selectedMap);
        if (shooting) {
            int distance = Math.abs(xCoor - xOriginOFSelectedUnit) + Math.abs(yCoor - yOriginOFSelectedUnit);
            int damage = selectedMap.getTile(currentLocation[0], currentLocation[1]).getLongRangeDamage(getCurrentUser(),
                    xOriginOFSelectedUnit, yOriginOFSelectedUnit, distance);
            selectedMap.getTile(yCoor, xCoor).damageAllEnemies(
                    getCurrentUser(), damage);
        }

        List<Point> patchPoints = PatchFinding.findPath(selectedMap, new Point(currentLocation[1], currentLocation[0]),
                new Point(xCoor, yCoor), true);
        moveForwardThePath(selectedMap.getTile(currentLocation[0], currentLocation[1]).findYourUnits(getCurrentUser()).get(0),
                patchPoints, currentLocation[1], currentLocation[0]);
        return GameControllerOut.ATTACK_STARTED;
    }

    public void setTargets() {
        for (User empire : Governance.getEmpires()) {
            if (empire.getGovernance().getUnits().size() != 0) {
                for (Unit unit : empire.getGovernance().getUnits()) {
                    int[] currentLocation = findUnit(empire, unit.getxOrigin(), unit.getyOrigin(), selectedMap);
                    setUnitTarget(unit, currentLocation[0], currentLocation[1]);
                }
            }
        }
//        for (int i = 0; i < selectedMap.getWidth(); i++) {
//            for (int j = 0; j < selectedMap.getLength(); j++) {
//                for (java.util.Map.Entry<String, ArrayList<Unit>> arrayListEntry : selectedMap.getTile(i, j).getPlayersUnits().entrySet()) {
//                    if (arrayListEntry.getValue() != null && arrayListEntry.getValue().size() != 0) {
//                        for (Unit unit : arrayListEntry.getValue()) {
//                            setUnitTarget(unit, i, j);
//                        }
//                    }
//                }
//            }
//        }
    }

    private void setUnitTarget(Unit unit, int ip, int jp) {
        int y = ip;
        int x = jp;
        boolean defensive = unit.getState().equals("defensive");
        if (unit.getState().equals("standing"))
            return;
        PriorityQueue queue = new PriorityQueue<>();
        for (int i = 0; i < selectedMap.getWidth(); i++)
            for (int j = 0; j < selectedMap.getLength(); j++) {
                selectedMap.getTile(i, j).setDistance((int) Math.sqrt((i - y) * (i - y) + (j - x) * (j - x)));
                queue.add(selectedMap.getTile(i, j));
            }

        while (!queue.isEmpty()) {
            Tile tile = (Tile) queue.remove();
            if ((!defensive || tile.getDistance() <= unit.getSpeed()) && tile.areEnemiesHere(unit.getMaster())) {
                unit.setxDestination(tile.getX());
                unit.setyDestination(tile.getY());
                System.out.println("target set for unit with x : " + unit.getxOrigin());
                System.out.println(tile.getX() + " , " + tile.getY());
                return;
            }
            if (tile.getDistance() > unit.getSpeed() && defensive)
                return;
        }
    }

    public void mapMotion() {
        for (User empire : Governance.getEmpires()) {
            if (empire.getGovernance().getUnits().size() != 0) {
                for (Unit unit : empire.getGovernance().getUnits()) {
                    PatchFinding.setCurrentForce(empire);
                    if (unit.getxDestination() == -1 || unit.getyDestination() == -1 || unit.isOnPatrol()) {
                        int[] currentLocation = findUnit(empire, unit.getxOrigin(), unit.getyOrigin(), selectedMap);
                    }
                    int[] currentLocation = findUnit(empire, unit.getxOrigin(), unit.getyOrigin(), selectedMap);
                    List<Point> patchPoints = new ArrayList<>();
                    if (!unit.isOnPatrol() && unit.getxDestination() != -1 && unit.getyDestination() != -1)
                        patchPoints = PatchFinding.findPath(selectedMap, new Point(currentLocation[1], currentLocation[0]),
                                new Point(unit.getxDestination(), unit.getyDestination()), true);
                    else if (unit.isOnPatrol()) {
                        Point des;
                        if (unit.getPatrolDestinations()[0].getX() == currentLocation[1] && unit.getPatrolDestinations()[0].getY() == currentLocation[0])
                            des = new Point(unit.getPatrolDestinations()[1].getX(), unit.getPatrolDestinations()[1].getY());
                        else
                            des = new Point(unit.getPatrolDestinations()[0].getX(), unit.getPatrolDestinations()[0].getY());
                        patchPoints = PatchFinding.findPath(selectedMap, new Point(currentLocation[1], currentLocation[0]),
                                des, true);
                    }
                    moveForwardThePath(unit, patchPoints, currentLocation[1], currentLocation[0]);
                }
            }
        }
    }

    public void foodRateEffect() {
        int rate = CurrentUser.getGovernance().getFoodRate();
        switch (rate) {
            case -2:
                this.CurrentUser.getGovernance().changePopularity(-8);
                break;
            case -1:
                this.CurrentUser.getGovernance().changePopularity(-4);
                break;
            case 0:
                break;
            case 1:
                this.CurrentUser.getGovernance().changePopularity(4);
                break;
            case 2:
                this.CurrentUser.getGovernance().changePopularity(8);
                break;
        }
        ArrayList<Resource> resources = CurrentUser.getGovernance().getResources();
        int countOfFood = 0;
        for (Resource resource : resources) {
            String name = resource.getType().getName();
            if (name.equals("apple") || name.equals("meat") || name.equals("bread") || name.equals("cheese"))
                countOfFood++;
        }
        CurrentUser.getGovernance().changePopularity(countOfFood - 1);
    }

    public void taxRateEffect() {
        int rate = CurrentUser.getGovernance().getTaxRate();
        switch (rate) {
            case -3:
                this.CurrentUser.getGovernance().changePopularity(8);
                break;
            case -2:
                this.CurrentUser.getGovernance().changePopularity(5);
                break;
            case -1:
                this.CurrentUser.getGovernance().changePopularity(3);
                break;
            case 0:
                this.CurrentUser.getGovernance().changePopularity(1);
                break;
            case 1:
                this.CurrentUser.getGovernance().changePopularity(-2);
                break;
            case 2:
                this.CurrentUser.getGovernance().changePopularity(-4);
                break;
            case 3:
                this.CurrentUser.getGovernance().changePopularity(-6);
                break;
            case 4:
                this.CurrentUser.getGovernance().changePopularity(-8);
                break;
            case 5:
                this.CurrentUser.getGovernance().changePopularity(-12);
                break;
            case 6:
                this.CurrentUser.getGovernance().changePopularity(-16);
                break;
            case 7:
                this.CurrentUser.getGovernance().changePopularity(-20);
                break;
            case 8:
                this.CurrentUser.getGovernance().changePopularity(-24);
                break;
        }
    }

    public void fearRateEffect() {
        int rate = CurrentUser.getGovernance().getFearRate();
        getCurrentUser().getGovernance().changePopularity(rate * -2);
    }

    public void churchEffect() {
        int count = 0;
        ArrayList<Building> buildings = CurrentUser.getGovernance().getBuildings();
        for (Building building : buildings) {
            if (building.getType().getName().equals("cathedral") || building.getType().getName().equals("church")) {
                count++;
            }
        }
        CurrentUser.getGovernance().changePopularity(count * 2);
    }

    public void produce() {
        for (User empire : Governance.getEmpires()) {
            //first wave:
            for (Building building : empire.getGovernance().getBuildings()) {
                if (building.getType().getWave() == 1)
                    ((ResourceMaker) building).produceAfterEachTurn();
            }
            //second wave:
            for (Building building : empire.getGovernance().getBuildings()) {
                if (building.getType().getWave() == 2)
                    ((ResourceMaker) building).produceAfterEachTurn();
            }
            //third wave:
            for (Building building : empire.getGovernance().getBuildings()) {
                if (building.getType().getWave() == 3)
                    ((ResourceMaker) building).produceAfterEachTurn();
            }
        }
    }

    public GameControllerOut disbandUnit() {
        if (xOriginOFSelectedUnit == -1)
            return GameControllerOut.SELECT_A_UNIT_FIRST;
        return findAndSetUnitDestination();
    }

    private GameControllerOut findAndSetUnitDestination() {
        int[] currentLocation = findUnit(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit, selectedMap);
        Unit selectedUnit = selectedMap.getTile(currentLocation[0], currentLocation[1]).findUnitByOrigin(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit);
        for (int i = 0; i < selectedMap.getWidth(); i++)
            for (int j = 0; j < selectedMap.getLength(); j++)
                for (Building building : selectedMap.getTile(i, j).getBuildings()) {
                    if (building.getOwner().getUsername().equals(getCurrentUser().getUsername()))
                        if ((selectedUnit.getTroops().get(0).getType().equals(UnitEnum.ENGINEER) && building.getType().equals(BuildingEnum.ENGINEERS_GUILD)) ||
                                (selectedUnit.getTroops().get(0).getType().isArab() && building.getType().equals(BuildingEnum.MERCENARY_POST) &&
                                        !selectedUnit.getTroops().get(0).getType().equals(UnitEnum.ENGINEER)) ||
                                (!selectedUnit.getTroops().get(0).getType().isArab() && building.getType().equals(BuildingEnum.BARRACKS) &&
                                        !selectedUnit.getTroops().get(0).getType().equals(UnitEnum.ENGINEER))
                        ) {
                            selectedMap.getTile(currentLocation[0], currentLocation[1]).findYourUnits(getCurrentUser()).get(0)
                                    .setxDestination(j);
                            selectedMap.getTile(currentLocation[0], currentLocation[1]).findYourUnits(getCurrentUser()).get(0)
                                    .setyDestination(i);
                            return GameControllerOut.RETREATING;
                        }
                }
        return GameControllerOut.NO_PLACE_TO_GO;
    }

    public GameControllerOut stopPetrol() {
        int[] currentLocation = findUnit(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit, selectedMap);
        Unit selectedUnit = selectedMap.getTile(currentLocation[0], currentLocation[1]).findUnitByOrigin(getCurrentUser(), xOriginOFSelectedUnit, yOriginOFSelectedUnit);
        selectedUnit.setOnPatrol(false);
        selectedUnit.setxDestination(-1);
        selectedUnit.setyDestination(-1);
        return GameControllerOut.SUCCESSFULLY_STOPPED;
    }

    public void setxOFSelectedBuilding(int xOFSelectedBuilding) {
        this.xOFSelectedBuilding = xOFSelectedBuilding;
    }

    public void setyOFSelectedBuilding(int yOFSelectedBuilding) {
        this.yOFSelectedBuilding = yOFSelectedBuilding;
    }


    private boolean smellsDanger(ArrayList<Unit> value) {
        for (Unit unit : value) {
            System.out.println(unit.getPresentTypes());
            if (unit.getPresentTypes().contains("fire thrower"))
                return true;
        }
        return false;
    }
}