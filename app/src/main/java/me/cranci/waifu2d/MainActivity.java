package me.cranci.waifu2d;

import android.content.pm.PackageManager;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String BACKGROUND_COLOR_KEY = "BackgroundColor";
    private static final String IMAGE_URI_KEY = "ImageUri";

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private ImageView imageView;
    private ImageButton colorPickerButton;
    private ImageButton imagePickerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        colorPickerButton = findViewById(R.id.colorPickerButton);
        imagePickerButton = findViewById(R.id.imagePickerButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Permission already granted, load saved data
            loadData();
        }
    }

    private void loadData() {
        // Load background color and image from SharedPreferences
        int savedColor = loadColor();
        Uri savedImageUri = loadImageUri();

        if (savedColor != 0) {
            getWindow().getDecorView().setBackgroundColor(savedColor);
        }

        if (savedImageUri != null) {
            setImageFromUri(savedImageUri);
        } else {
            // Set default image only when saved image URI is null
            Log.d("MainActivity", "Selected Image URI: " );
        }
    }

    private void setImageFromUri(Uri imageUri) {
        try {
            Bitmap bitmap = loadBitmapFromUri(imageUri);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e("MainActivity", "Bitmap is null for URI: " + imageUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error loading image from URI: " + imageUri, e);
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "IOException loading image from URI: " + uri, e);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("MainActivity", "SecurityException loading image from URI: " + uri, e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity", "Exception loading image from URI: " + uri, e);
        }
        return null;
    }

    public void pickColor(View view) {
        // Open a color picker or any other means to select a color

        // For demonstration, let's use a random color
        int randomColor = Color.rgb((int) (Math.random() * 255),
                (int) (Math.random() * 255), (int) (Math.random() * 255));

        // Save the selected color to SharedPreferences
        saveColor(randomColor);

        // Set the background color
        getWindow().getDecorView().setBackgroundColor(randomColor);
    }

    public void pickImage(View view) {
        // Check if the app has permission to access storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Permission already granted, open image picker
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            // Save the selected image URI to SharedPreferences
            saveImageUri(selectedImageUri);

            // Set the image in the ImageView
            setImageFromUri(selectedImageUri);
        }
    }

    private void saveColor(int color) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(BACKGROUND_COLOR_KEY, color);
        editor.apply();
    }

    private int loadColor() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(BACKGROUND_COLOR_KEY, 0);
    }

    private void saveImageUri(Uri imageUri) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        String encodedUri = Uri.encode(imageUri.toString());
        editor.putString(IMAGE_URI_KEY, encodedUri);
        editor.apply();
        Log.d("MainActivity", "Image URI saved: " + encodedUri);
    }

    private Uri loadImageUri() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String encodedUriString = prefs.getString(IMAGE_URI_KEY, null);
        if (encodedUriString != null) {
            String decodedUriString = Uri.decode(encodedUriString);
            Uri uri = Uri.parse(decodedUriString);
            Log.d("MainActivity", "Loaded image URI: " + uri);
            return uri;
        }
        return null;
    }


}