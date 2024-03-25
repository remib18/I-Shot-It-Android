package com.example.ishotit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.exifinterface.media.ExifInterface;

import com.example.ishotit.BackendConnector.FirebaseUploader;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class Validation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_validation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String photoFilePath = intent.getStringExtra("photoFilePath");
        String locationName = intent.getStringExtra("locationName");

        TextView locationNameView = findViewById(R.id.validation_location);
        locationNameView.setText(locationName);

        ImageView imageView = findViewById(R.id.validation_cameraView);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath);

        ImageButton validateButton = findViewById(R.id.validation_enterLifeBtn);
        validateButton.setOnClickListener(v -> {

            SharedPreferences preferences = getSharedPreferences("ishotit", Context.MODE_PRIVATE);
            String userName = preferences.getString("username", "");

            Intent newIntent = new Intent(this, MyPresentLife.class);
            FirebaseUploader uploader = new FirebaseUploader();
            uploader.uploadPhoto(photoFilePath, userName, new Date(), locationName, "prompt");
            startActivity(newIntent);
        });

        if (Objects.isNull(photoFilePath)) {
            Log.e("ISHOTIT:Validation", "No photo file path provided");
            return;
        }

        assert photoFilePath != null;

        try {
            ExifInterface exif = new ExifInterface(photoFilePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            Log.e("ISHOTIT:Validation", "Error reading image orientation", e);
        }

        imageView.setImageBitmap(bitmap);
    }
}