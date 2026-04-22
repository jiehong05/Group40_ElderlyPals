package my.edu.utar.group40_elderlypals.internal_integration;

public class EmergencyManager {

    public String createEmergencyMessage(double lat, double lon) {
        // This creates a Google Maps link for the family/responders
        String mapsLink = "https://www.google.com/maps?q=" + lat + "," + lon;

        return "🚨 EMERGENCY ALERT 🚨\n" +
                "User needs assistance!\n" +
                "Current Location: " + mapsLink + "\n" +
                "Please check the Health Card in-app for medical history.";
    }
}