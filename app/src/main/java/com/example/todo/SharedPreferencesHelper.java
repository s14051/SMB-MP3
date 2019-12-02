package com.example.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class SharedPreferencesHelper {
    private SharedPreferencesHelper () {}

    public static final void useSavedPreferencesForFont(Context context, View... viewsToProcess) {
        if (context == null){
            throw new IllegalArgumentException("Context passed to the useSavedPreferencesForFont method cannot be null.");
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        // defaults
        int defaultFontSize = context.getResources().getInteger(R.integer.preferences_default_font_size_key);
        String defaultFontColor = context.getResources().getString(R.string.preferences_default_font_color_key);

        // current
        int fontSize = sharedPreferences.getInt(context.getString(R.string.preferences_font_size_key), defaultFontSize);
        String fontColor = sharedPreferences.getString(context.getString(R.string.preferences_font_color_key), defaultFontColor);

        // process
        for (int i = 0; i < viewsToProcess.length; i++){
            View view = viewsToProcess[i];
            if (!(view instanceof TextView)){
                throw new IllegalArgumentException("Views passed to the useSavedPreferencesForFont method must be of type TextView.");
            }

            TextView textView = (TextView)view;
            textView.setTextSize(fontSize);
            textView.setTextColor(Color.parseColor("#" + fontColor));
        }
    }
}
