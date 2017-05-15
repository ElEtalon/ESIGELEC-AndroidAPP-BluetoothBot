package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private ImageButton imgAboutButton;
    private Switch switchButton;
    private ListView listViewDevices;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnection bluetoothConnection;

    private boolean isConnected = false;

    private ArrayList<String> deviceList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /// BLUETOOTH
        // get the bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothConnection = new BluetoothConnection(this.bluetoothAdapter);

        // turn on bluetooth
        this.turnOnBluetooth();

        /// LIST VIEW
        // get the list
        this.listViewDevices =(ListView)findViewById(R.id.listViewDevices);

        /// Switch
        // get the switch
        switchButton = (Switch) findViewById(R.id.discover_paired);
        // set the switch off
        switchButton.setChecked(false);

        // Switch listenner
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1, statusSwitch2;
                if (switchButton.isChecked())
                    Toast.makeText(getApplicationContext(), "Switch button checked ", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Switch button unchecked", Toast.LENGTH_LONG).show();
            }
        });

        // get list of bluetooth Devices
        deviceList = bluetoothConnection.getListPairedBluetooth(deviceList);

        // update the list
        this.listViewUpdate();

        // On click list
        this.listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "ListView item : " + position + " & id : " + deviceList.get((int)id), Toast.LENGTH_LONG).show();
                Intent controlsPage=new Intent(HomeActivity.this, ControlsActivity.class);
                controlsPage.putExtra("device_name", deviceList.get((int)id));
                startActivity(controlsPage);
            }
        });

        /// Image Button
        // On click logo --> about
        this.imgAboutButton =(ImageButton)findViewById(R.id.imageAboutButton);
        this.imgAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "About button clicked", Toast.LENGTH_LONG).show();
                //Log.i("clicks","You Clicked B1");
                Intent aboutPage=new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(aboutPage);
            }
        });
    }

    /**
     * Turn on bluetooth
     * @param
     */
    private void turnOnBluetooth() {
        if (!this.bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update the listView
     * @param
     */
    private void listViewUpdate(){
        this.listViewDevices.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, this.deviceList));
    }
}
