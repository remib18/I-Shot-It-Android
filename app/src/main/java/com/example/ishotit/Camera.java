package com.example.ishotit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ishotit.BackendConnector.ReverseGeocoding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Camera extends AppCompatActivity {

    private boolean isCameraLoaded = false;
    private ImageCapture imageCapture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReverseGeocoding.getLocationName(this, this, this::handleGeocodingResult);

        findViewById(R.id.camera_captureBtn).setOnClickListener(this::handlePhotoCapture);

        PreviewView cameraView = findViewById(R.id.camera_cameraView);
        cameraView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cameraView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                loadCamera();
            }
        });
    }

    private Void handleGeocodingResult(String locationName) {
        TextView location = findViewById(R.id.camera_location);
        location.setText(locationName);
        return null;
    }

    private void handlePhotoCapture(View v) {
        Log.d("ISHOTIT:Camera", "Capturing photo...");
        // Create timestamped output file to hold the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File outputDirectory = getExternalFilesDir(null);
        File photoFile = new File(outputDirectory, "JPEG_" + timeStamp + ".jpg");

        // Set up image capture listener
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d("ISHOTIT:Camera", "Photo capture succeeded: " + photoFile.getAbsolutePath());
                // Create intent to start Validation activity
                Intent intent = new Intent(Camera.this, Validation.class);
                intent.putExtra("photoFilePath", photoFile.getAbsolutePath());
                intent.putExtra("locationName", ((TextView) findViewById(R.id.camera_location)).getText());
                startActivity(intent);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("ISHOTIT:Camera", "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }

    private void loadCamera() {
        if (isCameraLoaded) {
            return;
        }
        isCameraLoaded = true;
        Log.d("ISHOTIT:Camera", "Loading camera");
        PreviewView cameraView = findViewById(R.id.camera_cameraView);

        // Check for camera permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ISHOTIT:Camera", "Requesting camera permission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera permission is needed.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Log.d("ISHOTIT:Camera", "Camera provider ready");

                // Create a new Preview instance
                Preview preview = new Preview.Builder()
                        .setTargetResolution(new Size(cameraView.getWidth(), cameraView.getHeight()))
                        .build();

                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = new CameraSelector.Builder().build();
                Log.d("ISHOTIT:Camera", "Camera selector ready");

                // Unbind any bound use cases before rebinding
                cameraProvider.unbindAll();

                // Bind the Preview use case to the camera provider
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                Log.d("ISHOTIT:Camera", "Camera provider bound to lifecycle");

                // Connect the preview use case to the preview view
                preview.setSurfaceProvider(cameraView.getSurfaceProvider());
                Log.d("ISHOTIT:Camera", "Preview set to surface provider");

            } catch (ExecutionException | InterruptedException e) {
                Log.e("ISHOTIT:Camera", "Error loading camera", e);
                // Handle any errors
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the camera
                loadCamera();
            } else {
                // Permission denied, handle appropriately
                Log.d("ISHOTIT:Camera", "Camera permission denied");
                Toast.makeText(this, "You need to enable the camera...", Toast.LENGTH_LONG).show();
            }
        }
    }
}