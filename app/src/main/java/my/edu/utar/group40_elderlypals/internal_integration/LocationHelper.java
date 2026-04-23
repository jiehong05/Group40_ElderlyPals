package my.edu.utar.group40_elderlypals.internal_integration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    
    public LocationHelper(Context context) {
        // Store context for later use (like Geocoder)
        this.context = context;
        // Initialize the location tool
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(final LocationCallback callback) {
        // Fetch the last known GPS coordinates
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            callback.onLocationReady(location.getLatitude(), location.getLongitude());
                        } else {
                            // Fallback to Kampar coordinates if GPS is off
                            callback.onLocationReady(4.3072, 101.1529);
                        }
                    }
                });
    }

    // Convert lat/lon to City Name (e.g., "Kampar")
    public String getCityName(double lat, double lon) {
        String cityName = "Unknown Location";
        if (context == null) return cityName;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                // locality usually returns the city name like "Kampar"
                cityName = addresses.get(0).getLocality();
                if (cityName == null) {
                    cityName = addresses.get(0).getAdminArea(); // Fallback to state/region
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public interface LocationCallback {
        void onLocationReady(double lat, double lon);
    }
}