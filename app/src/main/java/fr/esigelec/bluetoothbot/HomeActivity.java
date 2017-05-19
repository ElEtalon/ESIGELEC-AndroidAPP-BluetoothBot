package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.content.ContentResolver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ImageButton imgAboutButton;
    private ListView listViewDevices;

    private CheckBox checkBoxAutoLux;
    private SeekBar seekBarLuminiosity;
    private LuminosityControl luminosityControl;
    private SensorManager sensorManager;

    private ToggleButton buttonBluetoothOnOff;
    private Switch switchBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSearch bluetoothSearch;
    private boolean bluetoothState;

    private ArrayList<BluetoothDevice> discoveredDevices    = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> pairedDevices        = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Connectivity
        /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        TextView textConnectivity = (TextView) findViewById(R.id.TextConnectivity);
        textConnectivity.setText("OnCreate");
        Connectivity connectivity = new Connectivity(textConnectivity, connectivityManager);
        new RequeteHttp().execute(textConnectivity);
        //------------*/

        /**
         * Test permission
         */
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); //CODE_WRITE_SETTINGS_PERMISSION
        }

        /// BLUETOOTH
        /*
        * List of devices
        */
        // get the listview
        this.listViewDevices =(ListView)findViewById(R.id.listViewDevices);

        /*
        * Luminiosity utils
        */
        // seekBar
        this.seekBarLuminiosity = (SeekBar) findViewById(R.id.seekBarLuminiosity);
        this.seekBarLuminiosity.setMax(255);

        // check box
        this.checkBoxAutoLux = (CheckBox) findViewById(R.id.checkBoxAutoLux);

        // set auto mode false
        this.checkBoxAutoLux.setChecked(false);

        // get sensor manager
        this.sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Create luminiosity control class
        this.luminosityControl = new LuminosityControl(sensorManager);

        // Set the current luminosity with the current screen luminosity
        this.luminosityControl.updateCurrentLuminosityWithCurrentSystemLuminosity(getContentResolver());

        // update seekBar progress
        this.seekBarLuminiosity.setProgress((int)this.luminosityControl.getCurrentLuminosity());

        /*
        * About button
        */
        // About button
        this.imgAboutButton =(ImageButton)findViewById(R.id.imageAboutButton);

        /*
        * Bluetooth utils
        */
      
        // get the bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on/off bluetooth
        this.buttonBluetoothOnOff = (ToggleButton) findViewById(R.id.toggleBluetooth);

        // set the switch off
        this.buttonBluetoothOnOff.setChecked(false);

        // get the bluetooth switch (paired / discover)
        this.switchBluetooth = (Switch) findViewById(R.id.discover_paired);

        // get bluetooth state
        this.bluetoothState = this.bluetoothAdapter.isEnabled();

        // set the switch off/on
        this.switchBluetooth.setChecked(this.bluetoothState);

        // Create bluetoothSearch Class
        this.bluetoothSearch = new BluetoothSearch(this.bluetoothAdapter);

        // first display paired
        this.displayPairedDevices();

        // turn on bluetooth
        //this.turnOnBluetooth();

        // Luminosity listeners
        // seekBar MODE luminosity
        this.seekBarLuminiosity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int luminosityProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                luminosityProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                luminosityControl.setNewLuminosity(getContentResolver(), luminosityProgress);
            }
        });

        // luminosity Mode (auto(true) / manual(false))
        this.checkBoxAutoLux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luminosityControl.setLuminosityMode(checkBoxAutoLux.isChecked(), getContentResolver());
            }
        });


        // Bluetooth listeners
        // Switch listener
        switchBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Display paired devices
                if (switchBluetooth.isChecked()) {
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