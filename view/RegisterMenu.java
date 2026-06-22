package view;

import controller.LoginMenuController;
import controller.PasswordReset;
import controller.RegisterMenuController;
import model.User;
import view.enums.Commands;
import view.enums.LoginControllerOut;
import view.enums.ProfisterControllerOut;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

public class RegisterMenu {

    RegisterMenuController registerMenuController = new RegisterMenuController();

    public void run() throws IOException, NoSuchAlgorithmException {
        registerMenuController.setUpSloganDataBase();
        registerMenuController.setUpUserInfo();
        LoginMenuController.setUpStayedLogin();
        LoginMenuController.extractUserData();
        if (stayLogin()) {
            MainMenu mainMenu = new MainMenu(LoginMenuController.checkStayedLogin());
            mainMenu.run();
        }
        //  checkForStayed();
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            Matcher matcher;
            if (command.equals("exit")) break;
            else if ((matcher = Commands.getMatcher(command, Commands.CREATE_USER)) != null) {
                System.out.println(createUser(matcher.group("data")));
            } else if (command.matches("show current menu")) System.out.println("login menu");

            else if ((matcher = Commands.getMatcher(command, Commands.USER_LOGIN)) != null) {
                LoginMenuController loginMenuController = new LoginMenuController(matcher.group("data"));
                LoginControllerOut result = loginMenuController.checkForLogin();
                System.out.println(result.getContent());

                if (result.equals(LoginControllerOut.VALID))
                    loginMenuController.mainMenuRun();

                else if (result.equals(LoginControllerOut.PASSWORD_WRONG)) {
                    int timeOut = 5;
                    while (timeOut <= 320) {
                        System.out.println("You can try again in " + timeOut + " seconds");
                        loginMenuController.giveAnotherShot(timeOut, ScanMatch.getScanner());
                        if (loginMenuController.passwordMatch()) {
                            loginMenuController.mainMenuRun();
                            break;
                        }
                        timeOut += 5;
                    }
                    if (timeOut > 320)
                        System.out.println("Login failed: Password is wrong!");
                } else System.out.println("Login failed");
            } else if ((matcher = Commands.getMatcher(command, Commands.PASSWORD_FORGOT)) != null) {
                reset(matcher);
            } else if ((matcher = Commands.getMatcher(command, Commands.USER_LOGIN_STAYED)) != null) {
                if (LoginMenuController.getUserStayLogin().getUsername().equals(matcher.group("username"))) {
                    LoginMenuController loginMenuController = new LoginMenuController(null);
                    loginMenuController.mainMenuRunStayed(LoginMenuController.getUserStayLogin());
                }
            } else {
                System.out.println("invalid command");
            }
        }
    }

    public String createUser(String data) throws IOException {
        Matcher temp;
        ProfisterControllerOut result = registerMenuController.validateBeforeCreation(data);
        if (!result.equals(ProfisterControllerOut.VALID)) return result.getContent();
        String tempResult = registerMenuController.usernameExist("not graphic");
        if (!tempResult.equals(ProfisterControllerOut.VALID.getContent())) {
            System.out.println(tempResult);
            String respondToChangeUsername = ScanMatch.getScanner().nextLine().trim();
            if (!respondToChangeUsername.equals("y"))
                return ProfisterControllerOut.FAILED.getContent();
        }
        result = registerMenuController.handleRandomPassword();
        if (!result.equals(ProfisterControllerOut.VALID)) {
            System.out.println(result.getContent() + "\n" + registerMenuController.getPassword());
            System.out.println("Please write it here:");
            int counter = 9;
            while (true) {
                if (!ScanMatch.getScanner().nextLine().trim().equals(registerMenuController.getPassword()))
                    System.out.println("Wrong. Tries left: " + counter + "\nPlease enter your password: " + registerMenuController.getPassword());
                else break;
                counter--;
                if (counter == 0) return ProfisterControllerOut.FAILED.getContent();
            }
        }
        System.out.println("Pick your security question: 1. What is my father’s name? 2. What" +
                "was my first pet’s name? 3. What is my mother’s last name?\n" +
                "Your response should in form:\n" +
                "question pick -q <question-number> -a <answer> -c <answerconfirm>");
        if ((temp = Commands.getMatcher(ScanMatch.getScanner().nextLine(), Commands.SECURITY_QUESTION_PICK)) == null)
            return ProfisterControllerOut.FAILED.getContent();
        result = registerMenuController.getSecurityQuestion(temp);
        if (!result.equals(ProfisterControllerOut.VALID))
            return result.getContent();
        CaptchaMenu captchaMenu = new CaptchaMenu();
        if (!captchaMenu.run()) {
            return ProfisterControllerOut.REGISTER_CAPTCHA_WRONG.getContent();
        }
        return registerMenuController.createUser();
    }

    public void reset(Matcher matcher) throws NoSuchAlgorithmException {
        String out;
        PasswordReset passwordReset = new PasswordReset(matcher.group("username").trim());
        if ((out = passwordReset.userExist()) != null) {
            System.out.println(out);
            return;
        }
        String question = passwordReset.findQuestion();
        System.out.println(question);
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            if (passwordReset.answerCheck(command)) break;
            else System.out.println("Your answer is wrong. Please enter another answer.");
        }
        System.out.println("Please enter new password");
        ProfisterControllerOut out1;
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            out1 = passwordReset.checkNewPassword(true, command);
            if (out1 != null) {
                System.out.println(out1.getContent());
            } else break;
        }
        System.out.println("Please re-enter new password");
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            out1 = passwordReset.checkNewPassword(false, command);
            if (out1 != null) {
                System.out.println(out1.getContent());
            } else break;
        }
        CaptchaMenu captchaMenu = new CaptchaMenu();
        if (!captchaMenu.run()) {
            System.out.println("password reset was unsuccessful");
            return;
        }
        System.out.println(passwordReset.resetPassword());
    }

    public static boolean stayLogin() throws IOException, NoSuchAlgorithmException {
        User temp;
        if ((temp = LoginMenuController.checkStayedLogin()) != null) {
            return true;
        }
        ;
        return false;
    }
    //public void checkForStayed()
}

