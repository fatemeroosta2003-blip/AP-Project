import controller.LoginMenuController;
import controller.PasswordReset;
import controller.RegisterMenuController;
import controller.ShowProfileController;
import org.apache.commons.io.IOUtils;
import model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import view.RegisterMenu;
import view.enums.LoginControllerOut;
import view.enums.ProfisterControllerOut;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.security.NoSuchAlgorithmException;

public class TestClass {
    static RegisterMenuController registerMenuController = new RegisterMenuController();
    private static User user;

    public TestClass() throws NoSuchAlgorithmException {
    }

    @BeforeClass
    public static void settingUp() throws IOException {
        LoginMenuController.extractUserData();
        LoginMenuController.setUpStayedLogin();
        for (int i=0; i<User.getUsers().size(); i++){
            if (User.getUsers().get(i).getUsername().equals("epo3")) {
                user = User.getUsers().get(i);
                break;
            }
        }
    }

    //test for login
    @Test
    public void loginUserExist() throws IOException {
        LoginMenuController l = new LoginMenuController("user login -u epo3 -p abC12$");
        Assertions.assertTrue(l.userExist());
        l = new LoginMenuController("user login -u mmmmmm -p efe");
        Assertions.assertEquals(LoginControllerOut.USERNAME_NOT_FOUND, l.checkForLogin());
    }
    @Test
    public void loginUserPasswordMatch() throws IOException {
        LoginMenuController l = new LoginMenuController("user login -u epo3 -p abC12$");
        l.userExist();
        Assertions.assertTrue(l.passwordMatch());
        l = new LoginMenuController("user login -u epo3 -p wefg");
        l.userExist();
        l.passwordMatch();
        Assertions.assertEquals(LoginControllerOut.PASSWORD_WRONG, l.checkForLogin());


    }

    @Test
    public void checkStayedLogin() throws IOException, NoSuchAlgorithmException {
        LoginMenuController l = new LoginMenuController("user login -u epo3 -p abC12$");
        l.userExist();
        l.saveUserStayed(l.getUser());
        Assertions.assertTrue(RegisterMenu.stayLogin());
    }


    //test for show profile
    @Test
    public void showRank(){
        ShowProfileController s = new ShowProfileController(user);
        Assertions.assertEquals("Your rank is "+s.getRank(), s.showRank());
    }
    @Test
    public void showScore(){
        ShowProfileController s = new ShowProfileController(user);
        Assertions.assertEquals("Your highest score is "+user.getHighScore(), s.showScore());
    }
    @Test
    public void showSlogan(){
        ShowProfileController s = new ShowProfileController(user);
        if (user.getSlogan()==null)
            Assertions.assertEquals("Your slogan is empty", s.showSlogan());
        else
            Assertions.assertEquals("Your slogan is : "+user.getSlogan(), s.showSlogan());
    }
    @Test
    public void showDisplay() throws IOException {
        ShowProfileController s = new ShowProfileController(user);
        String ans = "";
        ans += "username : epo3\n";
        ans += "nickname : epo3\n";
        ans += "email : epo3@gmail.com\n";
        ans += "highest score : "+ user.getHighScore()+"\n";
        if (user.getSlogan() != null && user.getSlogan().length() != 0) ans += "slogan : "+ user.getSlogan()+"\n";
        ans += "your rank : "+ s.getRank();
        Assertions.assertEquals(ans, s.showDisplay());
    }

    //test for reset password
    PasswordReset p;
    @Test
    public void resetUserExist() throws NoSuchAlgorithmException {
        p = new PasswordReset("epo3");
        Assertions.assertNull(p.userExist());
        p = new PasswordReset("1111111111");
        Assertions.assertNotNull(p.userExist());
    }
    @Test
    public void findQuestionAndAnswer() throws NoSuchAlgorithmException{
        p = new PasswordReset("epo3");
        p.userExist();
        Assertions.assertEquals("What is your father’s name?", p.findQuestion());
        Assertions.assertTrue(p.answerCheck("dad"));
        Assertions.assertFalse(p.answerCheck("111"));
    }
    @Test
    public void checkPasswordFormat() throws NoSuchAlgorithmException {
        p=new PasswordReset("epo3");
        Assertions.assertEquals(ProfisterControllerOut.SHORT_PASSWORD, p.checkPasswordFormat("1"));
        Assertions.assertEquals(ProfisterControllerOut.NOT_CAPITAL_PASSWORD, p.checkPasswordFormat("aaaaaaaaaa"));
        Assertions.assertEquals(ProfisterControllerOut.NOT_SMALL_PASSWORD, p.checkPasswordFormat("BBBBBBBBB"));
        Assertions.assertEquals(ProfisterControllerOut.NOT_NUMBERS_PASSWORD, p.checkPasswordFormat("aBaaaaaaa"));
        Assertions.assertEquals(ProfisterControllerOut.NOT_SYMBOLS_PASSWORD, p.checkPasswordFormat("aB111111111"));
        Assertions.assertEquals(ProfisterControllerOut.VALID, p.checkPasswordFormat("abC12$"));
    }

/*    @Test
    @DisplayName("Correcting the input Strings with extra double quotes.")
    public void correctingDoubleQuotes() {
        String testing = new String();
        testing = "\"jhkjhh\"";
//        Assertions.assertThrows(NullPointerException.class, new Executable() {
//            @Override
//            public void execute() throws Throwable {
//
//            }
//        })

        Assertions.assertEquals(registerMenuController.correctDoubleQuotation(testing),"\"jhkjhh\"");
        testing = "";
        Assertions.assertEquals(registerMenuController.correctDoubleQuotation(testing),"");
        testing = "\"jhkj  hh\"";
        Assertions.assertEquals(registerMenuController.correctDoubleQuotation(testing),"jhkj  hh");
        testing = "jhkj  hh";
        Assertions.assertEquals(registerMenuController.correctDoubleQuotation(testing),"jhkj  hh");

    }
    //auto generate equals

    @Test
    public void checkRandomPassword() throws IOException {
        RegisterMenu registerMenu = new RegisterMenu();
        RegisterMenuController registerMenuController1 = new RegisterMenuController();
        //registerMenuController1.validateBeforeCreation("-u username -p password password -email nik_m@yahoo.com -n nik -s nope");
        registerMenu.createUser("-u username -p password password -email nik_m@yahoo.com -n nik -s nope");
        registerMenu.createUser("-u username -p password password -email nik_m@yahoo.com -n nik -s nope");
        registerMenu.createUser("-u username -p password password -email nik_m@yahoo.com -n nik -s nope");
        String xml = new String();
        if ((this.getClass().getResourceAsStream(System.getProperty("user.dir") + "/DataBase/userInfo.json")) != null)
        xml = IOUtils.toString(
                Objects.requireNonNull(this.getClass().getResourceAsStream(System.getProperty("user.dir") + "/DataBase/userInfo.json")),
                StandardCharsets.UTF_8
        );
        else xml ="this is empty";
        System.out.println(xml);
    }

*/


}
