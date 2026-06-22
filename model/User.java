package model;

import java.util.ArrayList;

public class User {
    private Governance governance;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String slogan;
    private int securityQuestion;
    private String securityAnswer;
    private int highScore;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private static ArrayList<User> users = new ArrayList<>();

    public User(String Username,
                String Password,
                String Nickname,
                String Email,
                String Slogan,
                int SecurityQuestion,
                String SecurityAnswer,
                int HighScore) {
        username = Username;
        password = Password;
        nickname = Nickname;
        email = Email;
        slogan = Slogan;
        securityQuestion = SecurityQuestion;
        securityAnswer = SecurityAnswer;
        highScore = HighScore;
        governance = new Governance();
    }

    public Governance getGovernance() {
        return governance;
    }

    public void addUserToArrayList() {
        User.users.add(this);
    }


    public static ArrayList<User> getUsers() {
        return users;
    }

    public String getUsername() {
        return username;
    }

    public boolean passwordMatch(String password) {
        if (this.password.equals(password)) return true;
        return false;
    }

    public int getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public void setSecurityQuestion(int securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public static void resetUsers() {
        User.users = new ArrayList<>();
    }
}
