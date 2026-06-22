package controller.modelFunctions;

import model.ResourceEnum;
import model.User;

public class BuildingFuncs {
    public void hovel(User master) {
        master.getGovernance().changeMaximumPopulation(8);
    }
    public void church(User master) {master.getGovernance().changePopularity(0);}
    public void cathedral(User master) {master.getGovernance().changePopularity(2);}
    public void killingpit(User master) {master.getGovernance().changeFearRate(1);}
    public void stable(User master) {
        master.getGovernance().changeResourceAmount(ResourceEnum.HORSE, 4);
    }
}
