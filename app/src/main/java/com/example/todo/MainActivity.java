package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferencesHelper.useSavedPreferencesForFont(this, findViewById(R.id.goToListButton), findViewById(R.id.goToPreferencesButton));
    }

    public void goToList(View view) {
        Intent list = new Intent(this, ListActivity.class);
        startActivity(list);
    }

    public void goToPreferences(View view) {
        Intent preferences = new Intent(this, PreferencesActivity.class);
        startActivity(preferences);
    }
}
