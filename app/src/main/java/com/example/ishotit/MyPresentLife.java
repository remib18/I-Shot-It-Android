package com.example.ishotit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
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

public class MyPresentLife extends AppCompatActivity {

    private final List<Picture.PictureResponse> imageUrls = new ArrayList<>();
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_present_life);

        RecyclerView recyclerView = findViewById(R.id.image_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageAdapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(imageAdapter);

        loadAllImages();
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.profile_image_left).setOnClickListener(v -> {
            Intent intent = new Intent(this, Friends.class);
            startActivity(intent);
        });

        findViewById(R.id.profile_image_right).setOnClickListener(v -> {
            Intent intent = new Intent(this, History.class);
            intent.putExtra("userId", User.getId(this));
            startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("photos")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5)
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
