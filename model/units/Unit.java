package model.units;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import model.Point;
import model.Tile;
import model.User;
import view.controls.GameControlTest;

import java.util.ArrayList;

public class Unit {
    String state;
    //this is to distinguish between different units in one house,
    //primary location is set when a unit is created, and will be
    //updated everytime select order is used:
    private int xOrigin;
    private int yOrigin;
    private Tile originTile = null;
    private Tile targetTile = null;
    private int xDestination = -1;
    private int yDestination = -1;
    private boolean onPatrol = false;
    private Point[] patrolDestinations;
    private boolean isOnMove = false;

    private BooleanProperty attack = new SimpleBooleanProperty();
    public boolean isAttack() {
        return attack.get();
    }
    public BooleanProperty attackProperty() {
        return attack;
    }
    public void setAttack(boolean attack) {
        this.attack.set(attack);
    }
    User master;
    //private HashMap<UnitEnum, ArrayList<Troop>> troops = new HashMap<>();
    private ArrayList<Troop> troops = new ArrayList<>();
    private Tile currentTile;

    public Unit(User master, UnitEnum type, int count, int yOrigin, int xOrigin) {
        this.master = master;
//        ArrayList<Troop> sameKind = new ArrayList<>();
//        for (int i = 0; i < count; i++)
//            sameKind.add(new Troop(type));
//        this.troops.put(type, sameKind);
        for (int i = 0; i < count; i++) {
            troops.add(new Troop(type));
        }
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.state = "standing";
    }

    public void addByTypeAndCount(UnitEnum type, int count) {
        for (int i = 0; i < count; i++) {
            troops.add(new Troop(type));
        }
//        ArrayList<Troop> sameKind = this.troops.get(type);
//        for (int i = 0; i < count; i++)
//            sameKind.add(new Troop(type));
//        this.troops.put(type, sameKind);
    }

//    public void addByTypeAndArrayList(UnitEnum type, ArrayList<Troop> list) {
//        this.troops.putIfAbsent(type, list);
//        for (Troop troop : list) {
//            this.troops.get(type).add(troop);
//        }
//    }

    public void addByUnit(Unit unit) {
        this.troops.addAll(unit.troops);
//        for (Map.Entry<UnitEnum, ArrayList<Troop>> integerEntry : unit.troops.entrySet()) {
//            addByTypeAndArrayList(integerEntry.getKey(), integerEntry.getValue());
//        }
    }

    public String getState() {
        return state;
    }

    public int getxOrigin() {
        return xOrigin;
    }

    public int getyOrigin() {
        return yOrigin;
    }

    public User getMaster() {
        return master;
    }

    public ArrayList<Troop> getTroops() {
        return troops;
    }

    public void takeSteadyDamageForAll(int damage) {
        for (Troop troop : troops) {
            if (!troop.isDead())
                troop.setHp(troop.getHp() - damage);
            if (troop.getHp() <= 0)
                troop.setDead(true);
        }
    }

    public void clearTheDead() {
        ArrayList<Troop> theLiving = new ArrayList<>();
        for (Troop troop : troops) {
            if (!troop.isDead())
                theLiving.add(troop);
        }
        troops = theLiving;
    }

    public void setxOrigin(int xOrigin) {
        this.xOrigin = xOrigin;
    }

    public void setyOrigin(int yOrigin) {
        this.yOrigin = yOrigin;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getxDestination() {
        return xDestination;
    }

    public void setxDestination(int xDestination) {
        this.xDestination = xDestination;
    }

    public int getyDestination() {
        return yDestination;
    }

    public void setyDestination(int yDestination) {
        this.yDestination = yDestination;
    }

    public int getSpeed() {
        int speed;
        int sum = 0;
        int counter = 0;
        for (Troop troop : troops) {
            sum += troop.getType().getSpeed();
            counter++;
        }
        if (counter != 0)
            return Math.round(sum / counter);
        else return 0;
    }

    public boolean isOnPatrol() {
        return onPatrol;
    }

    public void setOnPatrol(boolean onPatrol) {
        this.onPatrol = onPatrol;
    }

    public Point[] getPatrolDestinations() {
        return patrolDestinations;
    }

    public void setPatrolDestinations(Point[] patrolDestinations) {
        this.patrolDestinations = patrolDestinations;
    }

    public int getUnitDamage() {
        int damage = 0;
        for (Troop troop : this.troops) {
            damage += troop.getType().getDamage();
        }
        return damage;
    }

    public int getUnitHp() {
        int hp = 0;
        for (Troop troop : this.troops) {
            hp += troop.getHp();
        }
        return hp;
    }

    public String getPresentTypes() {
        StringBuilder types = new StringBuilder();
        ArrayList<UnitEnum> unitEnums = new ArrayList<>();
        for (Troop troop : this.troops) {
            if(!unitEnums.contains(troop.getType())) {
                unitEnums.add(troop.getType());
                types.append("\n").append(troop.getType().getName());
            }
        }
        return types.toString();
    }

    public boolean isOnMove() {
        return isOnMove;
    }

    public void setOnMove(boolean onMove) {
        isOnMove = onMove;
    }

    public Tile getOriginTile() {
        return originTile;
    }

    public void setOriginTile(Tile originTile) {
        this.originTile = originTile;
    }

    public Tile getTargetTile() {
        return targetTile;
    }

    public void setTargetTile(Tile targetTile) {
        this.targetTile = targetTile;
    }

    public Tile getCurrentTile() {return currentTile;}

    public void setCurrentTile(Tile currentTile) {this.currentTile = currentTile;}
}

