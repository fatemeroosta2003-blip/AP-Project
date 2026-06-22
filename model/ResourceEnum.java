package model;

import model.units.ResourceType;

public enum ResourceEnum {
    NULL("", 0, 0, null),
    WHEAT("wheat", 180, 5, ResourceType.NORMAL),
    MEAT("meat", 18, 6, ResourceType.FOOD),
    APPLE("apple", 12, 6, ResourceType.FOOD),
    CHEESE("cheese", 22, 6, ResourceType.FOOD),
    WOOD("wood", 10, 5, ResourceType.NORMAL),
    FLOUR("flour", 240, 10, ResourceType.NORMAL),
    HOPS("hops", 40, 4, ResourceType.NORMAL),
    STONE("stone", 40, 10, ResourceType.NORMAL),
    IRON("iron", 40, 20, ResourceType.NORMAL),
    //PITCH("pitch", 40, 20),
    BREAD("bread", 26, 6, ResourceType.FOOD),
    ALE("beer", 60, 20, ResourceType.NORMAL),
    HORSE("horse", 160, 80, ResourceType.NORMAL),
    ARMOUR("armour", 180, 100, ResourceType.WEAPON),
    OIL("oil", 160, 80, ResourceType.NORMAL),
    SWORD("sword", 160, 80, ResourceType.WEAPON),
    CROSSBOW("crossbow", 80, 40, ResourceType.WEAPON),
    SPEAR("spear", 100, 50, ResourceType.WEAPON),
    PIKE("pike", 120, 60, ResourceType.WEAPON),
    LEATHER_ARMOR("leather_armor", 160, 80, ResourceType.WEAPON),
    BOW("bow", 80, 40, ResourceType.WEAPON),
    MACE("mace", 180, 60, ResourceType.WEAPON),
    HORSEANDBOW("", 0, 0, ResourceType.WEAPON);
    private String name;
    private int buyCost;
    private int sellCost;
    private ResourceType resourceType;

    ResourceEnum(String name, int buyCost, int sellCost, ResourceType resourceType) {
        this.name = name;
        this.buyCost = buyCost;
        this.sellCost = sellCost;
        this.resourceType = resourceType;
    }

    public String getName() {
        return name;
    }

    public int getBuyCost() {
        return buyCost;
    }

    public int getSellCost() {
        return sellCost;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}
