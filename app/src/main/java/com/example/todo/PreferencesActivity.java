package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PreferencesActivity extends AppCompatActivity {
    private EditText fontSizeTextInputEditText;
    private EditText fontColorTextInputEditText;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        fontSizeTextInputEditText = findViewById(R.id.fontSizeTextInputEditText);
        fontColorTextInputEditText = findViewById(R.id.fontColorTextInputEditText);
        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int defaultFontSize = getResources().getInteger(R.integer.preferences_default_font_size_key);
        int fontSizeFromSharedPreferences = sharedPreferences.getInt(getString(R.string.preferences_font_size_key), defaultFontSize);
        fontSizeTextInputEditText.setText(String.valueOf(fontSizeFromSharedPreferences));

        String defaultFontColor = getResources().getString(R.string.preferences_default_font_color_key);
        String fontColorFromSharedPreferences = sharedPreferences.getString(getString(R.string.preferences_font_color_key), defaultFontColor);
        fontColorTextInputEditText.setText(fontColorFromSharedPreferences);
    }

    public void saveButtonClick(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.preferences_font_size_key), Integer.parseInt(fontSizeTextInputEditText.getText().toString()));
        editor.putString(getString(R.string.preferences_font_color_key), fontColorTextInputEditText.getText().toString());
        editor.apply();
        finish();
    }
}
