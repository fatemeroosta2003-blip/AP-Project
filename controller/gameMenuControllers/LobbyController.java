package controller.gameMenuControllers;

import controller.CommonController;
import model.Governance;
import model.User;
import view.enums.GameControllerOut;
import view.enums.LobbyControllerOut;
import view.enums.ProfisterControllerOut;

import java.util.ArrayList;

public class LobbyController {
    private String username;
    private User user;
    private User host;
    private ArrayList<User> players = new ArrayList<>();

    public LobbyController(User user) {
        this.host = user;
        players.add(user);
    }

    public User getHost() {
        return host;
    }

    public LobbyControllerOut getUser(String user) {
        this.username = user;
        return addUser();
    }

    private LobbyControllerOut addUser() {

        if (!CommonController.nullCheck(username))
            return LobbyControllerOut.EMPTY_INPUT;

        username = username.trim();
        if (!existUser()) {
            return LobbyControllerOut.USERNAME_NOT_FOUND;
        }
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(user)) {
                return LobbyControllerOut.PLAYER_ALREADY_IN;
            }
        }
        players.add(user);
        return LobbyControllerOut.SUCCESSFULLY_JOINED;
    }

    private boolean existUser() {
        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getUsername().equals(username)) {
                user = User.getUsers().get(i);
                return true;
            }
        }
        return false;
    }

    public boolean startGame() {
        if (players.size() > 1 && players.size() < 9) {
            Governance.setEmpires(players);
            return true;
        }
        return false;
    }

    public String removeUser(String user) {
        if (!CommonController.nullCheck(user))
            return LobbyControllerOut.EMPTY_INPUT.getContent();

        user = user.trim();
        username = user;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUsername().equals(username)) {
                if (i == 0) {
                    return LobbyControllerOut.REMOVING_YOURSELF.getContent();
                }
                players.remove(i);
                return LobbyControllerOut.SUCCESSFULLY_REMOVED_USER.manipulateRemovingFormat(username);
            }
        }
        return LobbyControllerOut.NO_SUCH_USER_IN_LOBBY.getContent();
    }

    public String showUsers() {
        StringBuilder ans = new StringBuilder(new String());
        for (int i = 0; i < players.size(); i++) {
            ans.append(i + 1).append(". ").append(players.get(i).getUsername()).append("\n");
        }
        return ans.toString();
    }
}
