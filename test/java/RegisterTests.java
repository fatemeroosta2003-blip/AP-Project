import controller.CommonController;
import controller.LoginMenuController;
import controller.RegisterMenuController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import view.enums.ProfisterControllerOut;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterTests {
    RegisterMenuController registerMenuController = new RegisterMenuController();
    @BeforeClass
    public static void settingUp() throws IOException {
        LoginMenuController.extractUserData();
    }

    @Test
    public void setUp() throws IOException {
        Assertions.assertNotEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " \"Vox machina3#\" \"Vox machina3#\" --email k.s@kj -n nick" +
                " -s testing"), ProfisterControllerOut.VALID);
        Assertions.assertEquals(registerMenuController.createUser(),ProfisterControllerOut.SUCCESSFULLY_REGISTERED.getContent());


        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " \"vox machina3#\" \"Vox machina3#\" --email -n nick" +
                " -s testing"), ProfisterControllerOut.EMPTY_FIELDS);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " \"vox machina3#\" \"Vox machina3#\" --email j@k.d -n nick" +
                " -s   "), ProfisterControllerOut.SLOGAN_AND_NO_SLOGAN);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u !ni#k -p" +
                " \"vox machina3#\" \"Vox machina3#\" --email -n nick"
                ), ProfisterControllerOut.EMPTY_FIELDS);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " vax  Vax --email j@k.d -n nick" +
                " -s   hi "), ProfisterControllerOut.SHORT_PASSWORD);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " vaxandvox  vaxandvox --email j@k.d -n nick" +
                " -s  hi "), ProfisterControllerOut.NOT_CAPITAL_PASSWORD);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " VAXANDVOX  VAXANDVOX --email j@k.d -n nick" +
                " -s  hi "), ProfisterControllerOut.NOT_SMALL_PASSWORD);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " Vaxandvox  Vaxandvox --email j@k.d -n nick" +
                " -s  hi "), ProfisterControllerOut.NOT_NUMBERS_PASSWORD);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                " Vaxandvox12  Vaxandvox12 --email j@k.d -n nick" +
                " -s  hi "), ProfisterControllerOut.NOT_SYMBOLS_PASSWORD);
//        Assertions.assertNotEquals(registerMenuController.validateBeforeCreation(" -u xepo -p" +
//                " Vaxandvox12#  Vaxandvox12# --email j@k.d -n nick" +
//                " -s   hi"), ProfisterControllerOut.VALID);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nickkk -p" +
                " Vaxandvox12#  Vaxandvox12# --email k.s@kj -n nick" +
                " -s  \"this is it, then\""), ProfisterControllerOut.EMAIL_TAKEN);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nickkk -p" +
                " Vaxandvox12#  Vaxandvox12# --email k.mamamia -n nick" +
                " -s  \"this is it, then\""), ProfisterControllerOut.EMAIL_INVALID_FORMAT);
        Assertions.assertEquals(registerMenuController.validateBeforeCreation(" -u nickkk -p" +
                " Vaxandvox12#  Vaxandvox1dsdsds2# --email kk.s@k.j -n nick" +
                " -s  \"this is it, then\""), ProfisterControllerOut.SECOND_CHANCE_WAISTED);


        Assertions.assertNotEquals(registerMenuController.validateBeforeCreation(" -u nik -p" +
                "  random --email k.s@kj -n nick   -s   random" +
                " -s testing"), ProfisterControllerOut.VALID);
        registerMenuController.handleRandomPassword();
        //Assertions.assertEquals(registerMenuController.createUser(),ProfisterControllerOut.SUCCESSFULLY_REGISTERED.getContent());
        registerMenuController.setUpUserInfo();
        registerMenuController.setUpSloganDataBase();
    }

    @Test
    public void randomGeneration() throws IOException {
        //random username:
        Assertions.assertNotEquals(registerMenuController.validateBeforeCreation(" -u testing -p" +
                " \"Vox machina3#\" \"Vox machina3#\" --email klk.s@kj -n nick" +
                " -s testing"), ProfisterControllerOut.VALID);
        //Assertions.assertEquals(registerMenuController.usernameExist(),ProfisterControllerOut.VALID.getContent());
        registerMenuController.setEmail("kmk.s@kj");
        Assertions.assertEquals(registerMenuController.createUser(),ProfisterControllerOut.SUCCESSFULLY_REGISTERED.getContent());

        //random password length:
        for(int i = 0; i < 5; i++)
        {
            Assertions.assertTrue(registerMenuController.randomPasswordGenerator().length() < 13);
        }
    }
}

