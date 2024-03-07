package com.example.ishotit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = prefs.getString("userId", "");
        String lastPosted = prefs.getString("lastPosted", "");

        Class<?> activity;
        if (userId.isEmpty()) {
            activity = Onboarding.class;
        } else if (!lastPosted.isEmpty()) {
            Date lastPostedAt = new Date(Long.parseLong(lastPosted));
            Date now = new Date();
            boolean havePostedInTheLast24h = now.getTime() - lastPostedAt.getTime() < 24 * 60 * 60 * 1000;
            activity = havePostedInTheLast24h ? MyPresentLife.class : Camera.class;
        } else {
            activity = Camera.class;
        }
        Intent intent = new Intent(this, activity);
        startActivity(intent);

        finish();
    }
}