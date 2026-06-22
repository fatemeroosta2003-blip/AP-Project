package view.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Commands {
    CREATE_USER("^\\s*user\\s+create\\s+(?<data>.+)$"),
    SECURITY_QUESTION_PICK("^question pick -q (?<number>\\d) -a (?<answer>.+) -c (?<answerConfirm>.+)$"),
    USER_LOGIN("^\\s*user\\s+login\\s+(?<data>.+)$"),
    PASSWORD_FORGOT("^\\s*forgot\\s+my\\s+password\\s+-u\\s+(?<username>\\S*)$"),
    CHANGE_USERNAME("^\\s*profile\\s+change\\s+-u\\s+(?<username>\\S*)$"),
    CHANGE_NICKNAME("^\\s*profile\\s+change\\s+-n\\s+\"?(?<nickname>[^\"]*)\"?$"),
    CHANGE_PASSWORD("^\\s*profile\\s+change\\s+password\\s+(?<data>.+)$"),
    EMAIL_CHANGE("^\\s*profile\\s+change\\s+-e\\s+(?<email>\\S+)$"),
    CHANGE_SLOGAN("^\\s*profile\\s+change\\s+slogan\\s+-s\\s+\"?(?<slogan>[^\"]*)\"?$"),
    REMOVE_USER("\\s*remove\\s+(?<username>\\S+)\\s*"),
    SET_TEXTURE("^\\s*settexture\\s+(?<data>.+)$"),
    SHOW_MAP("^\\s*show\\s+map\\s+(?<data>.+)$"),
    SHOW_DETAIL("^\\s*show\\s+details\\s+(?<data>.+)$"),
    MOVE_MAP("^\\s*map\\s+(?<data>.+)$"),
    CLEAR("^\\s*clear\\s+(?<data>.+)$"),
    BUY_SHOP("^\\s*sell\\s+(?<data>.+)$"),
    SELL_SHOP("^\\s*buy\\s+(?<data>.+)$"),
    TRADE("^\\s*trade\\s+(?<data>.+)$"),
    ACCEPT_TRADE("^\\s*trade\\s+accept\\s+(?<data>.+)$"),
    DROP_TREE("^\\s*droptree\\s+(?<data>.+)$"),
    DROP_ROCK("^\\s*droprock\\s+(?<data>.+)$"),
    SHOW_POP_FACTORS("^\\s*show\\s+popularity\\s+factors\\s*$"),
    SHOW_POPULARITY("^\\s*show\\s+popularity\\s*$"),
    SHOW_FOOD_RATE("^\\s*food\\s+rate\\s+show\\s*$"),
    SET_FOOD_RATE("^\\s*food\\s+rate\\s+-r\\s+(?<data>(-)?\\d)$"),
    SET_TAX_RATE("^\\s*tax\\s+rate\\s+-r\\s+(?<data>(-)?\\d)$"),
    SHOW_TAX_RATE("^\\s*tax\\s+rate\\s+show\\s*$"),
    DROP_BUILDING("^\\s*dropbuilding\\s+(?<data>.+)$"),
    DROP_UNIT("^\\s*dropunit\\s+(?<data>.+)$"),
    USER_LOGIN_STAYED("^\\s*user\\s+login\\s+-u\\s+\"?(?<username>\\S+)\"?"),
    SELECT_BUILDING("^\\s*select\\s+building\\s+(?<data>.+)$"),
    SELECT_UNIT("^\\s*select\\s+unit\\s+(?<data>.+)$"),
    SET_STATE("^\\s*set\\s+(?<data>.+)$"),
    CREATE_UNIT("^\\s*createunit\\s+(?<data>.+)$"),
    MOVE_UNIT("^\\s*move\\s+unit\\s+to\\s+(?<data>.+)$"),
    REPAIR("^\\s*repair\\s*"),
    NEXT_TURN("^\\s*next\\s+turn\\s*$"),
    SET_FEAR_RATE("^\\s*fear\\s+rate\\s+-r\\s*(?<data>(-)?\\d)$"),
    SHOW_FOOD_LIST("^\\s*show\\s+food\\s+list\\s*$"),
    PATROL_UNIT("^\\s*patrol\\s*unit\\s+(?<data>.+)$"),
    ATTACK("^\\s*attack(?<data>.+)$"),
    DISBAND_UNIT("^\\s*disband\\s*unit\\s*$"),
    STOP_PETROL("^\\s*stop\\s+petrol"),
    ;
    public final String regex;

    Commands(String regex) {
        this.regex = regex;
    }

    public static Matcher getMatcher(String input, Commands commands) {
        Matcher matcher = Pattern.compile(commands.regex).matcher(input);
        if (matcher.matches())
            return matcher;
        return null;
    }
}
