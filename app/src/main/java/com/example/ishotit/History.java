package com.example.ishotit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ishotit.BackendConnector.Picture;
import com.example.ishotit.BackendConnector.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History extends AppCompatActivity {

    private final List<Picture.PictureResponse> imageUrls = new ArrayList<>();
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.image_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageAdapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(imageAdapter);

        loadUserImages(User.getId(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.back_image_left).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyPresentLife.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadUserImages(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("photos")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String url = document.getString("url");
                            String username = document.getString("username");
                            String locationName = document.getString("locationName");
                            Date date = document.getDate("date");
                            Picture.PictureResponse picture = new Picture.PictureResponse();
                            picture.picturePath = url;
                            picture.userId = username;
                            picture.locationName = locationName;
                            picture.date = date;
                            imageUrls.add(picture);
                        }
                        imageAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("MyPresentLife", "Error getting documents: ", task.getException());
                    }
                });
    }
}