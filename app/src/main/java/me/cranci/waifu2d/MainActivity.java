package me.cranci.waifu2d;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String BACKGROUND_COLOR_KEY = "BackgroundColor";

    private ImageView imageView;
    private ImageButton colorPickerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        colorPickerButton = findViewById(R.id.colorPickerButton);

        // Load background color from SharedPreferences
        int savedColor = loadColor();
        if (savedColor != 0) {
            getWindow().getDecorView().setBackgroundColor(savedColor);
        }
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

    private void saveColor(int color) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(BACKGROUND_COLOR_KEY, color);
        editor.apply();
    }

    private int loadColor() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(BACKGROUND_COLOR_KEY, 0);
    }
}