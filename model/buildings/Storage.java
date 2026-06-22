package model.buildings;

import model.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class Storage extends Building {
    private int capacity;
    private int stored;

    public Storage(BuildingEnum type, User owner, int direction, boolean active) {
        super(type, owner, direction, active);
        this.capacity = 5000;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStored() {
        return stored;
    }
}
