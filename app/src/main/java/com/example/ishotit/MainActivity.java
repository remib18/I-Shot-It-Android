package com.example.ishotit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ishotit.BackendConnector.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userId = User.getId(this);

        Class<?> activity;
        if (Objects.isNull(userId)) {
            activity = Onboarding.class;
        } else {
            activity = User.hasAlreadyPostedToday(userId) ? MyPresentLife.class : Camera.class;
        }
        Intent intent = new Intent(this, activity);
        startActivity(intent);

        finish();
    }
}