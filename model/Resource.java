package model;

public class Resource {
    private ResourceEnum type;
    private int amount;

    public Resource(ResourceEnum type, int amount) {
        this.amount = amount;
        this.type = type;
    }

    public ResourceEnum getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public void changeAsset(int amount) {
        this.amount += amount;
    }
}
