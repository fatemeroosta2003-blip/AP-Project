package view;

import controller.ChangeProfileController;
import controller.ShowProfileController;
import model.User;
import view.enums.Commands;
import view.enums.ProfisterControllerOut;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

public class ProfileMenu {
    private User user;

    public ProfileMenu(User user) {
        this.user = user;
    }

    public void run() throws NoSuchAlgorithmException {
        ChangeProfileController changer;
        ShowProfileController show;
        while (true) {
            String command = ScanMatch.getScanner().nextLine();
            Matcher matcher;
            if (command.equals("back")) {
                System.out.println("Your are in the main menu");
                return;
            } else if (command.matches("show current menu")) System.out.println("profile menu");
            else if ((matcher = Commands.getMatcher(command, Commands.CHANGE_USERNAME)) != null) {
                changer = new ChangeProfileController(user, matcher.group("username"));
                System.out.println(changer.changeUserName());
            } else if ((matcher = Commands.getMatcher(command, Commands.CHANGE_NICKNAME)) != null) {
                changer = new ChangeProfileController(user, matcher.group("nickname"));
                System.out.println(changer.changeNickname());
            } else if ((matcher = Commands.getMatcher(command, Commands.CHANGE_PASSWORD)) != null) {
                changer = new ChangeProfileController(user, matcher.group("data"));
                ProfisterControllerOut resultOFCheck = changer.checkPasswordBeforeChanging();
                System.out.println(resultOFCheck.getContent());

                if (resultOFCheck.equals(ProfisterControllerOut.SUCCESSFULLY_ENTERED_PASS_TO_CHANGE_IT)) {
                    command = ScanMatch.getScanner().nextLine();
                    if (changer.confirmationCheck(command))
                        System.out.println(changer.changePassword().getContent());
                    else {
                        System.out.println(ProfisterControllerOut.SECOND_CHANCE_FOR_REENTERING_NEWPASSWORD.getContent());
                        command = ScanMatch.getScanner().nextLine();
                        if (changer.confirmationCheck(command))
                            System.out.println(changer.changePassword().getContent());
                        else System.out.println(ProfisterControllerOut.SECOND_CHANCE_WAISTED.getContent());
                    }
                }
            } else if ((matcher = Commands.getMatcher(command, Commands.EMAIL_CHANGE)) != null) {
                changer = new ChangeProfileController(user, matcher.group("email"));
                System.out.println(changer.changeEmail());
            } else if ((matcher = Commands.getMatcher(command, Commands.CHANGE_SLOGAN)) != null) {
                changer = new ChangeProfileController(user, matcher.group("slogan"));
                changer.changeSlogan();
                System.out.println("Your slogan changed successfully");
            } else if (command.matches("\\s*Profile\\s+remove\\s+slogan\\s*")) {
                changer = new ChangeProfileController(user);
                changer.removeSlogan();
                System.out.println("Your slogan cleared successfully");
            } else if (command.matches("\\s*profile\\s+display\\s+highscore\\s*")) {
                show = new ShowProfileController(user);
                System.out.println(show.showScore());
            } else if (command.matches("\\s*profile\\s+display\\s+rank\\s*")) {
                show = new ShowProfileController(user);
                System.out.println(show.showRank());
            } else if (command.matches("\\s*profile\\s+display\\s+slogan\\s*")) {
                show = new ShowProfileController(user);
                System.out.println(show.showSlogan());
            } else if (command.matches("\\s*profile\\s+display\\s*")) {
                show = new ShowProfileController(user);
                System.out.println(show.showDisplay());
            } else System.out.println("invalid command");
        }
    }
}
