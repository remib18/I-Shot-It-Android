package com.example.ishotit;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class myPresentLife extends AppCompatActivity {

    private List<String> imageUrls = Arrays.asList(
            "https://i.pinimg.com/474x/6f/3a/21/6f3a215076dd1fef558a7f266ce65596--google-search-yahoo-search.jpg",
            "https://www.christies.com/lotfinderimages/d55279/d5527997a.jpg",
            "https://i.pinimg.com/originals/0e/da/7c/0eda7c35d971e5d70c7cc19d4cb26692.jpg",
            "https://i.pinimg.com/736x/70/44/68/704468d17e60f7d651c2df6ac5b83025.jpg",
            "https://images.tuscany-villas.fr/ccontent_page/8870411417711703xx/large/1962-vespa.jpg"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_present_life);

        RecyclerView recyclerView = findViewById(R.id.image_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ImageAdapter(imageUrls));

    }
}