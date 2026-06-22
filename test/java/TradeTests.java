import controller.LoginMenuController;
import controller.gameMenuControllers.TradeMenuController;
import model.Governance;
import model.TradeItem;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import view.enums.Commands;
import view.enums.ShopAndTradeControllerOut;

import java.io.IOException;
import java.util.regex.Matcher;

public class TradeTests {
    TradeMenuController tradeMenuController = new TradeMenuController(new User("nik", "\"Vox machina3#\"", "nick", "k.s@kj", "ran", 2, "mom", 0));
    User user  = tradeMenuController.getCurrentUser();
    User other = new User("epo", "Epo400#", "k", "klkl.s@kj", "ran", 2, "dd", 0);
    @BeforeClass
    public static void settingUp() throws IOException {
        LoginMenuController.extractUserData();
    }

    @Test
    public void showList() {
        System.out.println(tradeMenuController.showTradeList());
    }

    @Test
    public void newTrade() {
        Matcher matcher = Commands.getMatcher(" trade -t bread -a 5 -p 50",Commands.TRADE);
        Assertions.assertEquals(tradeMenuController.newTradeRequest(matcher.group("data")), ShopAndTradeControllerOut.INVALID_INPUT_FORMAT);
        matcher = Commands.getMatcher(" trade -t bread -a 5 -p  -m desperate ",Commands.TRADE);
        Assertions.assertEquals(tradeMenuController.newTradeRequest(matcher.group("data")), ShopAndTradeControllerOut.INVALID_INPUT_FORMAT);
        matcher = Commands.getMatcher(" trade -t bread -a 5 -p 5000 -m desperate ",Commands.TRADE);
        Assertions.assertEquals(tradeMenuController.newTradeRequest(matcher.group("data")), ShopAndTradeControllerOut.CANNOT_AFFORD_TRADE);
        user.getGovernance().changeGold(50000000);
        matcher = Commands.getMatcher(" trade -t bread -a 5 -p 50 -m demand ",Commands.TRADE);
        Assertions.assertEquals(tradeMenuController.newTradeRequest(matcher.group("data")), ShopAndTradeControllerOut.REQUEST_ADDED);
    }

    @Test
    public void acceptAndNotify() {
        user.getGovernance().changeGold(5000);
        other.getGovernance().changeGold(5000);
        tradeMenuController.setCurrentUser(other);
        Matcher matcher = Commands.getMatcher(" trade -t bread -a 5 -p 50 -m demand ",Commands.TRADE);
        Assertions.assertEquals(tradeMenuController.newTradeRequest(matcher.group("data")), ShopAndTradeControllerOut.REQUEST_ADDED);
        tradeMenuController.setCurrentUser(user);
        matcher = Commands.getMatcher(" trade -t bread -a 5 -p 50 -m demand ",Commands.TRADE);
        Assertions.assertEquals(tradeMenuController.newTradeRequest(matcher.group("data")), ShopAndTradeControllerOut.REQUEST_ADDED);
        tradeMenuController.setCurrentUser(other);

        matcher = Commands.getMatcher((" trade accept -i " + Governance.getAllTrades().get(0).getId() + " -m   "),Commands.ACCEPT_TRADE);
        Assertions.assertEquals(tradeMenuController.doTheTrade(matcher.group("data"), null), ShopAndTradeControllerOut.INVALID_INPUT_FORMAT);
        matcher = Commands.getMatcher((" trade accept -i rubbish here -m accepted"),Commands.ACCEPT_TRADE);
        Assertions.assertEquals(tradeMenuController.doTheTrade(matcher.group("data") , null), ShopAndTradeControllerOut.TRADE_NOT_FOUND);
        matcher = Commands.getMatcher((" trade accept -i " + Governance.getAllTrades().get(0).getId() + " -m accepted"),Commands.ACCEPT_TRADE);
        Assertions.assertEquals(tradeMenuController.doTheTrade(matcher.group("data") , null), ShopAndTradeControllerOut.SELF_TRADE);
        matcher = Commands.getMatcher((" trade accept -i " + Governance.getAllTrades().get(1).getId() + " -m accepted"),Commands.ACCEPT_TRADE);
        Assertions.assertEquals(tradeMenuController.doTheTrade(matcher.group("data") , null), ShopAndTradeControllerOut.NOT_ENOUGH_COMMODITY);
        TradeItem activeTrade = Governance.getAllTrades().get(1);
        other.getGovernance().changeResourceAmount(activeTrade.getType(),50);
        int otherPreviousGold = other.getGovernance().getGold();
        int userPreviousGold = user.getGovernance().getGold();
        int otherPreviousResource = other.getGovernance().getResourceAmount(activeTrade.getType());
        int userPreviousResource = user.getGovernance().getResourceAmount(activeTrade.getType());
        matcher = Commands.getMatcher((" trade accept -i " + Governance.getAllTrades().get(1).getId() + " -m accepted"),Commands.ACCEPT_TRADE);
        Assertions.assertEquals(tradeMenuController.doTheTrade(matcher.group("data"), null), ShopAndTradeControllerOut.SUCCESS_FOR_TRADE);
        Assertions.assertEquals(user.getGovernance().getResourceAmount(activeTrade.getType()),userPreviousResource + activeTrade.getAmount());
        Assertions.assertEquals(user.getGovernance().getGold(),userPreviousGold - activeTrade.getPrice());
        Assertions.assertEquals(other.getGovernance().getResourceAmount(activeTrade.getType()),otherPreviousResource - activeTrade.getAmount());
        Assertions.assertEquals(other.getGovernance().getGold(),otherPreviousGold + activeTrade.getPrice());
        Assertions.assertFalse(Governance.getAllTrades().get(1).getActive());
        Assertions.assertNotNull(tradeMenuController.showTradeHistory());
        tradeMenuController.setCurrentUser(user);
        Assertions.assertNotNull(tradeMenuController.showTradeHistory());
        //for manual testing:
        //System.out.println(tradeMenuController.getCurrentUser().getUsername() + "\n" + tradeMenuController.showTradeHistory());
        //System.out.println(tradeMenuController.showTradeList());
        Assertions.assertFalse(Governance.getAllTrades().get(0).getSeenRequester());
        Assertions.assertFalse(Governance.getAllTrades().get(1).getSeenRequester());
        Assertions.assertNotNull(tradeMenuController.popup());
        Assertions.assertNotNull(tradeMenuController.popup());
        tradeMenuController.setCurrentUser(other);
        Assertions.assertNotNull(tradeMenuController.popup());
        Assertions.assertFalse(Governance.getAllTrades().get(0).getSeenRequester());
        Assertions.assertTrue(Governance.getAllTrades().get(1).getSeenRequester());
    }
}
