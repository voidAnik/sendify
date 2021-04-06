package com.JanataWifi.Sendify;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Initialize variables
    Button sender_hotspot, receiver_hotspot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Binding
        sender_hotspot = findViewById(R.id.sender_hotspot);
        receiver_hotspot = findViewById(R.id.receiver_hotspot);

        sender_hotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent senderIntent = new Intent(MainActivity.this, HostActivity.class);
                startActivity(senderIntent);
            }
        });

        receiver_hotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receiverIntent = new Intent(MainActivity.this, ClientActivity.class);
                startActivity(receiverIntent);
            }
        });
    }
}