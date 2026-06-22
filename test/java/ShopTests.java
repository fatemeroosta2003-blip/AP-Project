import controller.LoginMenuController;
import controller.RegisterMenuController;
import controller.gameMenuControllers.ShopMenuController;
import model.Resource;
import model.ResourceEnum;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import view.ShopMenu;
import view.enums.Commands;
import view.enums.ProfisterControllerOut;
import view.enums.ShopAndTradeControllerOut;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

public class ShopTests {
    @BeforeClass
    public static void settingUp() throws IOException {
        LoginMenuController.extractUserData();
    }

    ShopMenuController shopMenuController = new ShopMenuController(new User("nik", "\"Vox machina3#\"", "nick", "k.s@kj", "ran", 2, "mom", 0));
    @Test
    public void buyAndShow() {
        User user = shopMenuController.getCurrentUser();
        Assertions.assertNotNull(shopMenuController.showPriceList(user));
        Matcher matcher = Commands.getMatcher(" sell -a 54  -i watermelon   ",Commands.BUY_SHOP);
        Assertions.assertEquals(shopMenuController.buy(matcher.group("data"), user),ShopAndTradeControllerOut.INVALID_ITEM);
        matcher = Commands.getMatcher(" sell  -a 22 -i  meat ",Commands.BUY_SHOP);
        Assertions.assertEquals(shopMenuController.buy(matcher.group("data"), user),ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_PURCHASE);
        user.getGovernance().changeGold(2000);
        Assertions.assertEquals(shopMenuController.buy(matcher.group("data"), user),ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_PURCHASE);
        int cashBefore = user.getGovernance().getGold();
        Assertions.assertEquals(shopMenuController.getMerchandise().getAmount(), 22);
        Assertions.assertEquals(shopMenuController.getMerchandise().getType(), ResourceEnum.MEAT);
        shopMenuController.purchase();
        Assertions.assertEquals(user.getGovernance().getGold(), cashBefore - ResourceEnum.MEAT.getBuyCost() * 22);
        Assertions.assertEquals(user.getGovernance().getResourceAmount(ResourceEnum.MEAT), 22);
    }

    @Test
    public void sell() {
        User user = shopMenuController.getCurrentUser();
        user.getGovernance().changeResourceAmount(ResourceEnum.MEAT, 22);
        Matcher matcher = Commands.getMatcher(" buy -a 54  -i watermelon   ",Commands.SELL_SHOP);
        Assertions.assertEquals(shopMenuController.sell(matcher.group("data"), user),ShopAndTradeControllerOut.INVALID_ITEM);
        matcher = Commands.getMatcher(" buy  -a 252 -i  meat ",Commands.SELL_SHOP);
        Assertions.assertEquals(shopMenuController.sell(matcher.group("data"), user),ShopAndTradeControllerOut.NOT_ENOUGH_COMMODITY);
        matcher = Commands.getMatcher(" buy  -a 20 -i  meat ",Commands.SELL_SHOP);
        Assertions.assertEquals(shopMenuController.sell(matcher.group("data"), user),ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_SELL);
        int cashBefore = user.getGovernance().getGold();
        int meatBefore = user.getGovernance().getResourceAmount(ResourceEnum.MEAT);
        Assertions.assertEquals(shopMenuController.getMerchandise().getAmount(), 20);
        Assertions.assertEquals(shopMenuController.getMerchandise().getType(), ResourceEnum.MEAT);
        shopMenuController.retail();
        Assertions.assertEquals(user.getGovernance().getGold(), cashBefore + ResourceEnum.MEAT.getSellCost() * 20);
        Assertions.assertEquals(user.getGovernance().getResourceAmount(ResourceEnum.MEAT), meatBefore - 20);
    }
}