package my.edu.utar.group40_elderlypals;

public class CommandParser {

    public static VoiceAction parseCommand(String input) {

        if (input == null || input.trim().equals("")) {
            return new VoiceAction("UNKNOWN", "Sorry, I could not understand your request.");
        }

        String text = input.toLowerCase().trim();

        if (text.contains("happy")) {
            return new VoiceAction("MOOD_HAPPY", "Mood logged as happy.");
        }

        if (text.contains("sad")) {
            return new VoiceAction("MOOD_SAD", "Mood logged as sad.");
        }

        if (text.contains("tired")) {
            return new VoiceAction("MOOD_TIRED", "Mood logged as tired.");
        }

        if (text.contains("neutral")) {
            return new VoiceAction("MOOD_NEUTRAL", "Mood logged as neutral.");
        }

        if ((text.contains("took") || text.contains("taken")) &&
                (text.contains("pill") || text.contains("medicine"))) {

            if (text.contains("yellow")) {
                return new VoiceAction("MEDICATION_YELLOW", "Yellow pill logged successfully.");
            }

            if (text.contains("red")) {
                return new VoiceAction("MEDICATION_RED", "Red pill logged successfully.");
            }

            if (text.contains("white")) {
                return new VoiceAction("MEDICATION_WHITE", "White pill logged successfully.");
            }

            if (text.contains("green")) {
                return new VoiceAction("MEDICATION_GREEN", "Green pill logged successfully.");
            }

            return new VoiceAction("MEDICATION_TAKEN", "Medication logged successfully.");
        }

        if (text.contains("help") || text.contains("emergency") || text.contains("sos")) {
            return new VoiceAction("EMERGENCY", "Emergency mode activated.");
        }

        return new VoiceAction("UNKNOWN", "Sorry, I could not understand your request.");
    }
}