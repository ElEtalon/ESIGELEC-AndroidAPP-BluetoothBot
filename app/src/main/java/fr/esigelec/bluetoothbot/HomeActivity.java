package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private ImageButton imgAboutButton;
    private Switch switchButton;
    private ListView listViewDevices;
    private static final UUID BLUE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bTSocket;

    private boolean isConnected = false;

    private ArrayList<BluetoothDevice> discoveredDevices    = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> pairedDevices        = new ArrayList<BluetoothDevice>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /// BLUETOOTH
        // get the bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on bluetooth
        this.turnOnBluetooth();

        /// LIST VIEW
        // get the list
        this.listViewDevices =(ListView)findViewById(R.id.listViewDevices);
        // first display paired
        displayPairedDevices();

        /// Switch
        // get the switch
        switchButton = (Switch) findViewById(R.id.discover_paired);
        // set the switch off
        switchButton.setChecked(true);

        // Switch listener
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Display paired devices
                if (switchButton.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Display paired devices", Toast.LENGTH_LONG).show();
                    displayPairedDevices();

                // Display discovered devices
                }else {
                    Toast.makeText(getApplicationContext(), "Display discovery devices", Toast.LENGTH_LONG).show();
                    displayDiscoveredDevices();
                }
            }
        });

        // On click list
        this.listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selected = pairedDevices.get((int)id);

                // connection
                if(bluetoothConnect(selected)){
                    // If connected
                    Toast.makeText(getApplicationContext(), "Connection successful to " + selected.getName(), Toast.LENGTH_LONG).show();
                    Intent controlsPage=new Intent(HomeActivity.this, ControlsActivity.class);
                    controlsPage.putExtra("BluetoothDevice", selected);
                    startActivity(controlsPage);
                }else{
                    Toast.makeText(getApplicationContext(), "Connection error to " + selected.getName(), Toast.LENGTH_LONG).show();
                }
            }
        });

        /// Image Button
        // On click logo --> about
        this.imgAboutButton =(ImageButton)findViewById(R.id.imageAboutButton);
        this.imgAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "About button clicked", Toast.LENGTH_LONG).show();
                Intent aboutPage=new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(aboutPage);
            }
        });
    }

    /**
     * Display paired bluetooth devices
     */
    private void displayPairedDevices(){
        // get list of bluetooth Devices
        this.getListPairedBluetooth();

        // update the list
        this.listViewUpdate(this.pairedDevices);
    }

    /**
     * Display discovered bluetooth devices
     */
    private void displayDiscoveredDevices(){
        // reset list
        this.discoveredDevices = new ArrayList<BluetoothDevice>();

        // get list of bluetooth Devices
        getListDiscoveredBluetooth();

        // update the list
        this.listViewUpdate(this.discoveredDevices);
    }

    /**
     * Update the listView
     * @param
     */
    private void listViewUpdate(ArrayList<BluetoothDevice> deviceList){
        ArrayList<String> deviceListExplained = new ArrayList<String>();

        for(BluetoothDevice bt : deviceList){
            deviceListExplained.add(bt.getName() + " (" + bt.getAddress() + ")");
        }

        this.listViewDevices.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceListExplained));
    }

    /// BLUETOOTH FUNCTIONS
    /**
     * Turn on bluetooth
     * @param
     */
    private void turnOnBluetooth() {
        if (!this.bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            //Toast.makeText(getApplicationContext(), "Bluetooth turned on",Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplicationContext(), "Bluetooth already on", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get the list of the bluetooth devices
     * @param
     */
    private void getListPairedBluetooth(){
        this.pairedDevices = new ArrayList<BluetoothDevice>();
        // get list of devices
        Set<BluetoothDevice>paired = this.bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : paired){
            this.pairedDevices.add(bt);
        }

    }

    /**
     * bluetoothConnectThread
     * @param bluetoothDevice
     * @return
     */
    private void bluetoothConnectThread(BluetoothDevice bluetoothDevice) {
        BluetoothSocket tmp = null;

        try {
            tmp = bluetoothDevice.createRfcommSocketToServiceRecord(BLUE_UUID);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error when create connection socket " + e,Toast.LENGTH_LONG).show();
        }
        this.bTSocket = tmp;
    }

    /**
     * bluetoothConnect
     * @param bluetoothDevice
     * @return
     */
    private boolean bluetoothConnect(BluetoothDevice bluetoothDevice){
        this.bluetoothConnectThread(bluetoothDevice);

        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }

        Toast.makeText(getApplicationContext(), "Try to connect...",Toast.LENGTH_LONG).show();
        try {
            bTSocket.connect();
            return true;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error when connecting " + e,Toast.LENGTH_LONG).show();
            // Close the socket
            try {
                //bTSocket.close();
            } catch (Exception e2) {
                Toast.makeText(getApplicationContext(), "Error when closing the socket " + e,Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }

    /**
     * Begin bluetooth device discovery
     * @param
     */
    private void getListDiscoveredBluetooth(){
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        this.bluetoothAdapter.startDiscovery();

        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bluetoothReceiver, ifilter);
    }

    /**
     * Bluetooth Receiver from discovery
     */
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Notification
                Toast.makeText(getApplicationContext(), "Device found",Toast.LENGTH_LONG).show();

                // Create a new device item
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Add to the list
                discoveredDevices.add(device);
            }
        }
    };

}