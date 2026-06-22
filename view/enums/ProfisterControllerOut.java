package view.enums;

public enum ProfisterControllerOut {
    //profister = profile + register
    VALID("Your password successfully changed"),
    USERNAME_TAKEN("This username is already taken"),
    USERNAME_INVALID_FORMAT("Username's format is invalid!"),
    SUCCESSFULLY_CHANGED_USERNAME("Username changed to "),
    SUCCESSFULLY_CHANGED_NICKNAME("Nickname changed to "),
    WRONG_PASSWORD("Password is wrong"),
    SHORT_PASSWORD("Password is too short!"),
    NOT_CAPITAL_PASSWORD("Password does not include capital letters!"),
    NOT_SMALL_PASSWORD("Password does not include small letters!"),
    NOT_NUMBERS_PASSWORD("Password does not include numbers!"),
    NOT_SYMBOLS_PASSWORD("Password does not include symbols!"),
    NOT_NEW_PASSWORD("Please enter a new password!"),
    SUCCESSFULLY_ENTERED_PASS_TO_CHANGE_IT("Please enter your new password again"),
    SECOND_CHANCE_FOR_REENTERING_NEWPASSWORD("Please enter your new password again, CORRECTLY!"),
    SECOND_CHANCE_WAISTED("Failed: The confirmation was not entered correctly"),
    EMAIL_TAKEN("This email address is already used"),
    EMAIL_INVALID_FORMAT("Email format is not valid!"),
    SUCCESSFULLY_CHANGED_EMAIL("Your Email changed to "),
    EMPTY_FIELDS("Make sure to fill all the essential fields and try again!"),
    SLOGAN_AND_NO_SLOGAN("You either enter the slogan field, or you don't!"),
    SUGGESTING_USERNAME("This username is already taken.\nwould like to use "),
    SUGGESTING_PASSWORD("Your random password is: "),
    FAILED("Mission failed."),
    INVALID_INPUT_FORMAT("Failed: invalid input format"),
    INVALID_NUMBER("Failed: Question number should be from 1 to 3"),
    SUCCESSFULLY_REGISTERED("Registration successful.\nWelcome to the club, mate!"),
    INVALID_NEW_COORDINATES("Mission failed: invalid coordinates after moving"),
    RE_ENTER_PASSWORD("Please re-enter password correctly"),
    NOT_ENOUGH_RESOURCES("I'm afraid you don't have the resources necessary for this building"),
    NOT_A_VALID_PLACE("This is not the spot to put the building. Consider changing the location"),
    NOT_A_VALID_PLACE_FOR_TREES("This is not the spot to add trees. Consider changing the location"),
    NOT_A_VALID_PLACE_FOR_TROOP("This is not the spot to add a unit. Consider changing the location"),
    SUCCESSFULLY_ADDED_BUILDING("Building added successfully!"),
    UCCESSFULLY_ADDED_UNIT("Unit added successfully!"),
    REGISTER_CAPTCHA_WRONG("register was unsuccessful"),
    EMPTY_INPUT("Inout cannot be null, or just spaces"),
    CREATED_EMPTY_BUILDING("Building successfully created, but since you don't have enough workers, it is abandoned for now."),
    ONLY_ONE_GATEHOUSE("You can only have one gatehouse during the game. So take care of it!"),
    INVALID_STAIR_LOCATION("You can' place stairs there. Those should be placed near walls or gatehouses"),
    ;
    private String content;

    ProfisterControllerOut(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String manipulateRandomSlogan(String secondHalf) {
        return this.content + "\nBy the way, this is your random slogan:\n" + secondHalf;
    }

    public String manipulateTheEnd(String secondHalf) {
        return this.content + secondHalf + " successfully";
    }


    public String manipulateSuggestedUsername(String username) {
        return this.content + username + " instead?";
    }

}
