package com.JanataWifi.Sendify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ClientActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    ListView wifi_listView;
    ClientAdapter clientAdapter;
    ArrayList<ScanResult> wifiList;

    public static String qrScanned = "Nothing";

    Button bt_scanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        wifi_listView = (ListView) findViewById(R.id.lv_av_wifi);
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        bt_scanQR = findViewById(R.id.bt_scanQR);
        bt_scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientActivity.this, QRScannerActivity.class));
            }
        });

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        getPackageManager();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        } else {
            scanWifiList();
        }
    }

    private void scanWifiList() {
        wifiManager.startScan();
        wifiList = (ArrayList<ScanResult>) wifiManager.getScanResults();
        setAdapter();

    }

    private void setAdapter() {
        clientAdapter = new ClientAdapter(getApplicationContext(), wifiList);
        wifi_listView.setAdapter(clientAdapter);
    }

    private static class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Toast.makeText(this, ""+qrScanned, Toast.LENGTH_SHORT).show();
    }
}