package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private ImageButton imgAboutButton;
    private Switch switchButton;
    private ListView listViewDevices;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSearch bluetoothSearch;

    private ArrayList<BluetoothDevice> discoveredDevices    = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> pairedDevices        = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        TextView textConnectivity = (TextView) findViewById(R.id.TextConnectivity);
        textConnectivity.setText("OnCreate");
        Connectivity connectivity = new Connectivity(textConnectivity, connectivityManager);
        new RequeteHttp().execute(textConnectivity);
        //------------

        /// BLUETOOTH
        // get the bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on bluetooth
        this.turnOnBluetooth();

        // Create bluetoothSearch Class
        this.bluetoothSearch = new BluetoothSearch(this.bluetoothAdapter);

        /// LIST VIEW
        // get the list
        this.listViewDevices =(ListView)findViewById(R.id.listViewDevices);
        // first display paired
        this.displayPairedDevices();

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
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final BluetoothDevice selected = pairedDevices.get((int)id);

                Toast.makeText(getApplicationContext(), "Connection to " + selected.getName() + "...", Toast.LENGTH_LONG).show();

                // try to connect
                Thread tryConnection = new Thread() {
                    public void run() {
                        // bluetooth connection class
                        BluetoothConnection bluetoothConnection = new BluetoothConnection(bluetoothAdapter, selected);

                        // if connected
                        if(!bluetoothConnection.bluetoothConnect(selected)) {
                            Intent controlsPage = new Intent(HomeActivity.this, ControlsActivity.class);
                            controlsPage.putExtra("BluetoothDevice", selected);
                            startActivity(controlsPage);
                        }
                    }
                };
                tryConnection.start();
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
        // reset list
        this.pairedDevices = new ArrayList<BluetoothDevice>();

        // get list of bluetooth Devices
        this.pairedDevices = this.bluetoothSearch.getListPairedBluetooth();

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
        this.discoveredDevices = this.bluetoothSearch.getListDiscoveredBluetooth();

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
}