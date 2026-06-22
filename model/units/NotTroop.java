package model.units;

import model.User;

class NotTroop extends Unit {
    public NotTroop(User master, UnitEnum type, int count, int primaryY, int primaryX) {
        super(master, type, count, primaryY, primaryX);
    }
}
