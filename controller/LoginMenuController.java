package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Captcha;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import view.*;
import view.enums.LoginControllerOut;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Pattern;


public class LoginMenuController {
    private String data;
    private String inputUsername;
    private String inputPassword;
    private int userTryLogin;
    private User user;
    private boolean stayLogin = false;
    private static User userStayLogin;
    private CaptchaMenu captchaMenu = new CaptchaMenu();

    public LoginMenuController(String data) throws IOException {
        this.data = data;
        extractData();
    }

    public LoginMenuController() {
    }

    private void extractData() {
        inputUsername = CommonController.dataExtractor(data, "((?<!\\S)-u\\s+(?<wantedPart>(\"[^\"]*\")|[^-\\s]\\S*)(?<!\\s))").trim();
        inputPassword = CommonController.dataExtractor(data, "((?<!\\S)-p\\s+(?<wantedPart>(\"[^\"]*\")|[^-\\s]\\S*)(?<!\\s))").trim();
        if ((Pattern.compile("--stay-logged-in").matcher(data).find())) {
            stayLogin = true;
        }
    }

    public LoginControllerOut checkForLogin() {
        if (!userExist())
            return LoginControllerOut.USERNAME_NOT_FOUND;
        if (!passwordMatch())
            return LoginControllerOut.PASSWORD_WRONG;
        if (!captchaMenu.run())
            return LoginControllerOut.LOGIN_CAPTCHA_WRONG;
        return LoginControllerOut.VALID;
    }

    public void mainMenuRun() throws NoSuchAlgorithmException, IOException {
        if (stayLogin) {
            userStayLogin = user;
            saveUserStayed(userStayLogin);
        }
        MainMenu mainMenu = new MainMenu(user);
        mainMenu.run();
    }

    public void mainMenuRunStayed(User user) throws NoSuchAlgorithmException, IOException {
        MainMenu mainMenu = new MainMenu(user);
        mainMenu.run();
    }

    public void giveAnotherShot(int timeOut, Scanner scanner) {
        long startTime = System.currentTimeMillis() / 1000;
        while (true) {
            long timeNow = System.currentTimeMillis() / 1000;
            inputPassword = scanner.nextLine();
            if ((timeNow - startTime) > timeOut) {
                return;
            } else {
                System.out.println("You have to wait");
            }

        }
    }

    public boolean userExist() {

        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getUsername().equals(inputUsername)) {
                user = User.getUsers().get(i);
                return true;
            }
        }
        return false;
    }

    public boolean passwordMatch() {
        try {
            inputPassword = encryptPassword(inputPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (user.passwordMatch(inputPassword)) return true;
        return false;
    }

    public static void extractUserData() throws FileNotFoundException {
        //address here is: System.getProperty("user.dir") + "/DataBase/userInfo.json"
        String address = System.getProperty("user.dir") + "/DataBase/userInfo.json";
        if (new FileReader(address) == null) return;
        Gson gson = new Gson();
        JsonArray jsonArray = null;
        try {
            jsonArray = gson.fromJson(new FileReader(address), JsonArray.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (jsonArray != null)
            for (int i = 0; i < jsonArray.size(); i++) {
                if (i > 0) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    String password = jsonObject.get("user").getAsJsonObject().get("password").toString().replaceAll("\"", "");
                    int securityQuestion = Integer.parseInt(jsonObject.get("user").getAsJsonObject().get("securityQuestion").toString().replaceAll("\"", ""));
                    String securityAnswer = jsonObject.get("user").getAsJsonObject().get("securityAnswer").toString().replaceAll("\"", "");
                    String nickname = jsonObject.get("user").getAsJsonObject().get("nickname").toString().replaceAll("\"", "");
                    String slogan = jsonObject.get("user").getAsJsonObject().get("slogan").toString().replaceAll("\"", "");
                    String email = jsonObject.get("user").getAsJsonObject().get("email").toString().replaceAll("\"", "");
                    String username = jsonObject.get("user").getAsJsonObject().get("username").toString().replaceAll("\"", "");
                    String url = jsonObject.get("user").getAsJsonObject().get("url").toString().replaceAll("\"", "");
                    int highScore = Integer.parseInt(jsonObject.get("user").getAsJsonObject().get("highScore").toString().replaceAll("\"", ""));
                    User addingUser = new User(username, password, nickname, email, slogan, securityQuestion, securityAnswer, highScore);
                    addingUser.setUrl(url);
                    addingUser.addUserToArrayList();
                }
            }
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded;
    }

    public static User getUserStayLogin() {
        return userStayLogin;
    }

    public static void setUpStayedLogin() throws IOException {
        File userInfo = new File(System.getProperty("user.dir") + "/DataBase/stayed.json");
        if (!userInfo.exists()) {
            userInfo.createNewFile();
        } else {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/DataBase/stayed.json"));
            if (br.readLine() != null) {
                return;
            }
        }

        JSONObject userDetails = new JSONObject();
        userDetails.put("username", "");
        JSONObject userObject = new JSONObject();
        userObject.put("user", userDetails);

        JSONArray userList = new JSONArray();
        userList.add(userObject);

        FileWriter file = new FileWriter(System.getProperty("user.dir") + "/DataBase/stayed.json");
        try {
            file.write(userList.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUserStayed(User user) throws IOException {
        JSONArray usersList = readFromAJson(System.getProperty("user.dir") + "/DataBase/stayed.json");
        JSONObject userDetails = new JSONObject();
        userDetails.put("username", user.getUsername());
        // System.out.printf(userStayLogin.getUsername());
        JSONObject eachUserAsObject = new JSONObject();
        eachUserAsObject.put("user", userDetails);
        //Add the new onw to the list
        usersList.add(eachUserAsObject);

        //now, we should add the json array to our file.
        FileWriter file = new FileWriter(System.getProperty("user.dir") + "/DataBase/stayed.json");
        try {
            file.write(usersList.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file.close();
    }

    public static JSONArray readFromAJson(String address) {
        JSONArray userList = new JSONArray();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(address)) {
            Object obj = parser.parse(reader);
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray)
                userList.add(o);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public static User checkStayedLogin() throws FileNotFoundException {
        String address = System.getProperty("user.dir") + "/DataBase/stayed.json";
        if (new FileReader(address) == null) return null;
        Gson gson = new Gson();
        JsonArray jsonArray = null;
        try {
            jsonArray = gson.fromJson(new FileReader(address), JsonArray.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String username = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            if (i > 0) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                username = jsonObject.get("user").getAsJsonObject().get("username").toString().replaceAll("\"", "");
            }
        }
        User user = null;
        for (int k = 0; k < User.getUsers().size(); k++) {
            if (User.getUsers().get(k).getUsername().equals(username)) {
                user = User.getUsers().get(k);
                break;
            }
        }
        return user;
    }

    public static void clearStayed() throws IOException {
        JSONArray usersList = readFromAJson(System.getProperty("user.dir") + "/DataBase/stayed.json");
        JSONObject userDetails = new JSONObject();
        userDetails.put("username", "");
        // System.out.printf(userStayLogin.getUsername());
        JSONObject eachUserAsObject = new JSONObject();
        eachUserAsObject.put("user", userDetails);
        //Add the new onw to the list
        usersList.add(eachUserAsObject);

        //now, we should add the json array to our file.
        FileWriter file = new FileWriter(System.getProperty("user.dir") + "/DataBase/stayed.json");
        try {
            file.write(usersList.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file.close();
    }
    public boolean passwordMatch(String username, String inputPassword) {
        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getUsername().equals(username)) {
                user = User.getUsers().get(i);
            }
        }
        if (user==null) return false;
        try {
            inputPassword = encryptPassword(inputPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (user.passwordMatch(inputPassword)) return true;
        return false;
    }

    public User getUser() {
        return user;
    }
}