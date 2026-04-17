package my.edu.utar.group40_elderlypals.external_integration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHelper {
    private FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Context context) {
        // Initialize the location tool [cite: 129]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(final LocationCallback callback) {
        // Fetch the last known GPS coordinates [cite: 109]
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

    public interface LocationCallback {
        void onLocationReady(double lat, double lon);
    }
}