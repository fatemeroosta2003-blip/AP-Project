package model;

import controller.modelFunctions.ResourceMakerFuncs;
import model.buildings.Building;
import model.buildings.Storage;
import model.units.Unit;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class Governance {
    //todo: everytime a food type is manipulated, popularity should change if updateFoodDiversity changes.
    private static ArrayList<User> empires = new ArrayList<>();
    private int unemployedPopulation = 25;
    private int maximumPopulation = 100;
    private int popularity = 75;
    private int foodRate = -2;
    private int taxRate = 0;
    private int fearRate = 0;
    private int gold = 5000;
    private Storage granary;
    private Storage stockpile;
    private Storage armoury;
    private boolean haveGateHouse = false;
    private ArrayList<Building> buildings = new ArrayList<>();
    private ArrayList<Resource> resources = new ArrayList<>();
    private HashMap<ResourceEnum, Integer> resourceCount = new HashMap<>();
    private ArrayList<TradeItem> userTrades = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<Unit>();
    private static ArrayList<TradeItem> allTrades = new ArrayList<>();

    public Governance() {
        changeResourceAmount(ResourceEnum.WOOD,75);
        changeResourceAmount(ResourceEnum.APPLE,75);
        changeResourceAmount(ResourceEnum.BREAD,75);
        changeResourceAmount(ResourceEnum.STONE,75);
        changeResourceAmount(ResourceEnum.BOW,15);
        changeResourceAmount(ResourceEnum.SPEAR,15);
        changeResourceAmount(ResourceEnum.SWORD,15);
        changeResourceAmount(ResourceEnum.ARMOUR,15);
        changeResourceAmount(ResourceEnum.MACE,15);
        changeResourceAmount(ResourceEnum.LEATHER_ARMOR,15);
        changeResourceAmount(ResourceEnum.PIKE,15);
    }

    public void changeUnemployedPopulation(int unemployedPopulation) {
        this.unemployedPopulation += unemployedPopulation;
    }

    public int getUnemployedPopulation() {
        return unemployedPopulation;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public int getMaximumPopulation() {
        return maximumPopulation;
    }

    public void changeMaximumPopulation(int amount) {
        this.maximumPopulation += amount;
    }

    public void makeGovernanceNew() {
        empires.clear();
    }

    public static ArrayList<User> getEmpires() {
        return empires;
    }

    public static User getNextPlayer(User currentUser) {
        for (int i = 0; i < empires.size() - 1; i++)
            if (empires.get(i).getUsername().equals(currentUser.getUsername()))
                return empires.get(i + 1);
        return empires.get(0);
    }

    public static void setEmpires(ArrayList<User> empires) {
        Governance.empires = empires;
    }

    public int getGold() {
        return gold;
    }

    public void changeGold(int amount) {
        this.gold += amount;
    }

    public int getPopularity() {
        return popularity;
    }

    public int getFoodRate() {
        return foodRate;
    }

    public int getFoodDiversity() {
        int num = 0;
        for (Resource resource : this.resources) {
            if ((resource.getType().equals(ResourceEnum.APPLE) || resource.getType().equals(ResourceEnum.BREAD) ||
                    resource.getType().equals(ResourceEnum.MEAT) || resource.getType().equals(ResourceEnum.CHEESE))
                    && resource.getAmount() != 0)
                num++;
        }
        return num;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public int getFearRate() {
        return fearRate;
    }

    public void changePopularity(int number) {
        this.popularity += number;
        if (this.popularity > 100)
            this.popularity = 100;
        if (popularity<0)
            popularity = 0;
    }

    public void changeFoodRate(int number) {
        this.foodRate = number;
    }

    public void changeFearRate(int number) {
        this.fearRate += number;
    }

    public void changeTaxRate(int number) {
        this.taxRate = number;
    }

    public Building getGranary() {
        return granary;
    }

    public Building getStockpile() {
        return stockpile;
    }

    public Building getArmoury() {
        return armoury;
    }

    public void setGranary(Building granary) {
        this.granary = (Storage) granary;
    }

    public void setStockpile(Building stockpile) {
        this.stockpile = (Storage) stockpile;
    }

    public void setArmoury(Building armoury) {
        this.armoury = (Storage) armoury;
    }

    public ArrayList<TradeItem> getUserTrades() {
        return userTrades;
    }

    public void addToUserTrades(TradeItem tradeItem) {
        this.userTrades.add(tradeItem);
    }

    public static void addToAllTrades(TradeItem tradeItem) {
        allTrades.add(tradeItem);
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public static ArrayList<TradeItem> getAllTrades() {
        return allTrades;
    }

    public boolean changeResourceAmount(ResourceEnum type, int amount) {
        return ResourceMakerFuncs.changeOrAddResource(this.resources, type, amount);
    }

    public int getResourceAmount(ResourceEnum type) {
        if (type == null) return 0;
        if (this.resources != null)
            for (Resource r : this.resources) {
                if (r.getType().equals(type))
                    return r.getAmount();
            }
        return 0;
    }

    public void setUserTrades(ArrayList<TradeItem> userTrades) {
        this.userTrades = userTrades;
    }

    public static void setAllTrades(ArrayList<TradeItem> allTrades) {
        Governance.allTrades = allTrades;
    }

    public boolean isStockpileFull() {
        if (stockpile.getStored() >= stockpile.getCapacity()) return true;
        return false;
    }

    public boolean isGranaryFull() {
        if (granary.getStored() >= granary.getCapacity()) return true;
        return false;
    }

    public boolean isArmouryFull() {
        if (armoury.getStored() >= armoury.getCapacity()) return true;
        return false;
    }

    public void newStockpileResource() {
        HashMap<ResourceEnum, Integer> count = new HashMap<>();
        EnumSet<ResourceEnum> resourceEnums = EnumSet.allOf(ResourceEnum.class);
        ArrayList<Resource> list = new ArrayList<>(resourceEnums.size());
        for (ResourceEnum s : resourceEnums) {
            Resource r = new Resource(s, 0);
            list.add(r);
            count.put(s, 0);
        }
        resources = list;
        resourceCount = count;
    }

    public boolean haveGateHouse() {
        return haveGateHouse;
    }

    public void setHaveGateHouse(boolean haveGateHouse) {
        this.haveGateHouse = haveGateHouse;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void addUnit(Unit unit) {
        if (unit != null || unit.getTroops().size() != 0)
            this.units.add(unit);
    }

    public void setFoodRate(int foodRate) {
        this.foodRate = foodRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public void setFearRate(int fearRate) {
        this.fearRate = fearRate;
    }

}
