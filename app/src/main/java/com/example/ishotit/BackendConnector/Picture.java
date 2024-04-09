package com.example.ishotit.BackendConnector;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Picture {

    public static List<PictureResponse> getAllForCurrentPrompt() {
        return new ArrayList<>();
    }

    public static List<PictureResponse> getAllForUser(String userId) {
        return new ArrayList<>();
    }

    public static void upload(String photoFilePath, String username, Date date, String locationName, String prompt) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String cloudFilePath = "photos/" + new File(photoFilePath).getName();
        StorageReference photoRef = storageRef.child(cloudFilePath);

        Uri file = Uri.fromFile(new File(photoFilePath));
        UploadTask uploadTask = photoRef.putFile(file);

        uploadTask.addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> photo = new HashMap<>();
            photo.put("username", username);
            photo.put("date", date);
            photo.put("locationName", locationName);
            photo.put("prompt", prompt);
            photo.put("url", uri.toString());

            db.collection("photos").add(photo)
                    .addOnSuccessListener(documentReference -> Log.d("ISHOTIT:Validation", "DocumentSnapshot added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w("ISHOTIT:Validation", "Error adding document", e));
        })).addOnFailureListener(exception -> Log.e("ISHOTIT:Validation", "Upload failed", exception));
    }

    public static class PictureResponse {
        public String picturePath;
        public String userId;
        public String locationName;
        public String prompt;
        public Date date;
    }

}
