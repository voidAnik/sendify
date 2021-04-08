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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    ListView wifi_listView;
    ClientAdapter clientAdapter;
    ArrayList<ScanResult> wifiList;

    public static String qrScanned = "Nothing";

    Button bt_scanQR, bt_manual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        wifi_listView = (ListView) findViewById(R.id.lv_av_wifi);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        bt_scanQR = findViewById(R.id.bt_scanQR);
        bt_scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientActivity.this, QRScannerActivity.class));
            }
        });

        bt_manual = findViewById(R.id.bt_manual);
        bt_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWifi();
            }
        });

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        getPackageManager();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
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
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "" + qrScanned, Toast.LENGTH_SHORT).show();
    }

    public void connectWifi() {
        String currentSsid = "SOS";
        //String currentBssid = "f8:ad:cb:04:01:fa";
        String currentBssid = "50:D4:F7:F7:11:8E";
        String selectedPassItem = "sos2441139";
        WifiConfiguration wifiConf = null;
        WifiConfiguration savedConf = null;

        //existing configured networks
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    30);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    25);
        }else {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

            if (list != null) {
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + currentSsid + "\"")) {
                        Toast.makeText(this, "existing network found: " + i.networkId + " " + i.SSID, Toast.LENGTH_SHORT).show();
                        savedConf = i;
                        break;
                    }
                }
            }

            if (savedConf != null) {
                Toast.makeText(this, "coping existing configuration", Toast.LENGTH_SHORT).show();
                wifiConf = savedConf;
            } else {
                Toast.makeText(this, "creating new configuration", Toast.LENGTH_SHORT).show();
                wifiConf = new WifiConfiguration();
            }

            wifiConf.SSID = String.format("\"%s\"", currentSsid);
            wifiConf.BSSID = currentBssid;
            wifiConf.preSharedKey = String.format("\"%s\"", selectedPassItem);
            wifiConf.status = WifiConfiguration.Status.ENABLED;
            //wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            int netId;

            if (savedConf != null) {
                netId = wifiManager.updateNetwork(wifiConf);
                Toast.makeText(this, "configuration updated "+ netId, Toast.LENGTH_SHORT).show();
            } else {
                netId = wifiManager.addNetwork(wifiConf);
                Toast.makeText(this, "configuration created " + netId, Toast.LENGTH_SHORT).show();
            }
            //Log.d("Network244", currentSsid);
            //Log.d("Network244", selectedPassItem);
            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        }
    }
}