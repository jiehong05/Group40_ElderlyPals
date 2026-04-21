package my.edu.utar.group40_elderlypals;

public class VoiceAction {

    private String commandType;
    private String payload;
    private String message;

    public VoiceAction(String commandType, String payload, String message) {
        this.commandType = commandType;
        this.payload = payload;
        this.message = message;
    }

    public String getCommandType() {
        return commandType;
    }

    public String getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }
}