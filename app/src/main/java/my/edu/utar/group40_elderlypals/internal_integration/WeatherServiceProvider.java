package my.edu.utar.group40_elderlypals.internal_integration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherServiceProvider {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private WeatherApiService apiService;
    private String apiKey = "0c9e1afffa3812d8e53358c8e2f399f6";

    public WeatherServiceProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherApiService.class);
    }

    public void fetchWeatherAdvice(double lat, double lon, WeatherAdviceListener listener) {
        apiService.getCurrentWeather(lat, lon, apiKey, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float temp = response.body().main.temp;
                    String advice = generateSmartAdvice(temp);
                    listener.onAdviceReceived(advice);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                listener.onError("Could not fetch weather data.");
            }
        });
    }

    private String generateSmartAdvice(float temp) {
        if (temp > 33) {
            return "It is very hot today (" + temp + "°C). Please drink extra water and stay indoors.";
        } else if (temp < 24) {
            return "It's a bit chilly today. Keep yourself warm!";
        } else {
            return "The weather is pleasant. Have a nice day!";
        }
    }

    public interface WeatherAdviceListener {
        void onAdviceReceived(String advice);
        void onError(String message);
    }
}