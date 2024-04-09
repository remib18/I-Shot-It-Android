package com.example.ishotit.BackendConnector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public static boolean hasAlreadyPostedToday(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Photo collection structure
        // {
        //     userId: "user1",
        //     date: "2021-09-01"
        // }

        Date past24Hours = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

        AtomicBoolean hasPostedToday = new AtomicBoolean(false);

        db.collection("photos")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("date", past24Hours)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            hasPostedToday.set(true);
                        }
                    }
                });

        return hasPostedToday.get();
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

}
