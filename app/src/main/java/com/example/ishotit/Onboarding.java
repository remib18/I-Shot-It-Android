package com.example.ishotit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ishotit.BackendConnector.User;

public class Onboarding extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboarding_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.onboarding_start_btn).setOnClickListener(v -> handleFormSubmission());
    }

    private void handleFormSubmission() {
        EditText usernameField = findViewById(R.id.onboarding_username);
        EditText phoneNumberField = findViewById(R.id.onboarding_phone);
        EditText ageField = findViewById(R.id.onboarding_age);

        String username = usernameField.getText().toString();
        String phoneNumber = phoneNumberField.getText().toString();
        int age = Integer.parseInt(ageField.getText().toString());

        if (age < 13) {
            Toast.makeText(this, "You must be at least 13 years old to use I Shot It.", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences preferences = getSharedPreferences("ishotit", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("phoneNumber", phoneNumber);
        editor.putInt("age", age);
        editor.apply();

        // Register the user
        String userId = User.register(username, phoneNumber, age);

        // Save the userId to the default app preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor defaultEditor = prefs.edit();
        defaultEditor.putString("userId", userId);
        defaultEditor.apply();

        // Go to the main activity
        Intent startActivity = new Intent(this, MainActivity.class);
        startActivity(startActivity);
        finish();
    }
}