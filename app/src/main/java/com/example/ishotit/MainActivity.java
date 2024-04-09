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

        if (Objects.isNull(userId)) {
            Intent intent = new Intent(this, Onboarding.class);
            startActivity(intent);
            finish();
        } else {
            User.hasAlreadyPostedToday(userId, hasPosted -> {
                Intent intent = new Intent(this, hasPosted ? MyPresentLife.class : Camera.class);
                startActivity(intent);
                finish();
            });
        }
    }
}