package model.buildings;

import model.Resource;
import model.ResourceEnum;

public enum BuildingEnum {
    CATHEDRAL(BuildingEnumType.ForceRecruitment, 0,"cathedral", 20000, 1, 1000, new Resource(null, 0), 0),
    PITCH_DITCH(BuildingEnumType.TRAP,0, "pitch_ditch", 2000, 1, 0, new Resource(ResourceEnum.OIL, 2), 0),
    CAGED_WAR_DOGS(BuildingEnumType.TRAP, 0,"caged_war_dogs", 1000, 1, 100, new Resource(ResourceEnum.WOOD, 10), 0),
    HOPS_FARMER(BuildingEnumType.RESOURCE_MAKER,1, "hops_farmer", 1000, 1, 0, new Resource(ResourceEnum.WOOD, 15), 1),
    ENGINEERS_GUILD(BuildingEnumType.BUILDING, 0,"engineers_guild", 2000, 1, 100, new Resource(ResourceEnum.WOOD, 10), 0),
    INN(BuildingEnumType.BUILDING, 0,"inn", 2000, 1, 100, new Resource(ResourceEnum.WOOD, 20), 1),
    MILL(BuildingEnumType.RESOURCE_MAKER, 1,"mill", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 20), 3),
    PERIMETER_TOWER(BuildingEnumType.TOWER, 0,"perimeter_tower", 60000, 1, 0, new Resource(ResourceEnum.STONE, 10), 0),
    DEFENSE_TURRET(BuildingEnumType.TOWER, 0,"defense_turret", 80000, 1, 0, new Resource(ResourceEnum.STONE, 15), 0),
    KILLING_PIT(BuildingEnumType.TRAP, 0,"killing_pit", 500, 1, 0, new Resource(ResourceEnum.WOOD, 6), 0),
    SQUARE_TOWER(BuildingEnumType.TOWER, 0,"square_tower", 85000, 1, 0, new Resource(ResourceEnum.STONE, 35), 0),
    ROUND_TOWER(BuildingEnumType.TOWER, 0,"round_tower", 95000, 1, 0, new Resource(ResourceEnum.STONE, 40), 0),
    SMALL_STONE_GATEHOUSE(BuildingEnumType.GATE,0, "small_stone_gatehouse", 100000, 1, 0, new Resource(null, 0), 0),
    BIG_STONE_GATEHOUSE(BuildingEnumType.GATE, 0,"big_stone_gatehouse", 120000, 1, 0, new Resource(ResourceEnum.STONE, 20), 0),
    DRAWBRIDGE(BuildingEnumType.GATE, 0,"drawbridge", 750000, 1, 0, new Resource(ResourceEnum.WOOD, 10), 0),
    //KEEP(BuildingEnumType.BUILDING, "keep", 0, 0, new Resource(null, 0), 0),
    LOOKOUT_TOWER(BuildingEnumType.TOWER, 0,"lookout_tower", 150000, 1, 0, new Resource(ResourceEnum.STONE, 10), 0),
    CHURCH(BuildingEnumType.ForceRecruitment, 0,"church", 10000, 1, 250, new Resource(null, 0), 0),
    ARMOURY(BuildingEnumType.STORAGE, 0,"armoury", 40000, 1, 100, new Resource(ResourceEnum.WOOD, 5), 0),
    BARRACKS(BuildingEnumType.BUILDING, 0,"barrack", 10000, 1, 0, new Resource(ResourceEnum.STONE, 15), 0),
    HOVEL(BuildingEnumType.BUILDING, 0,"hovel", 1000, 1, 0, new Resource(ResourceEnum.WOOD, 6), 0),
    MERCENARY_POST(BuildingEnumType.BUILDING,0, "mercenary_post", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 10), 0),
    IRON_MINE(BuildingEnumType.RESOURCE_MAKER,1, "iron_mine", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 20), 2),
    STOCKPILE(BuildingEnumType.STORAGE, 0,"stockpile", 15000, 1, 0, new Resource(null, 0), 0),
    WOODCUTTERS(BuildingEnumType.RESOURCE_MAKER, 1,"woodcutter", 1000, 1, 0, new Resource(ResourceEnum.WOOD, 3), 1),
    OX_TETHER(BuildingEnumType.RESOURCE_MAKER, 1,"ox_tether", 500, 1, 0, new Resource(ResourceEnum.WOOD, 5), 1),
    PITCH_RIG(BuildingEnumType.RESOURCE_MAKER, 1,"pitch_rig", 500, 1, 0, new Resource(ResourceEnum.WOOD, 20), 1),
    QUARRY(BuildingEnumType.RESOURCE_MAKER, 1,"quarry", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 20), 3),
    APPLE_ORCHARD(BuildingEnumType.RESOURCE_MAKER, 1,"apple_orchard", 1000, 1, 0, new Resource(ResourceEnum.WOOD, 5), 1),
    DAIRY_FARM(BuildingEnumType.RESOURCE_MAKER, 2,"diary_farmer", 1000, 1, 0, new Resource(ResourceEnum.WOOD, 10), 1),
    WHEAT_FARM(BuildingEnumType.RESOURCE_MAKER, 1,"wheat_farm", 1000, 1, 0, new Resource(ResourceEnum.WOOD, 15), 1),
    BAKERY(BuildingEnumType.RESOURCE_MAKER, 3,"bakery", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 10), 1),
    BLACKSMITH(BuildingEnumType.RESOURCE_MAKER,2, "blacksmith", 2000, 1, 100, new Resource(ResourceEnum.WOOD, 20), 1),
    BREWER(BuildingEnumType.RESOURCE_MAKER, 2,"brewer", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 10), 1),
    GRANARY(BuildingEnumType.STORAGE, 0,"granary", 6000, 1, 0, new Resource(ResourceEnum.WOOD, 5), 0),
    STAIR(BuildingEnumType.BUILDING, 0,"stair", 500, 1, 0, new Resource(ResourceEnum.STONE, 0), 0),
    ARMOURER(BuildingEnumType.RESOURCE_MAKER, 2,"armourer", 2000, 1, 100, new Resource(ResourceEnum.WOOD, 20), 1),
    FLETCHER(BuildingEnumType.RESOURCE_MAKER, 2,"fletcher", 2000, 1, 100, new Resource(ResourceEnum.WOOD, 20), 1),
    POLE_TURNER(BuildingEnumType.RESOURCE_MAKER, 2,"pole_turner", 2000, 1, 100, new Resource(ResourceEnum.WOOD, 10), 1),
    OIL_SMELTER(BuildingEnumType.RESOURCE_MAKER, 2,"oil_smelter", 4000, 1, 100, new Resource(ResourceEnum.IRON, 10), 1),
    SMALL_WALL(BuildingEnumType.BUILDING, 0,"small_wall", 20000, 0, 4000,new Resource(ResourceEnum.STONE, 5), 0),
    BIG_WALL(BuildingEnumType.BUILDING, 0,"big_wall", 40000, 0, 4000,new Resource(ResourceEnum.STONE, 15), 0),
    HUNTERS_POST(BuildingEnumType.RESOURCE_MAKER, 1,"hunter_post", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 5), 1),
    SIEGE_TENT(BuildingEnumType.BUILDING, 0,"siege_tent", 2000, 1, 0, new Resource(null, 0), 0),
    STABLE(BuildingEnumType.BUILDING, 0,"stable", 2000, 1, 400, new Resource(ResourceEnum.WOOD, 20), 0),
    MARKET(BuildingEnumType.BUILDING, 0,"market", 2000, 1, 0, new Resource(ResourceEnum.WOOD, 5), 1),
    BATTERING_RAM(BuildingEnumType.BUILDING, 0,"battering_ram", 2000, 0, 0, new Resource(ResourceEnum.STONE, 10), 0),
    ;
    private BuildingEnumType type;
    private String name;
    private int originalHp;
    private int rate;
    private int goldCost;
    private int worker;
    private Resource resource;
    private int wave;

    BuildingEnum(BuildingEnumType type,  int wave, String name, int hp, int rate, int goldCost, Resource resource, int worker) {
        this.type = type;
        this.name = name;
        this.originalHp = hp;
        this.goldCost = goldCost;
        this.resource = resource;
        this.worker = worker;
        this.rate = rate;
        this.wave = wave;
    }

    public String getName() {
        return name;
    }

    public int getOriginalHp() {
        return originalHp;
    }

    public int getGoldCost() {
        return goldCost;
    }


    public int getWorker() {
        return worker;
    }

    public BuildingEnumType getType() {
        return type;
    }

    public Resource getResource() {
        return resource;
    }

    public int getRate() {
        return rate;
    }

    public int getWave() {
        return wave;
    }
}
