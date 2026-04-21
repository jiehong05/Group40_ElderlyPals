package my.edu.utar.group40_elderlypals;

public class VoiceAction {
    private String type;
    private String message;

    public VoiceAction(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}