package com.JanataWifi.Sendify;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Random;

public class HostActivity extends AppCompatActivity implements View.OnClickListener{

    //Initialize variables
    ImageView iv_qrCode;
    ImageButton ib_ssid, ib_pass;
    Button bt_reset, bt_share;
    EditText et_ssid, et_pass;
    private static String ssid, bssid;
    private static String password;
    private static String number;
    private final int PERMISSION_REQ_CODE = 101;

    private WifiManager wifiManager;
    WifiConfiguration currentConfig;
    WifiManager.LocalOnlyHotspotReservation hotspotReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // turn off auto night mode
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.give_hotspot)); // Changing title name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back to home option enabled

        // Bindings
        iv_qrCode = findViewById(R.id.iv_qrCode);
        ib_ssid = findViewById(R.id.ib_ssid);
        ib_ssid.setOnClickListener(HostActivity.this);
        ib_pass = findViewById(R.id.ib_pass);
        ib_pass.setOnClickListener(HostActivity.this);
        bt_reset = findViewById(R.id.bt_reset);
        bt_reset.setOnClickListener(HostActivity.this);
        bt_share = findViewById(R.id.bt_share);
        bt_share.setOnClickListener(HostActivity.this);
        et_ssid = findViewById(R.id.et_ssid);
        et_pass = findViewById(R.id.et_pass);

        //Getting number from intent
        number = getIntent().getStringExtra("number");

        // Get & set the info
        getSetWifiInfo();
        getPassword(12);

        // set info Password
        et_pass.setText(password);

        setQrCode(ssid, password);

    }

    private void setQrCode(String ssid, String password) { // set QR code image to ImageView

        // Initialize multi-format writer
        MultiFormatWriter writer = new MultiFormatWriter();

        String qrText = ssid + "," + password;
        try {
            // Initialize bit matrix
            BitMatrix matrix = writer.encode(qrText, BarcodeFormat.QR_CODE, 250, 250);
            // Initialize barcode encoder
            BarcodeEncoder encoder = new BarcodeEncoder();
            // Initialize bitmap
            Bitmap bitmap = encoder.createBitmap(matrix);
            // set to ImageView
            iv_qrCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void getSetWifiInfo() { // to get the wifi info

        if(!permissionsGranted()){
            //Toast.makeText(this, Html.fromHtml("<font color='"+ Color.RED +"' >" + "Location Permission Is Not Granted!" + "</font>"), Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
        } else {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= 29) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                        if (connectionInfo != null) {
                            String tempSsid = connectionInfo.getSSID();
                            ssid = tempSsid.replaceAll("\"", "");
                            bssid = connectionInfo.getBSSID();

                            // set info SSID
                            et_ssid.setText(number);
                        }
                    }
                } else {
                    /*androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Wifi is turned off. Turn on Wifi?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(HostActivity.this, "You must turn on Wifi!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    wifiManager.setWifiEnabled(true); //true or false
                                    getSetWifiInfo();
                                }
                            }).show();*/
                }
            } else {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo.isConnected()) {
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo != null) {
                        String tempSsid = connectionInfo.getSSID();
                        ssid = tempSsid.replaceAll("\"", "");
                        bssid = connectionInfo.getBSSID();

                        // set info SSID
                        et_ssid.setText(number);
                    }
                } else {
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Wifi is turned off. Turn on Wifi?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(HostActivity.this, "You must turn on Wifi!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    wifiManager.setWifiEnabled(true); //true or false
                                    getSetWifiInfo();
                                }
                            }).show();
                }
            }
        }
    }
    private Boolean permissionsGranted() { // Checking if permission already granted or not
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,  final String[] permissions,  final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSetWifiInfo();
            } else {
                Toast.makeText(this, Html.fromHtml("<font color='"+ Color.RED +"' >" + "You didn't give permission to access device location" + "</font>"), Toast.LENGTH_LONG).show();
                startAppDetails();
            }
        }
    }

    private void startAppDetails() { // see app details for permission
        Intent detailsIntent = new Intent();
        detailsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        detailsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        detailsIntent.setData(Uri.parse("package:" + getPackageName()));
        detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailsIntent);
    }

    public void getPassword(int length){ // get Random Password
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@&#".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();

        for(int i = 0; i < length; i++){
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        password = stringBuilder.toString();
    }

    public void lunchTetherSettings(){
        //Toast.makeText(this, "tether", Toast.LENGTH_SHORT).show();
        Intent tetherIntent = new Intent(Intent.ACTION_MAIN, null);
        tetherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        tetherIntent.setComponent(componentName);
        tetherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(tetherIntent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) { // Set On-click listener
        switch (view.getId())
        {
            case R.id.ib_ssid: // whn click copy ssid
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied ssid", ssid);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(HostActivity.this, "SSID Copied", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ib_pass: // whn click copy password
                ClipboardManager clipboard2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip2 = ClipData.newPlainText("Copied password", password);
                clipboard2.setPrimaryClip(clip2);
                Toast.makeText(HostActivity.this, "Password Copied", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bt_reset: // when click reset hotspot
                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please change the password of the hotspot.")
                        .setTitle("Set SSID & Password")
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                lunchTetherSettings();
                            }
                        }).show();
                break;

            case R.id.bt_share:

                androidx.appcompat.app.AlertDialog.Builder hotspotBuilder = new AlertDialog.Builder(this);
                hotspotBuilder.setMessage("Please turn on your hotspot.")
                        .setTitle("Turn on Hotspot")
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /*if(setWifiApState(true)){
                                    Toast.makeText(HostActivity.this, "hostspot enabled", Toast.LENGTH_SHORT).show();
                                }*/
                                Intent shareIntent = new Intent(HostActivity.this, HostHotspotActivity.class);
                                shareIntent.putExtra("SSID", ssid);
                                shareIntent.putExtra("BSSID", bssid);
                                shareIntent.putExtra("password", password);
                                startActivity(shareIntent);
                            }
                        }).show();
                break;

            default:
                Toast.makeText(this, Html.fromHtml("<font color='"+ Color.RED +"' >" + "ERROR OCCURRED" + "</font>"), Toast.LENGTH_SHORT).show();
        }
    }

    /*public boolean setWifiApState(boolean enabled) {
        //config = Preconditions.checkNotNull(config);
        try {
            WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (enabled) {
                mWifiManager.setWifiEnabled(false);
            }
            WifiConfiguration conf = getWifiApConfiguration();
            mWifiManager.addNetwork(conf);

            return (Boolean) mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class).invoke(mWifiManager, conf, enabled);
        } catch (Exception e) {
            Toast.makeText(this, "EXC", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    public WifiConfiguration getWifiApConfiguration() {
        Toast.makeText(HostActivity.this, "HERE", Toast.LENGTH_SHORT).show();
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID =  "\""+ssid+"\"";
        //conf.preSharedKey = password;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        return conf;
    }*/

}