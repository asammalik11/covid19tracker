package com.hackathon.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String docMain = "25373421b734d8865465ed063a369bf5";  // the doc will be used to store device names

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> listItems = new ArrayList<String>();

    BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "MainActivity";

    Button buttonRefresh, buttonSubmit;
    TextView bAddress;

    String bluetoothAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.dView);

        buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        bAddress = (TextView) findViewById(R.id.bAddress);

        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Bluetooth Address \n" + itemValue, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
//        bluetoothAdapter.cancelDiscovery();
    }

    // Broadcast Receiver for listing devices that are not yet paired
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + " : " + device.getAddress());

                listItems.add(device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    // searching for unpaired devices
    public void searchDevices(View view) {

        if (bluetoothAdapter == null) {
            // Show Alert
            Toast.makeText(getApplicationContext(),
                    "Bluetooth not supported", Toast.LENGTH_LONG)
                    .show();

            return;
        }


        // Show Alert
        Toast.makeText(getApplicationContext(),
                "Searching", Toast.LENGTH_LONG)
                .show();

        listItems.clear();
        arrayAdapter.notifyDataSetChanged();

        Log.d(TAG, "Searching for unpaired devices");


        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
        }
        if (!bluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
        }
    }


    // Only required for API 23+
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }
    }

    public void bSettings(View v){
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    public void submitMAC(View v){
        buttonRefresh.setEnabled(true);
        buttonRefresh.performClick();
        Log.i(TAG, "address: " + bAddress.getText());

        bluetoothAddress = (String) bAddress.getText().toString();

        // Create local json database
        CloudantDB.initDB(getApplicationContext(), docMain, bluetoothAddress);
    }


}