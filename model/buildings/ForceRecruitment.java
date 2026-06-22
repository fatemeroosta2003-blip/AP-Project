package model.buildings;

import controller.modelFunctions.ForceRecruitmentFuncs;
import model.Map;
import model.User;
import model.units.UnitEnum;
import view.enums.GameControllerOut;

public class ForceRecruitment extends Building {
    private int x;
    private int y;
    private boolean[] troopTypes = new boolean[4];
    //  troopTypes[0] = engineer
    //  troopTypes[1] = ladder man
    //  troopTypes[2] = european
    //  troopTypes[3] = arab

    public ForceRecruitment(BuildingEnum type, User owner, int direction, int yCoordinate, int xCoordinate, boolean active) {
        super(type, owner, direction, active);
        troopTypes = ForceRecruitmentFuncs.setRecruitmentTypes(type);
        this.x = x;
        this.y = y;
        if (type.equals(BuildingEnum.CATHEDRAL))
            owner.getGovernance().changePopularity(4);
        if (type.equals(BuildingEnum.CHURCH))
            owner.getGovernance().changePopularity(2);
    }

    public GameControllerOut recruitByOrderCreation(UnitEnum type, int count, Map map, boolean isArab) {
        return ForceRecruitmentFuncs.recruitUnitByOrderCreation(owner, troopTypes, type, count, map, y, x);
    }
}
