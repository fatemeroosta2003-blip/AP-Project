package model.buildings;

import model.User;

public class Tower extends Building {
    private int fireRange;
    private int defendRange;
    private boolean supportRiders;

    public Tower(BuildingEnum type, User owner, int direction, boolean active) {
        super(type, owner, direction, active);
        switch (type) {
            case DEFENSE_TURRET:
                this.fireRange = 0;
                this.defendRange = 0;
                this.supportRiders = false;
                break;
            case LOOKOUT_TOWER:
                this.fireRange = 0;
                this.defendRange = 0;
                this.supportRiders = false;
                break;
            case PERIMETER_TOWER:
                this.fireRange = 0;
                this.defendRange = 0;
                this.supportRiders = false;
                break;
            case ROUND_TOWER:
                this.fireRange = 0;
                this.defendRange = 0;
                this.supportRiders = true;
                break;
            case SQUARE_TOWER:
                this.fireRange = 0;
                this.defendRange = 0;
                this.supportRiders = true;
                break;
        }
    }
}
