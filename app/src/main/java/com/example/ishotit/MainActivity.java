package com.example.ishotit;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ishotit.BackendConnector.Instruction;
import com.example.ishotit.BackendConnector.ReverseGeocoding;
import com.example.ishotit.BackendConnector.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private String userId = null;
    private String location = null;
    private String prompt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Loading userId
        userId = User.getId(this);

        boolean isAirplaneModeOn = Settings.System.getInt(this.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

        if (isAirplaneModeOn) {
            Log.d("ISHOTIT:MainActivity", "Airplane mode is on");
            Toast.makeText(this, "Please turn off airplane mode to use our app and restart.", Toast.LENGTH_LONG).show();
            return;
        }

        // Loading location
        ReverseGeocoding.getLocationName(this, this, locationName -> {
            location = locationName;
            onUpdate();
            return null;
        });

        // Loading prompt
        Instruction.loadCurrentInstruction(instruction -> {
            prompt = instruction;
            onUpdate();
        });
    }

    private void onUpdate() {
        if (Objects.nonNull(location) && Objects.nonNull(prompt)) {
            Log.d("ISHOTIT:MainActivity", "Ready to redirect");
            Log.d("ISHOTIT:MainActivity", "Location: " + location);
            Log.d("ISHOTIT:MainActivity", "Prompt: " + prompt);
            onReady();
        }
    }

    private void onReady() {
        // Redirecting to the appropriate activity with the location and prompt
        if (Objects.isNull(userId)) {
            redirect(Onboarding.class);
            return;
        }
        User.hasAlreadyPostedToday(userId, hasPosted -> {
            Class<?> activity = hasPosted ? MyPresentLife.class : Camera.class;
            redirect(activity);
        });
    }

    private void redirect(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("location", location);
        intent.putExtra("prompt", prompt);
        startActivity(intent);
        finish();
    }
}