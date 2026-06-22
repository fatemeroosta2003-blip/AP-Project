package model;

import view.enums.TreeTypes;

public class Tree {
    private final TreeTypes type;

    public Tree(TreeTypes type) {
        this.type = type;
    }

    public TreeTypes getType() {
        return type;
    }

}
