package fr.esigelec.bluetoothbot;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private ImageButton imgAboutButton;
    private Switch switchButton;
    private ListView listViewDevices;
    private static final UUID BLUE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

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
                //Log.i("clicks","You Clicked B1");
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
        // get list of bluetooth Devices
        this.discoveredDevices = new ArrayList<BluetoothDevice>();
        //getListDiscoveredBluetooth();

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
            Toast.makeText(getApplicationContext(), "Bluetooth turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth already on", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get the list of the bluetooth devices
     * @param
     */
    private void getListPairedBluetooth(){
        // get list of devices
        Set<BluetoothDevice>paired = this.bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : paired){
            this.pairedDevices.add(bt);
        }

    }


    /**
     *
     * @param bTDevice
     * @return
     */
    private boolean bluetoothConnect(BluetoothDevice bTDevice) {
        bTSocket = null;
        try {
            bTSocket = bTDevice.createRfcommSocketToServiceRecord(BLUE_UUID);
        } catch (IOException e) {
            return false;
        }

        try {
            bTSocket.connect();
        } catch(IOException e) {
            try {
                bTSocket.close();
            } catch(IOException close) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    /*public boolean bluetoothConnectCancel() {
        try {
            bTSocket.close();
        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }*/

    /**
     * Begin bluetooth device discovery
     * @param
     */
    /*private void getListDiscoveredBluetooth(){
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        this.bluetoothAdapter.startDiscovery();

        //let's make a broadcast receiver to register our things
        bluetoothReceiver = new BroadcastReceiver();
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bluetoothReceiver, ifilter);
    }




    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                // Add it to our adapter
                mAdapter.add(newDevice);
                mAdapter.notifyDataSetChanged();
            }
        }
    };*/

    /**
     * @Override
    public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();

    // When discovery finds a device
    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    // Get the BluetoothDevice object from the Intent
    BluetoothDevice device = intent
    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    // If it's already paired, skip it, because it's been listed
    // already
    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
    mNewDevicesArrayAdapter.add(device.getName() + "\n"
    + device.getAddress());
    }
    // When discovery is finished, change the Activity title
    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
    setProgressBarIndeterminateVisibility(false);
    setTitle(R.string.select_device);
    if (mNewDevicesArrayAdapter.getCount() == 0) {
    String noDevices = getResources().getText(
    R.string.none_found).toString();
    mNewDevicesArrayAdapter.add(noDevices);
    }
    }
    }
     */
}