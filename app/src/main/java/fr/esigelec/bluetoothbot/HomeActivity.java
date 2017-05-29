package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ToggleButton;

import java.util.ArrayList;

import static android.R.attr.name;

public class HomeActivity extends AppCompatActivity  implements BluetoothCallback{

    private ImageButton imgAboutButton;
    private ListView listViewDevices;

    private CheckBox checkBoxAutoLux;
    private SeekBar seekBarLuminiosity;
    private LuminosityControl luminosityControl;
    private SensorManager sensorManager;

    private ToggleButton buttonBluetoothOnOff;
    private Switch switchBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothService bluetoothSearch;
    private boolean bluetoothState;
    private ProgressBar progressBarDiscovered;

    private TextView textConnectivity;
    private ConnectivityManager connectivityManager;
    private Connectivity connectivity;

    private ArrayList<BluetoothDevice> discoveredDevices    = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> pairedDevices        = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /**
         * Test permission
         */
        if (!Settings.System.canWrite(this)) {
            Utils.testPermission(this, Settings.ACTION_MANAGE_WRITE_SETTINGS);
        }

        //------------------------------------------------------------------------------------------
        /*
        * Connectivity
        */
        // Instance connectivity Manager class
        this.connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // get text view
        this.textConnectivity = (TextView) findViewById(R.id.TextConnectivity);
        this.textConnectivity.setText("OnCreate");

        // Instance connectivity
        this.connectivity = new Connectivity(textConnectivity, connectivityManager);

        // execute request
        //new RequeteHttp().execute(this.textConnectivity);

        /*
        * FireBase Token de la mort
         */
        FireBaseToken FBtoken = new FireBaseToken();
        FireBaseNotification FBnotification = new FireBaseNotification();


        //------------------------------------------------------------------------------------------

        /*
        * Luminiosity utils
        */
        // seekBar
        this.seekBarLuminiosity = (SeekBar) findViewById(R.id.seekBarLuminiosity);
        this.seekBarLuminiosity.setMax(255);

        // check box
        this.checkBoxAutoLux = (CheckBox) findViewById(R.id.checkBoxAutoLux);

        // check if phone has the sensor
        this.checkBoxAutoLux.setEnabled(this.checkIFPhoneHasLightSensor());

        // get sensor manager
        this.sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Create luminiosity control class
        this.luminosityControl = new LuminosityControl(sensorManager);

        // Set the current luminosity with the current screen luminosity
        this.luminosityControl.updateCurrentLuminosityWithCurrentSystemLuminosity(getContentResolver());

        // update seek bar and check box
        this.updateLuminosityControls();

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

        // Create bluetoothSearch Class
        this.bluetoothSearch = new BluetoothService(this.bluetoothAdapter, this, this);

        // turn on/off bluetooth
        this.buttonBluetoothOnOff = (ToggleButton) findViewById(R.id.toggleBluetooth);

        // progress bar
        this.progressBarDiscovered = (ProgressBar) findViewById(R.id.progressBarDiscovered);
        this.progressBarDiscovered.setVisibility(View.INVISIBLE);

        // set the switch on/off
        this.buttonBluetoothOnOff.setChecked(this.bluetoothSearch.getBluetoothState());

        // if the phone has not bluetooth adapter
        this.buttonBluetoothOnOff.setEnabled(this.checkIFPhoneHasBluetoothAdapter());

        // get the bluetooth switch (paired / discover)
        this.switchBluetooth = (Switch) findViewById(R.id.discover_paired);

        // get bluetooth state
        this.bluetoothState = this.bluetoothAdapter.isEnabled();

        // get the listview
        this.listViewDevices =(ListView)findViewById(R.id.listViewDevices);

        // set the switch off/on
        this.switchBluetooth.setChecked(this.bluetoothState);

        // first display paired
        this.displayPairedDevices();

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

                // set / unset grey
                if(checkBoxAutoLux.isChecked()){
                    seekBarLuminiosity.setEnabled(false);
                }else{
                    seekBarLuminiosity.setEnabled(true);
                }
            }
        });


        // Bluetooth listeners
        // state listener
        this.buttonBluetoothOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothSearch.turnOnOffBluetooth(HomeActivity.this, buttonBluetoothOnOff.isChecked());
            }
        });


        // Switch listener
        switchBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Display paired devices
                if (switchBluetooth.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Display paired devices", Toast.LENGTH_LONG).show();
                    progressBarDiscovered.setVisibility(View.INVISIBLE);
                    displayPairedDevices();

                // Display discovered devices
                }else {
                    Toast.makeText(getApplicationContext(), "Display discovery devices", Toast.LENGTH_LONG).show();
                    progressBarDiscovered.setVisibility(View.VISIBLE);
                    displayDiscoveredDevices();
                }
            }
        });

        // On click list
        this.listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final BluetoothDevice selected = pairedDevices.get((int)id);

                Toast.makeText(getApplicationContext(), "Connecting to " + selected.getName() + "...", Toast.LENGTH_SHORT).show();

                // try to connect
                Thread tryConnection = new Thread() {
                    public void run() {
                        // bluetooth connection class
                        BluetoothConnection bluetoothConnection = new BluetoothConnection(bluetoothAdapter, selected);

                        // try to connect
                        bluetoothConnection.bluetoothConnect();

                        // if connected
                        //if(bluetoothConnection.isConnected()) {
                            Intent controlsPage = new Intent(HomeActivity.this, ControlsActivity.class);
                            controlsPage.putExtra("BluetoothDevice", selected);
                            startActivity(controlsPage);
                        //}
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
     * Try to connect to the selected device
     * @param device
     */
    private void tryConnect(BluetoothDevice device){
        // bluetooth connection class
        BluetoothConnection bluetoothConnection = new BluetoothConnection(bluetoothAdapter, device, this);

        // try to connect
        bluetoothConnection.bluetoothConnect();
    }

    /**
     * Update luminosity seek bar and check box
     */
    private void updateLuminosityControls(){
        // seek bar
        try {
            this.seekBarLuminiosity.setProgress(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            this.seekBarLuminiosity.setProgress(0);
        }

        // check box
        try {
            if(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1){
                this.checkBoxAutoLux.setChecked(true);
            }else{
                this.checkBoxAutoLux.setChecked(false);
            }
        } catch (Settings.SettingNotFoundException e) {
            this.checkBoxAutoLux.setChecked(false);
        }
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
     * Check if the phone has a light sensor
     * @return
     */
    private boolean checkIFPhoneHasLightSensor(){
        PackageManager PM= this.getPackageManager();
        return PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
    }
    /**
     * Check if the phone has a bluetooth adapter
     * @return
     */
    private boolean checkIFPhoneHasBluetoothAdapter(){
        PackageManager PM= this.getPackageManager();
        return PM.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.bluetoothSearch.onDestroy();
    }

    @Override
    public void onBluetoothDiscoveryFound(BluetoothDevice device) {
        // Add the new device
        this.discoveredDevices.add(device);

        // update the list
        this.listViewUpdate(this.discoveredDevices);
    }


    @Override
    public void onBluetoothConnection(int returnCode) {
        if(returnCode == Constants.BLUETOOTH_CONNECTED){
            Intent controlsPage = new Intent(HomeActivity.this, ControlsActivity.class);
            startActivity(controlsPage);
        }
    }

    @Override
    public void onReceiveData(String data) {

    }

    @Override
    public void onBluetoothDiscovery(int code) {
        switch (code){
            case Constants.BAR_FINISHED:
                progressBarDiscovered.setVisibility(View.INVISIBLE);
        }
    }
}