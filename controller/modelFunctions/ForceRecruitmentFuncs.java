package controller.modelFunctions;

import model.Map;
import model.ResourceEnum;
import model.User;
import model.buildings.BuildingEnum;
import model.units.UnitEnum;
import view.enums.GameControllerOut;

public class ForceRecruitmentFuncs {

    public static GameControllerOut recruitUnitByOrderCreation(User master, boolean[] troopTypes, UnitEnum type, int count, Map map, int y, int x) {
        if ((type.equals(UnitEnum.ENGINEER) && !troopTypes[0]) ||
                (type.equals(UnitEnum.LADDER_MAN) && !troopTypes[1]))
            return GameControllerOut.CANNOT_ADD_UNIT_FROM_HERE;
        if ((type.isArab() && !troopTypes[3]) || (!type.isArab() && !troopTypes[2]))
            return GameControllerOut.CANNOT_ADD_UNIT_FROM_HERE;
        if (master.getGovernance().getGold() < type.getCost() * count) return GameControllerOut.NOT_ENOUGH_GOLD;

        if (type.getWeaponType().equals(ResourceEnum.HORSEANDBOW)) {
            if (master.getGovernance().getResourceAmount(ResourceEnum.HORSE) == 0 ||
                    master.getGovernance().getResourceAmount(ResourceEnum.BOW) == 0)
                return GameControllerOut.NOT_ENOUGH_WEAPON;
        } else {
            if (master.getGovernance().getResourceAmount(type.getWeaponType()) == 0)
                return GameControllerOut.NOT_ENOUGH_WEAPON;
        }

        master.getGovernance().changeGold(-1 * type.getCost() * count);
        useWeapons(type, master);
        map.getTile(y, x).findYourUnits(master).get(0).addByTypeAndCount(type, count);
        return GameControllerOut.SUCCESSFULLY_CREATED_UNIT;
    }

    public static boolean[] setRecruitmentTypes(BuildingEnum type) {
        boolean[] ans = new boolean[4];
        switch (type) {
            case ENGINEERS_GUILD:
                ans[0] = true;
                ans[1] = true;
                break;
            case BARRACKS:
                ans[2] = true;
                break;
            case MERCENARY_POST:
                ans[3] = true;
                break;
        }
        return ans;
    }

    public static void useWeapons(UnitEnum soldierType, User master) {
        ResourceEnum weaponType = soldierType.getWeaponType();
        if (weaponType.equals(ResourceEnum.NULL)) return;
        if (weaponType.equals(ResourceEnum.HORSEANDBOW)) {
            master.getGovernance().changeResourceAmount(ResourceEnum.HORSE, -1);
            master.getGovernance().changeResourceAmount(ResourceEnum.BOW, -1);
            return;
        }
        master.getGovernance().changeResourceAmount(weaponType, -1);
    }
}
