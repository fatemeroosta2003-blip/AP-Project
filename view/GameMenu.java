package view;

import java.io.IOException;

import controller.gameMenuControllers.GameController;
import model.Governance;
import model.Map;
import model.ResourceEnum;
import model.User;
import view.enums.Commands;
import view.enums.GameControllerOut;

import java.util.regex.Matcher;

public class GameMenu {

    Map map;
    private User currentUser;
    private GameController gameController;

    public GameMenu(User host) {
        this.currentUser = host;
    }

    //todo: reset every governance in the end.
    public void run() throws IOException {
        map = (new MapMenu(null, this.currentUser, this.gameController)).setUpMap();
        gameController = new GameController(this.currentUser, this.map);
        if (!createGateHouse(gameController)) return;
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            Matcher matcher;
            if (command.matches("map menu")) {
                System.out.println("You are in the map menu");
                MapMenu mapMenu = new MapMenu(this.map, this.currentUser, this.gameController);
                mapMenu.run();
                this.map = mapMenu.map;
                gameController.setSelectedMap(this.map);
            } else if (command.matches("show current menu")) System.out.println("game menu");
            else if (command.matches("shop menu")) {
                if (gameController.getSelectedBuilding() != null && gameController.getSelectedBuilding().getType().getName().equals("market")) {
                    System.out.println("You are in the shop menu");
                    new ShopMenu(currentUser).run();
                } else System.out.println("You should go to the market first!");
            } else if (command.matches("trade menu")) {
                System.out.println("You are in the trade menu");
                new TradeMenu(currentUser).run();
            } else if ((matcher = Commands.getMatcher(command, Commands.SHOW_POP_FACTORS)) != null) {
                System.out.println(gameController.showPopularityFactors());
            } else if ((matcher = Commands.getMatcher(command, Commands.SHOW_POPULARITY)) != null) {
                System.out.println(gameController.showPopularity());
            } else if ((matcher = Commands.getMatcher(command, Commands.SET_FOOD_RATE)) != null) {
                System.out.println(gameController.setFoodRate(matcher.group("data")).getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.SET_FEAR_RATE)) != null) {
                System.out.println(gameController.setFearRate(matcher.group("data")).getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.SHOW_FOOD_LIST)) != null) {
                System.out.print(gameController.showFoodList());
            } else if ((matcher = Commands.getMatcher(command, Commands.SET_TAX_RATE)) != null) {
                if (gameController.getSelectedBuilding() != null &&
                        (gameController.getSelectedBuilding().getType().getName().equals("small_stone_gatehouse") ||
                                gameController.getSelectedBuilding().getType().getName().equals("big_stone_gatehouse"))) {
                    System.out.println(gameController.setTaxRate(matcher.group("data")).getContent());
                } else System.out.println("You should go to the gatehouse first!");
            } else if ((matcher = Commands.getMatcher(command, Commands.SHOW_FOOD_RATE)) != null) {
                System.out.println(gameController.showFoodRate());
            } else if ((matcher = Commands.getMatcher(command, Commands.SHOW_TAX_RATE)) != null) {
                System.out.println(gameController.showTaxRate());
            } else if ((matcher = Commands.getMatcher(command, Commands.SELECT_BUILDING)) != null) {
                System.out.println(gameController.selectBuilding(matcher.group("data")));
            } else if ((matcher = Commands.getMatcher(command, Commands.DROP_BUILDING)) != null) {
                System.out.println(GameControllerOut.DROP.getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.CREATE_UNIT)) != null) {
                System.out.println(gameController.createUnit(matcher.group("data"), false).getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.REPAIR)) != null) {
                System.out.println(gameController.repair().getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.SELECT_UNIT)) != null) {
                System.out.println(gameController.selectUnit(matcher.group("data")).getContent());
            }  else if ((matcher = Commands.getMatcher(command, Commands.STOP_PETROL)) != null) {
                System.out.println(gameController.stopPetrol().getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.PATROL_UNIT)) != null) {
                System.out.println(gameController.patrolUnit(matcher.group("data")).getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.ATTACK)) != null) {
                System.out.println(gameController.attack(matcher.group("data")));
            }else if ((matcher = Commands.getMatcher(command, Commands.DISBAND_UNIT)) != null) {
                System.out.println(gameController.disbandUnit().getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.SET_STATE)) != null) {
                System.out.println(gameController.setState(matcher.group("data")).getContent());
            } else if ((matcher = Commands.getMatcher(command, Commands.MOVE_UNIT)) != null) {
                System.out.println(gameController.moveUnit(matcher.group("data")).getContent());
            } else if (command.matches("trade menu")) {
                TradeMenu tradeMenu = new TradeMenu(currentUser);
                tradeMenu.run();
            } else if ((matcher = Commands.getMatcher(command, Commands.NEXT_TURN)) != null) {
                gameController.produce();
                gameController.setTargets();
                gameController.mapMotion();
                gameController.foodRateEffect();
                gameController.taxRateEffect();
                gameController.fearRateEffect();
                gameController.churchEffect();
                //set target, fight , move , update resources , govern functions lie here
                //soldier's damage should be set according to the fear rate at each turn
                this.currentUser = Governance.getNextPlayer(this.currentUser);
                gameController.prepareForNextPlayer(this.currentUser);
                System.out.println(GameControllerOut.NEXT_TURN.getContent() + this.currentUser.getUsername());
                if (!this.currentUser.getGovernance().haveGateHouse())
                    if (!createGateHouse(gameController)) return;
            }
            else if (command.matches("sword")){
                System.out.println(currentUser.getGovernance().getResourceAmount(ResourceEnum.SPEAR));
                System.out.println(currentUser.getGovernance().getResourceAmount(ResourceEnum.PIKE));
            }
            else
                System.out.println("invalid command");
        }
    }

    private boolean createGateHouse(GameController gameController) {
        System.out.println("Before anything, you should choose a gatehouse. Tell me, which gatehouse would you like to" +
                " build?\nEnter s for small_stone_gatehouse (it's free!) or b for big_stone_gatehouse (for 20 stones)");
        String typeStr = ScanMatch.getScanner().nextLine();
        int type = 0;
        boolean failed = false;
        if (typeStr != null && typeStr.trim().equals("s"))
            type = 1;
        else if (typeStr != null && typeStr.trim().equals("b"))
            type = 2;
        else {
            System.out.println("The game shall end here and now duo to gatehouse settlement issues. You will return to the main menu");
            return false;
        }
        System.out.println("Also, please choose your gate's directions: \"ns\" for north-south; and \"ew\" for east-west");
        String directionStr = ScanMatch.getScanner().nextLine();
        int direction;
        if (directionStr != null && directionStr.trim().equals("ns"))
            direction = 1;
        else if (directionStr != null && directionStr.trim().equals("ew"))
            direction = 2;
        else {
            System.out.println("The game shall end here and now duo to gatehouse settlement issues. You will return to the main menu");
            return false;
        }
        System.out.println("Perfect! now, where would you like to put it?\nYour answer should be in form: -x [xPosition] -y [yPosition]");
        String coordinateInput = ScanMatch.getScanner().nextLine();
        GameControllerOut out = gameController.buildGateHouse(type, direction, coordinateInput);
        this.map = gameController.getSelectedMap();
        System.out.println(out.getContent());
        if (out.equals(GameControllerOut.SUCCESSFULLY_ADDED_GATEHOUSE))
            return true;
        else if (out.equals(GameControllerOut.NOT_A_SPOT)) {
            System.out.println("You have one more chance. Enter new coordinates (-x [xPosition] -y [yPosition])");
            coordinateInput = ScanMatch.getScanner().nextLine();
            out = gameController.buildGateHouse(type, direction, coordinateInput);
            this.map = gameController.getSelectedMap();
            System.out.println(out.getContent());
            if (out.equals(GameControllerOut.SUCCESSFULLY_ADDED_GATEHOUSE))
                return true;
        } else
            System.out.println("The game shall end here and now duo to gatehouse settlement issues. You will return to the main menu");
        return false;
    }
}
