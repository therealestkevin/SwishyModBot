public class UserMessageEvent {
    private String displayName;
    private double sentiment;
    private String message;

    public UserMessageEvent(String displayName, double sentiment, String message) {
        this.displayName = displayName;
        this.sentiment = sentiment;
        this.message = message;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getSentiment() {
        return sentiment;
    }

    public void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
