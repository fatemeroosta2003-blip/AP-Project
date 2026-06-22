package model.units;

import model.ResourceEnum;

public enum UnitEnum {
    LORD("lord", ResourceEnum.NULL, 0, 250, 5000, 0, 1, false),
    PIKE_MAN("pike man", ResourceEnum.PIKE, 50, 50, 200, 0, 1, false),
    TUNNELER("tunneler", ResourceEnum.NULL, 30, 50, 25, 0, 3, false),
    ARCHER_BOW("archer bow", ResourceEnum.BOW, 20, 25, 25, 0, 3, true),
    ARCHER("archer", ResourceEnum.BOW, 20, 25, 25, 0, 3, true),
    SPEAR_MAN("spear man", ResourceEnum.SPEAR, 10, 50, 50, 0, 2, false),
    SWORDS_MAN("swords man", ResourceEnum.SWORD, 150, 200, 400, 0, 1, false),
    CROSSBOW_MAN("crossbow_man", ResourceEnum.CROSSBOW, 40, 25, 100, 0, 2, false),
    MACE_MAN("mace man", ResourceEnum.MACE, 100, 100, 200, 0, 2, false),
    SOLDIER_ENGINEER("soldier engineer", ResourceEnum.NULL, 0, 0, 0, 0, 0, false),
    KNIGHT("knight", ResourceEnum.HORSE, 250, 250, 1000, 0, 4, false),
    BLACK_MONK("black monk", ResourceEnum.WOOD, 150, 100, 200, 0, 1, false),
    HORSE_ARCHER("horse archer", ResourceEnum.HORSEANDBOW, 80, 25, 100, 0, 4, true),
    ARABIAN_SWORDSMAN("arabian swordsman", ResourceEnum.SWORD, 200, 200, 400, 0, 4, true),
    LADDER_MAN("ladder man", ResourceEnum.WOOD, 10, 0, 25, 0, 3, true),
    ASSASSIN("assassin", ResourceEnum.NULL, 500, 100, 200, 0, 2, true),
    FIRE_THROWER("fire thrower", ResourceEnum.NULL, 100, 100, 50, 0, 4, true),
    SLAVE("slave", ResourceEnum.NULL, 300, 25, 1, 0, 3, true),
    ENGINEER("engineer", ResourceEnum.NULL, 25, 0, 25, 0, 2, true),
    SLINGER("slinger", ResourceEnum.STONE, 50, 25, 25, 0, 3, true),
    DOGS("dog", ResourceEnum.NULL, 0, 25, 100, 0, 4, false);
    private String name;
    private int cost;
    private int damage;
    private int primaryHp;
    private int range;
    private int speed;
    private boolean isArab;
    private ResourceEnum weaponType;

    UnitEnum(String name, ResourceEnum weaponType, int cost, int damage, int primaryHp, int range, int speed, boolean isArab) {
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.primaryHp = primaryHp;
        this.range = range;
        this.speed = speed;
        this.isArab = isArab;
        this.weaponType = weaponType;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getDamage() {
        return damage;
    }

    public int getPrimaryHp() {
        return primaryHp;
    }

    public int getRange() {
        return range;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isArab() {
        return isArab;
    }

    public ResourceEnum getWeaponType() {
        return weaponType;
    }
}
