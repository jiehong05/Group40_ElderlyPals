package my.edu.utar.group40_elderlypals.internal_integration;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    public MainData main;

    @SerializedName("weather")
    public List<WeatherDescription> weather;

    public class MainData {
        public float temp;
        public int humidity;
    }

    public class WeatherDescription {
        public String description;
    }
}