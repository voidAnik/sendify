package com.JanataWifi.Sendify;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    // Initialize variables
    Button sender_hotspot, receiver_hotspot;
    EditText et_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Binding
        sender_hotspot = findViewById(R.id.sender_hotspot);
        receiver_hotspot = findViewById(R.id.receiver_hotspot);
        et_number = findViewById(R.id.et_number);

        sender_hotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_number.getText().toString().length() < 11){
                    et_number.setError("Enter Your phone number of 11 digits!");
                } else {
                    Intent senderIntent = new Intent(MainActivity.this, HostActivity.class);
                    senderIntent.putExtra("number", et_number.getText().toString());
                    startActivity(senderIntent);
                }
            }
        });

        receiver_hotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_number.getText().toString().length() < 11){
                    et_number.setError("Enter Your phone number of 11 digits!");
                } else {
                    Intent receiverIntent = new Intent(MainActivity.this, ClientActivity.class);
                    receiverIntent.putExtra("number", et_number.getText().toString());
                    startActivity(receiverIntent);
                }
            }
        });
    }
}