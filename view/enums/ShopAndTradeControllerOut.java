package view.enums;

public enum ShopAndTradeControllerOut {
    INVALID_ITEM("There is no such item in the store!"),
    NOT_ENOUGH_GOLD("I'm afraid you cannot afford this product. Get some gold!"),
    SELF_TRADE("Oh dear, you can't accept your own trade requests!"),
    CANNOT_AFFORD_TRADE("You don't have enough gold to keep your end of the bargain. No trade!"),
    PROMPT_CONFIRMATION_FOR_PURCHASE("Are you sure you want to sell "),
    INVALID_INPUT_FORMAT("Failed: invalid input format"),
    PROMPT_CONFIRMATION_FOR_SELL("Are you sure you want to buy "),
    ABORT_THE_MISSION("Aborting the mission. No problemo!"),
    NOT_ENOUGH_COMMODITY("We don't have enough number of commodities in the stock"),
    SUCCESS_FOR_SHOP("permission granted to do the doThePurchase"),
    SUCCESS_FOR_TRADE("Trade done successfully"),
    REQUEST_ADDED("Your request has been successfully added to the list"),
    TRADE_NOT_FOUND("No trade was found with that id"),
    SUCCESSFULL_BUY("Purchase done successfully"),
    SUCCESSFULLY_SELL("Item(s) sold successfully"),
    INVALID_RESOURCE_NAME("The resource you called for is not to be found in these lands... Name something else!"),
    ;
    private String content;

    ShopAndTradeControllerOut(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


}
