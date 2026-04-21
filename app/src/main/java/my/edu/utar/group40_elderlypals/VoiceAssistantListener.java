package my.edu.utar.group40_elderlypals;

/**
 * Interface for Member 3 (Voice Assistant) to communicate results back to Member 4 (Frontend).
 */
public interface VoiceAssistantListener {
    /**
     * Called when a valid command is recognized.
     * @param commandType One of: "MOOD", "MEDICATION", "EMERGENCY", "WEATHER"
     * @param payload The specific data (e.g., "happy", "aspirin", "help_needed")
     */
    void onCommandDetected(String commandType, String payload);

    /**
     * Called when the voice assistant changes state (for UI updates).
     * @param state One of: "IDLE", "LISTENING", "PROCESSING"
     */
    void onStateChanged(String state);

    /**
     * Called if there is an error (e.g., no internet, permission denied).
     * @param errorMessage User-friendly error message.
     */
    void onError(String errorMessage);
}
