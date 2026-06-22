package view.enums;

public enum LoginControllerOut {
    VALID("User logged in successfully!\nYou are in the main menu"),
    USERNAME_NOT_FOUND("Username not found!"),
    PASSWORD_WRONG("Password is wrong!"),
    PASSWORD_SHORT("Password is too short!"),
    PASSWORD_NOT_CAPITAL("Password does not include capital letters!"),
    PASSWORD_NOT_SMALL("Password does not include small letters!"),
    PASSWORD_NOT_NUMBERS("Password does not include numbers!"),
    PASSWORD_NOT_SYMBOLS("Password does not include symbols!"),
    LOGIN_CAPTCHA_WRONG("Login was unsuccessful");
    private final String content;

    LoginControllerOut(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
