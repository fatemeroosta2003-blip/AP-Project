package model;

public enum TileTexture {

    SMALL_POND("small_pond", false, false, false, TileGraphicTexture.SEA),
    SEA("sea", false, false, false, TileGraphicTexture.SEA),
    FORD("ford", false, false, true, TileGraphicTexture.SEA),
    IRON("iron", true, false, true, TileGraphicTexture.LAND),
    EARTH("earth", true, true, true, TileGraphicTexture.LAND),
    SCRUB("scrub", true, false, true, TileGraphicTexture.TREE),
    THICK_SCRUB("thick_scrub", true, false, true, TileGraphicTexture.TREE),
    OIL("oil", false, false, true, TileGraphicTexture.LAND),
    BIG_POND("big_pond", false, false, false, TileGraphicTexture.SEA),
    RIVER("river", false, false, false, TileGraphicTexture.SEA),
    SAND("sand", false, false, true, TileGraphicTexture.LAND),
    LAWN("lawn", true, true, true, TileGraphicTexture.TREE),
    ROCK("rock", false, false, false, TileGraphicTexture.LAND),
    //todo use the last 5 in the map
    ;
    final boolean fertility;
    final boolean constructiblity;
    final boolean walkability;
    final TileGraphicTexture tileGraphicTexture;

    TileTexture(String name, boolean fertility, boolean constructiblity, boolean walkability, TileGraphicTexture tileGraphicTexture) {
        this.name = name;
        this.fertility = fertility;
        this.constructiblity = constructiblity;
        this.walkability = walkability;
        this.tileGraphicTexture = tileGraphicTexture;
    }

    String name;

    public boolean isFertility() {
        return fertility;
    }

    public boolean isConstructiblity() {
        return constructiblity;
    }

    public boolean isWalkability() {
        return walkability;
    }

    public String getName() {
        return name;
    }

    public TileGraphicTexture getTileGraphicTexture() {return tileGraphicTexture;}
}
