package com.example.ishotit.BackendConnector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {

    /**
     * Register a new user
     *
     * @param username    - the username of the user
     * @param phoneNumber - the phone number of the user
     * @param age         - the age of the user
     * @return the userId of the user
     */
    public static String register(String username, String phoneNumber, int age) {

        String userId = UUID.randomUUID().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("phoneNumber", phoneNumber);
        user.put("age", age);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).set(user);

        return userId;
    }

    public static void hasAlreadyPostedToday(String userId, ResponseCallback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date past24Hours = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

        db.collection("photos")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("date", past24Hours)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ISHOTIT:User", "Checking if user has already posted today: " + !task.getResult().isEmpty());
                        callback.onResult(!task.getResult().isEmpty());
                    } else {
                        Log.e("ISHOTIT:User", "Error getting documents: ", task.getException());
                    }
                });
    }

    public static String getId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = prefs.getString("userId", "");

        if (userId.isEmpty()) {
            return null;
        }

        if (userId.equals("TODO")) {  // TODO: Remove this line: dev fix
            return null;
        }

        return userId;
    }

    public interface ResponseCallback<T> {
        void onResult(T value);
    }

}
