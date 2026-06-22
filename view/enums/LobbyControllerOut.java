package view.enums;

public enum LobbyControllerOut {
    USERNAME_NOT_FOUND("Username not found"),
    PLAYER_ALREADY_IN("This user already joined lobby"),
    SUCCESSFULLY_JOINED("User joined lobby"),
    REMOVING_YOURSELF("You cannot remove yourself, because your are owner of lobby"),
    SUCCESSFULLY_REMOVED_USER(" removed successfully"),
    NO_SUCH_USER_IN_LOBBY("There isn't any user with entered username in the lobby"),
    EMPTY_INPUT("Inout cannot be null, or just spaces"),
    ;
    private String content;

    LobbyControllerOut(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String manipulateRemovingFormat(String firstHalf) {
        String secondHalf = this.content;
        return firstHalf + secondHalf;
    }
}
