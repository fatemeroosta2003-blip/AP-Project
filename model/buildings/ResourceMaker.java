package model.buildings;

import controller.modelFunctions.ResourceMakerFuncs;
import model.ResourceEnum;
import model.User;

public class ResourceMaker extends Building {
    private final ResourceEnum usedResources;
    private final ResourceEnum producedResource;
    private final ResourceEnum secondProducedOne;
    public ResourceMaker(BuildingEnum type, User owner, int direction, boolean active) {
        super(type, owner, direction, active);
        ResourceEnum[] initializing = new ResourceEnum[3];
        initializing = ResourceMakerFuncs.setInputAndOutput(type);
        this.usedResources = initializing[0];
        this.producedResource = initializing[1];
        this.secondProducedOne = initializing[2];
    }

    public void produceAfterEachTurn() {
        ResourceMakerFuncs.produceWithResources(owner.getGovernance(), type.getRate(), usedResources, producedResource, secondProducedOne);
    }
}
