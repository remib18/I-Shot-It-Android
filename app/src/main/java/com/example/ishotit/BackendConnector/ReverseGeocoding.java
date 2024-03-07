package com.example.ishotit.BackendConnector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ReverseGeocoding {

    private final Context context;
    private final Activity activity;

    private ReverseGeocoding(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private static void askForLocationPermission(Context context, Activity activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Toast.makeText(context, "Location permission is required.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getLocationName(Context context, Activity activity, Function<String, Void> callback) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LocationManager.class);
        askForLocationPermission(context, activity);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                locationManager.removeUpdates(this);
                Log.d("ISHOTIT:ReverseGeocoding", "Location updated");
                try {
                    ReverseGeocoding reverseGeocoding = new ReverseGeocoding(context, activity);
                    Location loc = new Location(location.getLatitude(), location.getLongitude());
                    String name = reverseGeocoding.main(loc);

                    callback.apply(name);
                } catch (Exception e) {
                    Log.e("ISHOTIT:ReverseGeocoding", "Callback failed");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

        });
    }

    private String main(Location loc) {
        Log.d("ISHOTIT:ReverseGeocoding", "Getting location name");

        if (loc == null) {
            Log.e("ISHOTIT:ReverseGeocoding", "Location is null");
            return "Unknown Location";
        }
        if (!this.isNewLocation(loc)) {
            Log.d("ISHOTIT:ReverseGeocoding", "Location is not new, returning stored location name");
            return this.getStoredLocationName();
        }

        return this.fetchNewLocationName(loc);
    }

    private boolean isNewLocation(Location loc) {
        SharedPreferences prefs = context.getSharedPreferences("locationPrefs", Context.MODE_PRIVATE);
        double lastLatitude = Double.longBitsToDouble(prefs.getLong("lastLatitude", Double.doubleToLongBits(0.0)));
        double lastLongitude = Double.longBitsToDouble(prefs.getLong("lastLongitude", Double.doubleToLongBits(0.0)));

        double diffLatitude = Math.abs(lastLatitude - loc.latitude);
        double diffLongitude = Math.abs(lastLongitude - loc.longitude);

        if (diffLatitude > 0.01 || diffLongitude > 0.01) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastLatitude", Double.doubleToRawLongBits(loc.latitude));
            editor.putLong("lastLongitude", Double.doubleToRawLongBits(loc.longitude));
            editor.apply();
            return true;
        }

        return false;
    }

    private String getStoredLocationName() {
        SharedPreferences prefs = context.getSharedPreferences("locationPrefs", Context.MODE_PRIVATE);
        String loc = prefs.getString("lastName", "");
        if (loc.isEmpty() || loc.equals("Unknown Location")) {
            Log.e("ISHOTIT:ReverseGeocoding", "Stored location name is empty");
        }
        return loc;
    }

    private String fetchNewLocationName(Location loc) {
        Log.d("ISHOTIT:ReverseGeocoding", "Fetching new location name");
        AtomicReference<String> locationName = new AtomicReference<>("Unknown Location");

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("https://api.geoapify.com/v1/geocode/reverse?lat=" + loc.latitude + "&lon=" + loc.longitude + "&apiKey=" + SecretManager.REVERSE_GEOCODING_API_KEY);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");

                InputStream inputStream = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                String responseText = responseBuilder.toString();

                Log.d("ISHOTIT:ReverseGeocoding", url + ": " + http.getResponseCode() + " " + http.getResponseMessage());
                locationName.set(responseFormatter(responseText));
                http.disconnect();
            } catch (MalformedURLException e) {
                Log.e("ISHOTIT:ReverseGeocoding", "Malformed URL");
            } catch (IOException e) {
                Log.e("ISHOTIT:ReverseGeocoding", "IO Exception");
            }

            handler.post(() -> {
                if (Objects.nonNull(locationName)) {
                    SharedPreferences prefs = context.getSharedPreferences("locationPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastName", locationName.get());
                    editor.putLong("latitude", Double.doubleToRawLongBits(loc.latitude));
                    editor.putLong("longitude", Double.doubleToRawLongBits(loc.longitude));
                    editor.apply();
                } else {
                    Log.e("ISHOTIT:ReverseGeocoding", "Location name is null");
                }
            });
        });

        return locationName.get();
    }

    private String responseFormatter(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray features = json.getJSONArray("features");
            JSONObject feature = features.getJSONObject(0);
            JSONObject properties = feature.getJSONObject("properties");
            String city = properties.getString("city");
            String country = properties.getString("country");
            String res = city + ", " + country;
            Log.d("ISHOTIT:ReverseGeocoding", "Location name: " + res);
            return res;
        } catch (JSONException e) {
            Log.e("ISHOTIT:ReverseGeocoding", "JSON Exception");
            throw new RuntimeException(e);
            // return "Unknown Location";
        }
    }

    private static class Location {
        double latitude;
        double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}