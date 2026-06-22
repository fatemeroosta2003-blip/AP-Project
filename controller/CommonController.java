package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import model.ResourceEnum;
import model.units.UnitEnum;
import view.LoginMenu;
import view.controls.ShopMenuControl;
import view.enums.ProfisterControllerOut;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonController {
    public static String dataExtractor(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) return "";
        return matcher.group("wantedPart");
    }

    public static ProfisterControllerOut checkPasswordFormat(String password) {
        if (password.length() < 6)
            return ProfisterControllerOut.SHORT_PASSWORD;
        if (!password.matches(".*[A-Z].*"))
            return ProfisterControllerOut.NOT_CAPITAL_PASSWORD;
        if (!password.matches(".*[a-z].*"))
            return ProfisterControllerOut.NOT_SMALL_PASSWORD;
        if (!password.matches(".*[0-9].*"))
            return ProfisterControllerOut.NOT_NUMBERS_PASSWORD;
        if (!password.matches(".*[^a-zA-Z0-9].*"))
            return ProfisterControllerOut.NOT_SYMBOLS_PASSWORD;
        return ProfisterControllerOut.VALID;
    }

    public static ResourceEnum resourceFinder(String resource) {
        EnumSet<ResourceEnum> resourceEnums = EnumSet.allOf(ResourceEnum.class);
        for (ResourceEnum resourceEnum : resourceEnums) {
            if (resourceEnum.getName().equals(resource))
                return resourceEnum;
        }
        return ResourceEnum.NULL;
    }

    public static UnitEnum unitTypeSpecifier(String type) {
        EnumSet<UnitEnum> unitEnums = EnumSet.allOf(UnitEnum.class);
        for (UnitEnum unitEnum : unitEnums) {
            if (unitEnum.getName().equals(type))
                return unitEnum;
        }
        return null;
    }

    public static boolean nullCheck(String str) {
        return str != null && str.length() != 0 && str.trim().length() != 0;
    }
}
