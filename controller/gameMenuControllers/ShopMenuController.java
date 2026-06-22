package controller.gameMenuControllers;

import controller.CommonController;
import model.Resource;
import model.ResourceEnum;
import model.User;
import view.enums.ShopAndTradeControllerOut;

public class ShopMenuController {
    private final User currentUser;
    private Resource merchandise;

    public void setMerchandise(Resource merchandise) {
        this.merchandise = merchandise;
    }

    public ShopMenuController(User currentUser) {
        this.currentUser = currentUser;
    }

    public Resource getMerchandise() {
        return merchandise;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String showPriceList(User currentUser) {
        String ans = new String();
        for (ResourceEnum value : ResourceEnum.values()) {
            if (!value.getName().equals(""))
                ans += value.getName() + ":\n        sell cost -> " + value.getBuyCost() + "\n        buy cost -> " +
                        value.getSellCost() + "\n        storage amount -> " + currentUser.getGovernance().getResourceAmount(value) + "\n";
        }
        return ans;
    }

    //doThePurchase and buy must be called immediately after extract item and amount
    public ShopAndTradeControllerOut buy(String data, User master) {
        //ShopAndTradeControllerOut out = extractItemAndAmount(data);
        //if (!out.equals(ShopAndTradeControllerOut.SUCCESS_FOR_SHOP))
        //    return out;
        if (this.merchandise.getType().equals(ResourceEnum.NULL))
            return ShopAndTradeControllerOut.INVALID_ITEM;
        if (master.getGovernance().getGold() < this.merchandise.getType().getBuyCost() * this.merchandise.getAmount())
            return ShopAndTradeControllerOut.NOT_ENOUGH_GOLD;

        //todo: check capacity
        return ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_PURCHASE;
    }

    public ShopAndTradeControllerOut sell(String data, User master) {
        //ShopAndTradeControllerOut out = extractItemAndAmount(data);
        //if (!out.equals(ShopAndTradeControllerOut.SUCCESS_FOR_SHOP))
        //   return out;
        if (this.merchandise.getType().equals(ResourceEnum.NULL))
            return ShopAndTradeControllerOut.INVALID_ITEM;
        if (master.getGovernance().getResourceAmount(this.merchandise.getType()) < merchandise.getAmount())
            return ShopAndTradeControllerOut.NOT_ENOUGH_COMMODITY;
        return ShopAndTradeControllerOut.PROMPT_CONFIRMATION_FOR_SELL;
    }

    public ShopAndTradeControllerOut extractItemAndAmount(String data) {
        if (CommonController.dataExtractor(data, "((?<!\\S)-i\\s+(?<wantedPart>([^-]+))(?<!\\s))").length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-a\\s+(?<wantedPart>(\\d+))(?<!\\s))").length() == 0)
            return ShopAndTradeControllerOut.INVALID_INPUT_FORMAT;
        if (CommonController.dataExtractor(data, "((?<!\\S)-i\\s+(?<wantedPart>([^-]+))(?<!\\s))").trim().length() == 0 ||
                CommonController.dataExtractor(data, "((?<!\\S)-a\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim().length() == 0)
            return ShopAndTradeControllerOut.INVALID_INPUT_FORMAT;
        String item = CommonController.dataExtractor(data, "((?<!\\S)-i\\s+(?<wantedPart>([^-]+))(?<!\\s))").trim();
        int amount = Integer.parseInt(CommonController.dataExtractor(data, "((?<!\\S)-a\\s+(?<wantedPart>(\\d+))(?<!\\s))").trim());
        ResourceEnum resourceItem = CommonController.resourceFinder(item);
        this.merchandise = new Resource(resourceItem, amount);
        return ShopAndTradeControllerOut.SUCCESS_FOR_SHOP;
    }

    public ShopAndTradeControllerOut purchase() {
        currentUser.getGovernance().changeGold(-1 * merchandise.getType().getBuyCost() * merchandise.getAmount());
        currentUser.getGovernance().changeResourceAmount(merchandise.getType(), merchandise.getAmount());
        return ShopAndTradeControllerOut.SUCCESSFULL_BUY;
    }

    public ShopAndTradeControllerOut retail() {
        currentUser.getGovernance().changeGold(merchandise.getType().getSellCost() * merchandise.getAmount());
        currentUser.getGovernance().changeResourceAmount(merchandise.getType(), -1 * merchandise.getAmount());
        return ShopAndTradeControllerOut.SUCCESSFULLY_SELL;
    }
}
