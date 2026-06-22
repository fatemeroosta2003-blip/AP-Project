package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import view.enums.ProfisterControllerOut;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeProfileController {
    private User user;
    private String dataToChange;
    private String oldPassword;
    private String newPassword;

    public ChangeProfileController(User user, String dataToChange) {
        this.user = user;
        this.dataToChange = dataToChange;
    }

    public ChangeProfileController(User user) {
        this.user = user;
    }


    public String changeUserName() {
        if (!CommonController.nullCheck(dataToChange))
            return ProfisterControllerOut.EMPTY_INPUT.getContent();

        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getUsername().equals(dataToChange)) {
                return ProfisterControllerOut.USERNAME_TAKEN.getContent();
            }
        }
        if (dataToChange.matches(".*[\\W+].*")) {
            return ProfisterControllerOut.USERNAME_INVALID_FORMAT.getContent();
        }
        changeDetail(user.getUsername(), "username", dataToChange);
        user.setUsername(dataToChange);
        return ProfisterControllerOut.SUCCESSFULLY_CHANGED_USERNAME.manipulateTheEnd(dataToChange);
    }

    public String changeNickname() {
        if (!CommonController.nullCheck(dataToChange))
            return ProfisterControllerOut.EMPTY_INPUT.getContent();

        user.setNickname(dataToChange);
        changeDetail(user.getUsername(), "nickname", dataToChange);
        return ProfisterControllerOut.SUCCESSFULLY_CHANGED_NICKNAME.manipulateTheEnd("\"" + dataToChange + "\"");
    }

    public ProfisterControllerOut checkPasswordBeforeChanging() throws NoSuchAlgorithmException {
        oldPassword = dataExtractor(dataToChange, "-o\\s+(?<wantedPart>\\S*)");
        newPassword = dataExtractor(dataToChange, "-n\\s+(?<wantedPart>\\S*)");
        String temp = oldPassword;
        oldPassword = encryptPassword(oldPassword);
        if (!user.passwordMatch(oldPassword)) {
            return ProfisterControllerOut.WRONG_PASSWORD;
        }
        ProfisterControllerOut passFormat = checkPasswordFormat(newPassword);
        if (!(passFormat.equals(ProfisterControllerOut.VALID))) return passFormat;
        if (newPassword.equals(temp)) return ProfisterControllerOut.NOT_NEW_PASSWORD;
        return ProfisterControllerOut.SUCCESSFULLY_ENTERED_PASS_TO_CHANGE_IT;
    }

    public boolean confirmationCheck(String newPasswordConfirmation) {
        return newPasswordConfirmation.equals(newPassword);
    }

    public ProfisterControllerOut changePassword() throws NoSuchAlgorithmException {
        newPassword = encryptPassword(newPassword);
        user.setPassword(newPassword);
        changeDetail(user.getUsername(), "password", newPassword);
        return ProfisterControllerOut.VALID;
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded;
    }

    public String dataExtractor(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) return "";
        return matcher.group("wantedPart");
    }

    private ProfisterControllerOut checkPasswordFormat(String password) {
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

    public String changeEmail() {
        if (!CommonController.nullCheck(dataToChange))
            return ProfisterControllerOut.EMPTY_INPUT.getContent();

        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getEmail().equalsIgnoreCase(dataToChange))
                return ProfisterControllerOut.EMAIL_TAKEN.getContent();
        }
        String regex = "^[\\w|.]+@[\\w|.]+\\.[\\w|.]+$";
        if (!Pattern.compile(regex).matcher(dataToChange).find())
            return ProfisterControllerOut.EMAIL_INVALID_FORMAT.getContent();
        user.setEmail(dataToChange);
        changeDetail(user.getUsername(), "email", dataToChange);
        return ProfisterControllerOut.SUCCESSFULLY_CHANGED_EMAIL.manipulateTheEnd(dataToChange);
    }

    public void changeSlogan() {
        user.setSlogan(dataToChange);
        changeDetail(user.getUsername(), "slogan", dataToChange);
    }

    public void removeSlogan() {
        user.setSlogan(null);
        changeDetail(user.getUsername(), "slogan", "");
    }

    public void changeDetail(String username, String toChange, String forChange) {
        //  System.out.println(toChange + ": "+ forChange);
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
            newUserDetails.put("url", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("url").toString()));
            newUserDetails.put("highScore", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("highScore").toString()));
            newUserDetails.put("password", correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("password").toString()));
            if (!isTheOne)
                newUserDetails.put("" + toChange, correctDoubleQuotation(jsonObject.get("user").getAsJsonObject().get("" + toChange).toString()));
            if (isTheOne) {
                newUserDetails.replace("" + toChange, correctDoubleQuotation(forChange.toString()));
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
        if (input.length() > 0) {
            if (input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"')
                return input.substring(1, input.length() - 1);
        }
        return input;
    }

    public ChangeProfileController() {
    }
}