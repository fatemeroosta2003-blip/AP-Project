package model.buildings;

import model.User;

public class Building {
    protected BuildingEnum type;
    protected int hp;
    protected User owner;
    protected int direction;
    protected boolean active;
    protected boolean destroyed = false;
    private boolean onFire = false;
    private int turnsOnFire = 1;

    public Building(BuildingEnum type, User owner, int direction, boolean active) {
        this.type = type;
        this.owner = owner;
        this.direction = direction;
        this.hp = type.getOriginalHp();
        this.active = active;
    }

    public BuildingEnum getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public User getOwner() {
        return owner;
    }

    public int getDirection() {
        return direction;
    }

    public void changeHp(int amount) {
        this.hp += amount;
    }

    public void resetHp() {
        this.hp = type.getOriginalHp();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void takeDamage(int damage) {
        if(!destroyed)
            hp -= damage;
        if(hp <= 0) {
            //!active && !destroyed -> with enough people, the building can function again.
            //!active && destroyed  -> building can't function unless it is repaired first.
            destroyed = true;
            active = false;
        }
    }

    public boolean isOnFire() {
        return onFire;
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
    }

    public int getTurnsOnFire() {
        return turnsOnFire;
    }

    public void addTurnsOnFire() {
        if(!isOnFire()) return;
        this.turnsOnFire+= 1;
        if(turnsOnFire > 3) {
            turnsOnFire = 1;
            onFire = false;
        }
    }

    public void resetTurnsOnFire() {
        setOnFire(true);
        this.turnsOnFire = 1;
    }
}