package model.units;

public class Troop {
    private UnitEnum type;
    private int hp;
    private boolean dead;

    public Troop(UnitEnum type) {
        this.type = type;
        this.hp = type.getPrimaryHp();
    }

    public UnitEnum getType() {
        return type;
    }

    public void setType(UnitEnum type) {
        this.type = type;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void takeDamage(int amount) {
        if(!dead)
            hp -= amount;
        if(hp <= 0)
            dead = true;
    }
}
