package com.example.callum.makemymeeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This class controls the settings menu to edit text size and colour.
 */

public class SettingsMain extends AppCompatActivity {
    private Button buttonBack;

    /**
     * {@inheritDoc}
     * @param savedInstanceState - Get phone state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        /**
         * Create button
         * Instantiate button
         * Call "openMainActivity" to navigate back to MainActivity on button press
         */
        buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    /**
     * On button pressed, navigate to MainActivity
     */
    public void openMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
