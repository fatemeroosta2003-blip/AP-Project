package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradeItem {

    private String lastDateUpdate;
    private String id;
    private User oneWhoRequests;
    private User oneWhoAnswersTheCall;
    private ResourceEnum type;
    private int amount;
    private int price;
    private String message;
    private Boolean active;
    private Boolean seenRequester = false;
    private Boolean seenAccepter = false;
    private Boolean accepted = false;

    private boolean donation;

    private void updateDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm: ");
        lastDateUpdate = now.format(formatter);
    }

    public TradeItem(String id, User oneWhoAnswersTheCall, User oneWhoRequests, ResourceEnum type, int amount, int price, String message, Boolean active, boolean donation) {
        this.id = id;
        this.oneWhoRequests = oneWhoRequests;
        this.type = type;
        this.amount = amount;
        this.price = price;
        this.message = message;
        this.active = active;
        this.donation = donation;
        this.oneWhoAnswersTheCall = oneWhoAnswersTheCall;
        updateDate();
    }

    public String getId() {
        return id;
    }

    public User getOneWhoRequests() {
        return oneWhoRequests;
    }

    public User getOneWhoAnswersTheCall() {
        return oneWhoAnswersTheCall;
    }

    public void setOneWhoAnswersTheCall(User oneWhoAnswersTheCall) {
        this.oneWhoAnswersTheCall = oneWhoAnswersTheCall;
        updateDate();
    }

    public String getTypeName() {
        return this.type.getName();
    }

    public ResourceEnum getType() {
        return this.type;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        updateDate();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        updateDate();
    }

    public void setSeenRequester(Boolean notified) {
        seenRequester = notified;
    }

    public Boolean getSeenRequester() {
        return seenRequester;
    }

    public Boolean getSeenAccepter() {
        return seenAccepter;
    }

    public void setSeenAccepter(Boolean seenAccepter) {
        this.seenAccepter = seenAccepter;
    }

    public String getLastDateUpdate() {
        return lastDateUpdate;
    }

    public Boolean getAccepted() {return accepted;}

    public void setAccepted(Boolean accepted) {this.accepted = accepted;}

    public User getTheOtherUser(User currentUser) {
        if(getOneWhoAnswersTheCall().getUsername().equals(currentUser.getUsername()))
            return getOneWhoRequests();
        else
            return getOneWhoAnswersTheCall();
    }

    public boolean isDonation() {return donation;}
}
