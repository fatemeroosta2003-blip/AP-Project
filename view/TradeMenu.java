package view;

import controller.gameMenuControllers.TradeMenuController;
import model.User;
import view.enums.Commands;

import java.util.regex.Matcher;

public class TradeMenu {
    private final User currentUser;

    public TradeMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void run() {
        TradeMenuController tradeMenuController = new TradeMenuController(currentUser);
        System.out.print(tradeMenuController.popup());
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            Matcher matcher;
            if (command.matches("back")) {
                System.out.println("Your are in the game menu");
                return;
            } else if (command.matches("\\s*trade\\s+list\\s*")) {
                System.out.print(tradeMenuController.showTradeList());
            } else if ((matcher = Commands.getMatcher(command, Commands.ACCEPT_TRADE)) != null) {
                System.out.println(tradeMenuController.doTheTrade(matcher.group("data"), null).getContent());
            } else if (command.matches("\\s*trade\\s+history\\s*")) {
                System.out.print(tradeMenuController.showTradeHistory());
            } else if (command.matches("show current menu")) System.out.println("trade menu");
            else if ((matcher = Commands.getMatcher(command, Commands.TRADE)) != null) {
                System.out.println(tradeMenuController.newTradeRequest(matcher.group("data")).getContent());
            } else
                System.out.println("invalid command");
        }
    }

}
