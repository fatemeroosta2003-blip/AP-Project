package controller;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.User;
import view.ScanMatch;
import view.enums.ProfisterControllerOut;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordReset {
    private String inputUsername;
    private User user;
    private String newPassword;

    public PasswordReset(String inputUsername) throws NoSuchAlgorithmException {
        this.inputUsername = inputUsername;
    }

    public String resetPassword() throws NoSuchAlgorithmException {
        newPassword = encryptPassword(newPassword);
        user.setPassword(newPassword);
        changePassword(inputUsername);
        return "Your password reset successfully";
    }

    public String userExist() {

        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getUsername().equals(inputUsername)) {
                user = User.getUsers().get(i);
                return null;
            }
        }
        return "Username not found";
    }

    public String findQuestion() {
        String question;
        if (user.getSecurityQuestion() == 1) question = "What is your father’s name?";
        else if (user.getSecurityQuestion() == 2) question = "What was your first pet’s name?";
        else question = "What is your mother’s last name?";
        return question;
    }

    public boolean answerCheck(String command) {
        if (command.equals(user.getSecurityAnswer())) return true;
        else return false;
    }

    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded;
    }

    public ProfisterControllerOut checkNewPassword(boolean sign, String command) {
        ProfisterControllerOut out;
        if (newPassword == null || sign) {
            newPassword = command;
            out = checkPasswordFormat(newPassword);
            if (!out.equals(ProfisterControllerOut.VALID)) return out;
            else return null;
        } else if (!sign) {
            out = ProfisterControllerOut.RE_ENTER_PASSWORD;
            if (newPassword.equals(command)) return null;
            else return out;
        }
        return null;
    }

    public ProfisterControllerOut checkPasswordFormat(String password) {
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

    public void changePassword(String username) {
        String userInfoAddress = System.getProperty("user.dir") + "/DataBase/userInfo.json";
        Gson gson = new Gson();
        JsonArray usersList;
        JSONArray newUsersList = new JSONArray();
        try {
            usersList = gson.fromJson(new FileReader(userInfoAddress), JsonArray.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < usersList.size(); i++) {

            boolean isTheOne = false;
            JsonObject jsonObject = usersList.get(i).getAsJsonObject();
            JSONObject eachUserWithKey = new JSONObject();
            JSONObject newUserDetails = new JSONObject();
            if (jsonObject.get("user").getAsJsonObject().get("username").toString().equals("\"" + username + "\""))
                isTheOne = true;
            newUserDetails.put("nickname", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("nickname").toString()));
            newUserDetails.put("email", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("email").toString()));
            newUserDetails.put("slogan", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("slogan").toString()));
            newUserDetails.put("securityQuestion", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("securityQuestion").toString()));
            newUserDetails.put("securityAnswer", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("securityAnswer").toString()));
            newUserDetails.put("username", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("username").toString()));
            newUserDetails.put("highScore", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("highScore").toString()));
            newUserDetails.put("url", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("url").toString()));
            if (!isTheOne)
                newUserDetails.put("password", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("password").toString()));
            if (isTheOne) {
                newUserDetails.put("password", newPassword.toString());
            }
            eachUserWithKey.put("user", newUserDetails);
            newUsersList.add(eachUserWithKey);
        }

        //now, we should add the json array to our file.
        FileWriter file = null;
        try {
            file = new FileWriter(System.getProperty("user.dir") + "/DataBase/userInfo.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            file.write(newUsersList.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String correctDoubleQuotation(String input) {
        if (input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"')
            return input.substring(1, input.length() - 1);
        return input;
    }

    public PasswordReset() {
    }
}