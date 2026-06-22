package controller;

import controller.modelFunctions.BuildingFuncs;
import model.*;
import model.buildings.*;
import model.units.Troop;
import model.units.Unit;
import model.units.UnitEnum;
import view.enums.ProfisterControllerOut;
import view.enums.TreeTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class MapMenuController {
    private final Map map1 = new Map(200, 200);

    public Map selectedMap;
    int xTexture = 0;
    int yTexture = 0;
    int x2Texture = 0;
    int y2Texture = 0;
    String typeTexture;

    int xShowingMap = -1;
    int yShowingMap = -1;

    public Map getSelectedMap() {
        return selectedMap;
    }

    public String setUpACustom(int widthAndLength) {
        int[] ranges = setRange(widthAndLength / 2 - 1, widthAndLength / 2 - 1, widthAndLength, widthAndLength);
        Map randomMap = makeRandomMap(widthAndLength, widthAndLength);
        this.selectedMap = randomMap;
        return printMap(randomMap, ranges);
    }

    public String setUpATemplate() {
        setUpDefaultMaps();
        this.selectedMap = map1;
        String ans = "I give you a 200*200 map. You can change the map texture any time with this command:\n" +
                "settexture -x [x] -y [y] -t [type])\n";
        int[] range = setRange(99, 99, 200, 200);
        ans += printMap(map1, range) + "\n";
        return ans;
    }

    private Map makeRandomMap(int length, int width) {
        Map makingOne = new Map(width, length);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < 35; j++)
                makingOne.getTile(i, j).setTexture(TileTexture.SEA);
        for (int i = 0; i < width - 40; i++) {
            makingOne.getTile(i, i + 35).setTexture(TileTexture.FORD);
            makingOne.getTile(i, i + 36).setTexture(TileTexture.SMALL_POND);
            makingOne.getTile(i, i + 37).setTexture(TileTexture.SMALL_POND);
            makingOne.getTile(i, i + 38).setTexture(TileTexture.FORD);
        }
        for (int i = width / 2; i < width / 2 + 2; i++)
            for (int j = 35; j < length; j++)
                makingOne.getTile(i, j).setTexture(TileTexture.SCRUB);
        for (int i = 0; i < 10; i++) {
            for (int j = length - 1; j > length - 15; j--)
                makingOne.getTile(i, j).setTexture(TileTexture.IRON);
            for (int j = 0; j < 5; j++)
                makingOne.getTile(i, length - 20 - j).setTexture(TileTexture.THICK_SCRUB);
        }
        for (int i = width - 1; i > width - 20; i--)
            for (int j = 35; j < 45; j++)
                makingOne.getTile(i, j).setTexture(TileTexture.OIL);
        return makingOne;
    }

    //(0,0) is top left.
    private void setUpDefaultMaps() {
        //Designing map template number 1:
        boolean[][] mark1 = new boolean[100][100];
        int pickLand;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                map1.getTile(i, j).setTexture(TileTexture.SEA);
                mark1[i][j] = true;
            }
            for (int j = 85; j < 100; j++) {
                map1.getTile(i, j).setTexture(TileTexture.SEA);
                mark1[i][j] = true;
            }
        }
        for (int i = 85; i < 100; i++) {
            for (int j = 0; j < 15; j++) {
                map1.getTile(i, j).setTexture(TileTexture.SMALL_POND);
                mark1[i][j] = true;
            }
            for (int j = 85; j < 100; j++) {
                map1.getTile(i, j).setTexture(TileTexture.FORD);
                mark1[i][j] = true;
            }
        }
        for (int i = 15; i < 35; i++) {
            for (int j = 20; j < 24; j++) {
                map1.getTile(i, j).setTexture(TileTexture.IRON);
                mark1[i][j] = true;
            }
            if (i > 31)
                for (int j = 24; j < 30; j++) {
                    map1.getTile(i, j).setTexture(TileTexture.OIL);
                    mark1[i][j] = true;
                }
        }
        for (int i = 0; i < 100; i++)
            for (int j = 0; j < 100; j++)
                if (!mark1[i][j]) {
                    pickLand = (int) (3 * Math.random());
                    if (pickLand == 2) map1.getTile(i, j).setTexture(TileTexture.SCRUB);
                    if (pickLand == 1) map1.getTile(i, j).setTexture(TileTexture.THICK_SCRUB);
                    if (pickLand == 0) map1.getTile(i, j).setTexture(TileTexture.EARTH);
                }
    }

    public int[] setRange(int x, int y, int length, int width) {
        //showing map range: (5*6) * (5*3)
        //we first see if the coordinates above our point exist:
        //int[0] = min y; int[1] = max y; int[2] = min x; int[3] = max x;
        int[] result = new int[4];
        result[0] = Math.max(y - 7, 0);
        result[1] = Math.min(y + 7, width - 1);
        //assuming: (*14) x (*15)
        result[2] = Math.max(x - 14, 0);
        result[3] = Math.min(x + 15, length - 1);
        return result;
    }

    public static String printMap(Map map, int[] ranges) {
        String ans = new String();
        char tileOccupation;
        int xcounterForBreak = 0;
        int ycounterForBreak = 0;
        for (int i = ranges[0]; i <= ranges[1]; i++) {
            if (ycounterForBreak % 3 == 0) {
                for (int k = 0; k < (ranges[3] - ranges[2] + 1) * 7 / 6 + 2; k++) ans += "-";
                ans += "\n";
            }
            ycounterForBreak++;
            for (int j = ranges[2]; j <= ranges[3]; j++) {
                tileOccupation = map.getTile(i, j).getTileOccupation();
                if (xcounterForBreak % 6 == 0) ans += "\033[49m|";
                xcounterForBreak++;
                switch (map.getTile(i, j).getTexture()) {
                    case OIL:
                        ans += "\033[38;5;249;48;5;16m" + tileOccupation;
                        break;
                    case SEA:
                        ans += "\033[38;5;249;48;5;19m" + tileOccupation;
                        break;
                    case EARTH:
                        ans += "\033[38;5;249;48;5;52m" + tileOccupation;
                        break;
                    case FORD:
                        ans += "\033[38;5;251;48;5;38m" + tileOccupation;
                        break;
                    case IRON:
                        ans += "\033[38;5;249;48;5;166m" + tileOccupation;
                        break;
                    case SCRUB:
                        ans += "\033[38;5;247;48;5;41m" + tileOccupation;
                        break;
                    case THICK_SCRUB:
                        ans += "\033[38;5;249;48;5;29m" + tileOccupation;
                        break;
                    case SMALL_POND:
                        ans += "\033[1;38;5;249;48;5;123m" + tileOccupation;
                        break;
                    case BIG_POND:
                        ans += "\033[38;5;249;48;5;57m" + tileOccupation;
                        break;
                    case RIVER:
                        ans += "\033[38;5;249;48;5;21m" + tileOccupation;
                        break;
                    case SAND:
                        ans += "\033[38;5;249;48;5;230m" + tileOccupation;
                        break;
                    case LAWN:
                        ans += "\033[38;5;249;48;5;34m" + tileOccupation;
                        break;
                    case ROCK:
                        ans += "\033[38;5;249;48;5;8m" + tileOccupation;
                        break;
                }
                if (j == ranges[3]) {
                    ans += "\033[49m|" + "\n";
                    xcounterForBreak = 0;
                }
            }
        }
        //Adding a guidance table:
        ans += "-------------Table Info-------------" + "\n";
        ans += "\033[38;5;249;48;5;16m                OIL                \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;19m                SEA                \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;52m                EARTH              \033[49m" + "\n";
        ans += "\033[38;5;251;48;5;38m                FORD               \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;166m                IRON               \033[49m" + "\n";
        ans += "\033[38;5;247;48;5;41m                SCRUB              \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;29m                THICK SCRUB        \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;34m                LAWN               \033[49m" + "\n";
        ans += "\033[1;38;5;249;48;5;123m                SMALL POND         \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;57m                BIG POND           \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;21m                RIVER              \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;230m                SAND               \033[49m" + "\n";
        ans += "\033[38;5;249;48;5;8m                ROCK               \033[49m" + "\n";
        return ans;
    }

    public String setTextureForTheWholeMap(Map map, String data) throws IOException {
        if (!extractDataForTexture(data))
            return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        TileTexture tileType = convertStringTextureToEnum(typeTexture);
        if (tileType == null)
            return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        y2Texture = map.getWidth() - 1 - y2Texture;
        boolean doWeHaveX2 = x2Texture == -1;
        if (doWeHaveX2) {
            x2Texture = 0;
            y2Texture = 0;
        }
        if (!validateTextureCoordinates(map.getLength(), map.getWidth())) {
            return "Mission failed: invalid coordinates!";
        }
        if (doWeHaveX2) {
            if (map.getTile(yTexture, xTexture).getBuildings().size() != 0)
                return "Mission failed: You can't change the" +
                        "texture while there is a building on it!";
            map.getTile(yTexture, xTexture).setTexture(tileType);
            if (tileType.equals(TileTexture.SMALL_POND))
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        if (yTexture + i > -1 && yTexture + i < selectedMap.getWidth() && xTexture + j > -1 && xTexture + j < selectedMap.getLength())
                            map.getTile(yTexture + i, xTexture + j).setTexture(tileType);
            if (tileType.equals(TileTexture.BIG_POND))
                for (int i = -2; i < 3; i++)
                    for (int j = -2; j < 3; j++)
                        if (yTexture + i > -1 && yTexture + i < selectedMap.getWidth() && xTexture + j > -1 && xTexture + j < selectedMap.getLength())
                            map.getTile(yTexture + i, xTexture + j).setTexture(tileType);
        } else {
            if (tileType.equals(TileTexture.SMALL_POND) || tileType.equals(TileTexture.BIG_POND))
                return "Ponds hava a fixed size. You can't use a custom size";
            for (int i = yTexture; i < y2Texture; i++)
                for (int j = xTexture; j < x2Texture; j++) {
                    if (map.getTile(i, j).getBuildings().size() != 0)
                        return "Mission failed: You can't change a tile's" +
                                "texture while there is a building on it!";
                }
            for (int i = yTexture; i < y2Texture; i++)
                for (int j = xTexture; j < x2Texture; j++) {
                    map.getTile(i, j).setTexture(tileType);
                }
        }
        xTexture = 0;
        x2Texture = 0;
        y2Texture = 0;
        yTexture = 0;
        return "Texture set successfully!";
    }

    public boolean validateTextureCoordinates(int mapLength, int mapWidth) {
        x2Texture = x2Texture == -1 ? 0 : x2Texture;
        y2Texture = y2Texture == -1 ? 0 : y2Texture;
        return xTexture >= 0 && xTexture <= mapLength - 1 && yTexture >= 0 && yTexture <= mapWidth - 1 &&
                x2Texture >= 0 && x2Texture <= mapLength - 1 && y2Texture >= 0 && y2Texture <= mapWidth - 1;
    }

    public TileTexture convertStringTextureToEnum(String typeTexture) {
        if (typeTexture == null || typeTexture.length() == 0 || typeTexture.trim().length() == 0)
            return null;
        EnumSet<TileTexture> tileTextures = EnumSet.allOf(TileTexture.class);
        for (TileTexture tileTexture : tileTextures) {
            if (tileTexture.getName().equals(typeTexture.trim()))
                return tileTexture;
        }
        return null;
    }

    public boolean extractDataForTexture(String data) throws IOException {
        //todo: handle errors. everytime we use that there should be a type somewhere...
        if (CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))").length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))").length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-t\\s+(?<wantedPart>([^-]+))(?<!\\s))").length() == 0)
            return false;
        if (CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim().length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim().length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-t\\s+(?<wantedPart>([^-]+))(?<!\\s))").trim().length() == 0)
            return false;
        xTexture = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        yTexture = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        yTexture = selectedMap.getWidth() - 1 - yTexture;
        typeTexture = CommonController.dataExtractor(data, "((?<!\\S)-t\\s+(?<wantedPart>([^-]+))(?<!\\s))").trim();
        String x2T = CommonController.dataExtractor(data, "((?<!\\S)-x2\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        String y2T = CommonController.dataExtractor(data, "((?<!\\S)-y2\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        if ((x2T.length() == 0 && y2T.length() != 0) || (y2T.length() == 0 && x2T.length() != 0))
            return false;
        if (x2T.length() != 0) x2Texture = Integer.parseInt(x2T.trim());
        else x2Texture = -1;
        if (y2T.length() != 0) y2Texture = Integer.parseInt(y2T.trim());
        else y2Texture = -1;
        return true;
    }

    public boolean extractDataxandy(String data) throws IOException {
        String x = CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        String y = CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))");
        if (x.length() == 0 || y.length() == 0) return false;
        if (x.trim().length() == 0 || y.trim().length() == 0) return false;
        xTexture = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-x\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        yTexture = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-y\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        yTexture = selectedMap.getWidth() - 1 - yTexture;
        return true;
    }

    public String showMap(String data) throws IOException {
        String ans = new String();
        if (!extractDataxandy(data)) return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        int[] range = setRange(xTexture, yTexture, selectedMap.getLength(), selectedMap.getWidth());
        xShowingMap = xTexture;
        yShowingMap = yTexture;
        return printMap(selectedMap, range);
    }

    public String moveMap(String data) {
        String upStr = CommonController.dataExtractor(data, "((?<!\\S)up\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim();
        String leftStr = CommonController.dataExtractor(data, "((?<!\\S)left\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim();
        String downStr = CommonController.dataExtractor(data, "((?<!\\S)down\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim();
        String rightStr = CommonController.dataExtractor(data, "((?<!\\S)right\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim();
        boolean doWeHaveRight = rightStr.length() > 0;
        boolean doWeHaveUp = upStr.length() > 0;
        int up = upStr.length() > 0 ? Integer.parseInt(upStr) : 1;
        int left = leftStr.length() > 0 ? Integer.parseInt(leftStr) : 1;
        int down = downStr.length() > 0 ? Integer.parseInt(downStr) : 1;
        int right = rightStr.length() > 0 ? Integer.parseInt(rightStr) : 1;
        if (doWeHaveRight) xTexture = xShowingMap + right;
        else xTexture = xShowingMap - left;
        if (doWeHaveUp) yTexture = yShowingMap - up;
        else yTexture = yShowingMap + down;
        if (!validateTextureCoordinates(this.selectedMap.getLength(), this.getSelectedMap().getWidth()))
            return ProfisterControllerOut.INVALID_NEW_COORDINATES.getContent();
        int[] ranges = setRange(xTexture, yTexture, this.selectedMap.getLength(), this.selectedMap.getWidth());
        return printMap(this.selectedMap, ranges);
    }

    public String clearTile(String data) throws IOException {
        if (!extractDataxandy(data)) return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        if (!validateTextureCoordinates(this.selectedMap.getLength(), this.selectedMap.getWidth()))
            return "failed: invalid coordinates";
        this.selectedMap.getTile(yTexture, xTexture).clear();
        return "Tile cleared successfully!";
    }

    public String dropTree(String data) throws IOException {
        if (!extractDataForTexture(data)) return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        if (!validateTextureCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return ProfisterControllerOut.FAILED.getContent();
        TreeTypes type = specifyTreeType(typeTexture);
        if (type == null)
            return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        if (!selectedMap.getTile(yTexture, xTexture).getTexture().isWalkability())
            return ProfisterControllerOut.NOT_A_VALID_PLACE_FOR_TREES.getContent();
        selectedMap.getTile(yTexture, xTexture).getTrees().add(new Tree(type));
        return "Tree added successfully!";
    }

    private TreeTypes specifyTreeType(String typeTexture) {
        if (typeTexture == null || typeTexture.length() == 0 || typeTexture.trim().length() == 0)
            return null;
        EnumSet<TreeTypes> treeTypes = EnumSet.allOf(TreeTypes.class);
        for (TreeTypes treeType : treeTypes) {
            if (treeType.getName().equals(typeTexture))
                return treeType;
        }
        return null;
    }

    public String dropRock(String data) throws IOException {
        if (!extractDataxandy(data)) return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        String x = CommonController.dataExtractor(data, "((?<!\\S)-d\\s+(?<wantedPart>[n,e,w,s,r])(?<!\\s))");
        if (x.length() == 0) return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        typeTexture = x.trim();
        if (!validateTextureCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return ProfisterControllerOut.FAILED.getContent();

        if (typeTexture.equals("r")) {
            int random = (int) (4 * Math.random());
            if (random == 0)
                typeTexture = "n";
            if (random == 1)
                typeTexture = "e";
            if (random == 2)
                typeTexture = "w";
            if (random == 3)
                typeTexture = "s";
        }
        selectedMap.getTile(yTexture, xTexture).setRockDirection(typeTexture);
        selectedMap.getTile(yTexture, xTexture).setTexture(TileTexture.ROCK);
        return "Rock added successfully!";
    }


    public String showDetail(String data) throws IOException {
        String ans = "|  mAp dEtailS  |\n";
        if (!extractDataxandy(data) || !validateTextureCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return ProfisterControllerOut.INVALID_INPUT_FORMAT.getContent();
        ans += "The texture is: " + selectedMap.getTile(yTexture, xTexture).getTexture().toString();
        if (xShowingMap == -1) xShowingMap = xTexture;
        if (yShowingMap == -1) yShowingMap = yTexture;
        //getting trees:
        if (selectedMap.getTile(yShowingMap, xShowingMap).existTree())
            ans += extractTrees(selectedMap.getTile(yShowingMap, xShowingMap).getTrees());
        //getting buildings:
        if (selectedMap.getTile(yShowingMap, xShowingMap).getBuildings().size() != 0)
            ans += selectedMap.getTile(yShowingMap, xShowingMap).showBuildings();
        //getting troops:
        if (selectedMap.getTile(yTexture, xShowingMap).getPlayersUnits().size() != 0)
            ans += extractUnits(selectedMap.getTile(yShowingMap, xShowingMap).getPlayersUnits());
        xTexture = 0;
        yTexture = 0;
        return ans;
    }

    private String extractUnits(HashMap<String, ArrayList<Unit>> playersUnits) {
        String ans = "\n";
        for (java.util.Map.Entry<String, ArrayList<Unit>> entry : playersUnits.entrySet()) {
            ans += "|__Owner: " + entry.getKey() + " troops:\n" + troopCount(entry.getValue());
        }
        return ans;
    }

    private String troopCount(ArrayList<Unit> units) {
        String ans = "";
        HashMap<String, Integer> troopTypes = new HashMap<>();
        EnumSet<UnitEnum> unitEnums = EnumSet.allOf(UnitEnum.class);
        for (UnitEnum unitEnum : unitEnums) {
            troopTypes.put(unitEnum.getName(),0);
        }
        for (Unit unit : units) {
            for (Troop troop : unit.getTroops()) {
                String type = troop.getType().getName();
                troopTypes.put(type,troopTypes.get(type) + 1);
            }
        }
//        for (Unit unit : units) {
//            for (java.util.Map.Entry<UnitEnum, ArrayList<Troop>> enumArrayListEntry : unit.getTroops().entrySet()) {
//                int temp = troopTypes.get(enumArrayListEntry.getKey().getName());
//                troopTypes.put(enumArrayListEntry.getKey().getName(), enumArrayListEntry.getValue().size() + temp);
//            }
//        }
        for (java.util.Map.Entry<String, Integer> stringIntegerEntry : troopTypes.entrySet()) {
            if (stringIntegerEntry.getValue() != 0) {
                if(!ans.equals("")) ans = "\n";
                ans += "Type: " + stringIntegerEntry.getKey() + " Count: " + stringIntegerEntry.getValue();
            }
        }
        return ans;
    }

    private String extractTrees(ArrayList<Tree> trees) {
        String ans = "These are the trees and their number:\n";
        HashMap<String, Integer> treeTypes = new HashMap<>();
        for (Tree tree : trees) {
            if (treeTypes.get(tree.getType().getName()) == null)
                treeTypes.put(tree.getType().getName(), 0);
            else {
                int temp = treeTypes.get(tree.getType().getName()) + 1;
                treeTypes.put(tree.getType().getName(), temp);
            }
        }
        for (java.util.Map.Entry<String, Integer> entry : treeTypes.entrySet()) {
            ans += "\n" + entry.getKey() + " -> " + entry.getValue();
        }
        return ans;
    }

    public ProfisterControllerOut dropBuilding(String data, User currentPlayer) throws IOException {
        if (!extractDataForTexture(data)) return ProfisterControllerOut.INVALID_INPUT_FORMAT;
        if (!validateTextureCoordinates(selectedMap.getLength(), selectedMap.getWidth()))
            return ProfisterControllerOut.INVALID_INPUT_FORMAT;
        BuildingEnum type = buildingTypeSpecifier(typeTexture);
        if (type == null) return ProfisterControllerOut.INVALID_INPUT_FORMAT;
        if (!checkLocation(selectedMap, yTexture, xTexture, type)) return ProfisterControllerOut.NOT_A_VALID_PLACE;
        if (!checkFinance(currentPlayer, type)) return ProfisterControllerOut.NOT_ENOUGH_RESOURCES;
        if (type.equals(BuildingEnum.SMALL_STONE_GATEHOUSE) || type.equals(BuildingEnum.BIG_STONE_GATEHOUSE))
            return ProfisterControllerOut.ONLY_ONE_GATEHOUSE;
        if(type.equals(BuildingEnum.STAIR) && !checkThePlaceForStairs(currentPlayer))
            return ProfisterControllerOut.INVALID_STAIR_LOCATION;
        Building addingBuilding = null;
        boolean enoughPlayers = currentPlayer.getGovernance().getUnemployedPopulation() >= type.getWorker();
        if (enoughPlayers) currentPlayer.getGovernance().changeUnemployedPopulation(-1 * type.getWorker());
        switch (type.getType()) {
            case GATE:
                addingBuilding = new Gate(type, currentPlayer, 0, enoughPlayers);
                break;
            case TRAP:
                addingBuilding = new Trap(type, currentPlayer, 0, enoughPlayers);
                this.selectedMap.getTile(yTexture, xTexture).setHasTrap(true);
                if (type.equals(BuildingEnum.KILLING_PIT)) {
                    currentPlayer.getGovernance().changeFearRate(1);
                    currentPlayer.getGovernance().changePopularity(-2);
                }
                break;
            case TOWER:
                addingBuilding = new Tower(type, currentPlayer, 0, enoughPlayers);
                break;
            case ForceRecruitment:
                addingBuilding = new ForceRecruitment(type, currentPlayer, 0, yTexture, xTexture, enoughPlayers);
                break;
            case STORAGE:
                addingBuilding = new Storage(type, currentPlayer, 0, enoughPlayers);
                break;
            case BUILDING:
                addingBuilding = new Building(type, currentPlayer, 0, enoughPlayers);
                break;
            case RESOURCE_MAKER:
                addingBuilding = new ResourceMaker(type, currentPlayer, 0, enoughPlayers);
                break;
        }
        if (isItNearGateHouse()) {
            currentPlayer.getGovernance().changeFearRate(-1);
            currentPlayer.getGovernance().changePopularity(1);
        }
        this.selectedMap.getTile(yTexture, xTexture).getBuildings().add(addingBuilding);
        currentPlayer.getGovernance().getBuildings().add(addingBuilding);
        primaryPerformance(currentPlayer,addingBuilding);
        if (!enoughPlayers) return ProfisterControllerOut.CREATED_EMPTY_BUILDING;
        return ProfisterControllerOut.SUCCESSFULLY_ADDED_BUILDING;
    }

    private boolean checkThePlaceForStairs(User currentPlayer) {
        for(int i = yTexture - 1; i < yTexture + 2; yTexture++)
            for(int j = xTexture - 1; j < xTexture + 2; j++) {
                if(selectedMap.getTile(i,j).doWeHaveWallsOrGates(currentPlayer))
                    return true;
            }
        return false;
    }

    private void primaryPerformance(User currentPlayer, Building addingBuilding) {
        BuildingFuncs buildingFuncs = new BuildingFuncs();
        if(addingBuilding.getType().equals(BuildingEnum.CHURCH))
            buildingFuncs.church(currentPlayer);
        if(addingBuilding.getType().equals(BuildingEnum.CATHEDRAL))
            buildingFuncs.cathedral(currentPlayer);
        if(addingBuilding.getType().equals(BuildingEnum.HOVEL))
            buildingFuncs.hovel(currentPlayer);
        if(addingBuilding.getType().equals(BuildingEnum.KILLING_PIT))
            buildingFuncs.killingpit(currentPlayer);
    }

    private boolean isItNearGateHouse() {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                if (yTexture + i >= 0 && yTexture + i < selectedMap.getWidth() && xTexture + j >= 0 && xTexture + j < selectedMap.getLength())
                    if (doWeHaveGateInThisTile(selectedMap.getTile(yTexture + i, xTexture + j).getBuildings()))
                        return true;
            }
        return false;
    }

    public boolean doWeHaveGateInThisTile(ArrayList<Building> buildings) {
        for (Building building : buildings) {
            if (building.getType().equals(BuildingEnum.BIG_STONE_GATEHOUSE) ||
                    building.getType().equals(BuildingEnum.SMALL_STONE_GATEHOUSE))
                return true;
        }
        return false;
    }

    public boolean checkLocation(Map selectedMap, int yTexture, int xTexture, BuildingEnum type) {
        boolean[] isException = new boolean[3];
        if ((isException[0] = type.equals(BuildingEnum.IRON_MINE)) && !selectedMap.getTile(yTexture, xTexture).getTexture().equals(TileTexture.IRON))
            return false;
        else if ((isException[1] = type.equals(BuildingEnum.PITCH_DITCH)) && !selectedMap.getTile(yTexture, xTexture).getTexture().equals(TileTexture.OIL))
            return false;
        else if ((isException[2] = type.equals(BuildingEnum.QUARRY)) && !selectedMap.getTile(yTexture, xTexture).getTexture().equals(TileTexture.ROCK))
            return false;
        else
            return isException[0] || isException[1] || isException[2] || selectedMap.getTile(yTexture, xTexture).getTexture().isConstructiblity();
    }

    private boolean checkFinance(User currentPlayer, BuildingEnum buildingType) {
        if (buildingType == null || buildingType.getResource() == null ||
                buildingType.getResource().getType() == null || buildingType.getResource().getType().equals(ResourceEnum.NULL))
            return true;
        if (currentPlayer.getGovernance().getResourceAmount((buildingType.getResource().getType())) < buildingType.getResource().getAmount())
            return false;
        if (currentPlayer.getGovernance().getGold() < buildingType.getGoldCost())
            return false;
        currentPlayer.getGovernance().changeResourceAmount(buildingType.getResource().getType(), -1 * buildingType.getResource().getAmount());
        currentPlayer.getGovernance().changeGold(-1 * buildingType.getGoldCost());
        currentPlayer.getGovernance().changeUnemployedPopulation(-1 * buildingType.getWorker());
        return true;
    }

    public BuildingEnum buildingTypeSpecifier(String type) {
        EnumSet<BuildingEnum> buildingEnums = EnumSet.allOf(BuildingEnum.class);
        for (BuildingEnum buildingEnum : buildingEnums) {
            if (buildingEnum.getName().equals(type))
                return buildingEnum;
        }
        return null;
    }

    public int getxTexture() {
        return xTexture;
    }

    public int getyTexture() {
        return yTexture;
    }

}