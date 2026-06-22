package view.enums;

import model.Tree;

public enum TreeTypes {
    DESERT_SHRUB("desert_shrub"),
    CHERRY_PALM("cherry_palm"),
    OLIVE_TREE("olive_tree"),
    COCONUT_PALM("coconut_palm"),
    DATE_PALM("date_palm");
    private final String name;

    TreeTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
