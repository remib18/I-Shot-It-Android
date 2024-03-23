package com.example.ishotit.BackendConnector;

import com.google.firebase.firestore.FirebaseFirestore;

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
        // Generate a unique userId
        String userId = UUID.randomUUID().toString();

        // Create a new user object
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("phoneNumber", phoneNumber);
        user.put("age", age);

        // Get an instance of Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document to the "users" collection with userId as the document ID
        db.collection("users").document(userId).set(user);

        return userId;
    }

}
