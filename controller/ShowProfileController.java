package controller;

import model.User;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ShowProfileController {
    private User user;

    public ShowProfileController(User user) {
        this.user = user;
    }

    public String showRank() {
        ArrayList<User> users = User.getUsers();
        Collections.sort(users, new Comparator<User>() {
            public int compare(User a, User b) {
                return b.getHighScore() - a.getHighScore();
            }
        });
        Collections.sort(users, new Comparator<User>() {
            public int compare(User a, User b) {
                if (a.getHighScore() == b.getHighScore())
                    return a.getUsername().compareTo(b.getUsername());
                return 0;
            }
        });
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                return "Your rank is " + (i + 1);
            }
        }
        return null;
    }

    public String showScore() {
        return "Your highest score is " + user.getHighScore();
    }

    public String showSlogan() {
        if (user.getSlogan() == null || user.getSlogan().length() == 0) return "Your slogan is empty";
        else return "Your slogan is : " + user.getSlogan();
    }

    public String showDisplay() {
        String ans = "";
        ans += "username : " + user.getUsername() + "\n";
        ans += "nickname : " + user.getNickname() + "\n";
        ans += "email : " + user.getEmail() + "\n";
        ans += "highest score : " + user.getHighScore() + "\n";
        if (user.getSlogan() != null && user.getSlogan().length() != 0) ans += "slogan : " + user.getSlogan() + "\n";
        ans += "your rank : " + getRank();
        return ans;
    }

    public int getRank() {
        ArrayList<User> users = User.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getHighScore() == -1) {
                users.remove(i);
                break;
            }
        }
        Collections.sort(users, new Comparator<User>() {
            public int compare(User a, User b) {
                return b.getHighScore() - a.getHighScore();
            }
        });
        Collections.sort(users, new Comparator<User>() {
            public int compare(User a, User b) {
                if (a.getHighScore() == b.getHighScore())
                    return a.getUsername().compareTo(b.getUsername());
                return 0;
            }
        });
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                return (i + 1);
            }
        }
        return 0;
    }
}
