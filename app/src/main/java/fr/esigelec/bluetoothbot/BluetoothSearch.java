package fr.esigelec.bluetoothbot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Paul on 16/05/2017.
 */

public class BluetoothSearch {

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;
    private boolean bluetoothState;
    private Activity activity;

    private ArrayList<BluetoothDevice> discoveredDevices;
    private ArrayList<BluetoothDevice> pairedDevices;

    public BluetoothSearch(BluetoothAdapter adapter, Activity activity){
        this.bluetoothAdapter   = adapter;
        this.activity           = activity;
        this.discoveredDevices  = new ArrayList<BluetoothDevice>();
        this.pairedDevices      = new ArrayList<BluetoothDevice>();
        this.bluetoothState     = this.bluetoothAdapter.isEnabled();
    }

    /**
     * Get bluetooth state
     * @return
     */
    public boolean getBluetoothState(){
        return this.bluetoothState;
    }

    /**
     * Turn on/off bluetooth
     * @param
     */
    public void turnOnOffBluetooth(HomeActivity home, boolean mode) {
        // on
        if(mode){
            if (!this.bluetoothAdapter.isEnabled()){
                this.bluetoothAdapter.enable();
                /*Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                home.startActivityForResult(turnOn, 0);*/
                Toast.makeText(home.getApplicationContext(), "Bluetooth turned on",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(home.getApplicationContext(), "Bluetooth already on", Toast.LENGTH_LONG).show();
            }
            // off
        }else{
            if (this.bluetoothAdapter.isEnabled()){
                this.bluetoothAdapter.disable();
                Toast.makeText(home.getApplicationContext(), "Bluetooth turned off",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(home.getApplicationContext(), "Bluetooth already off", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * get pairedDevices arrayList
     * @return
     */
    public ArrayList<BluetoothDevice> getListPairedBluetooth(){
        this.updateListPairedBluetooth();
        return this.pairedDevices;
    }

    /**
     * get discoveredDevices arrayList
     * @return
     */
    public ArrayList<BluetoothDevice> getListDiscoveredBluetooth(){
        this.updateListDiscoveredBluetooth();
        return this.discoveredDevices;
    }

    /**
     * Get the list of the bluetooth devices
     * @param
     */
    private void updateListPairedBluetooth(){
        this.pairedDevices = new ArrayList<BluetoothDevice>();
        // get list of devices
        Set<BluetoothDevice> paired = this.bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : paired){
            this.pairedDevices.add(bt);
        }

    }


    /**
     * Begin bluetooth device discovery
     * @param
     */
    private void updateListDiscoveredBluetooth(){
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        this.bluetoothAdapter.startDiscovery();

        // init array
        this.discoveredDevices = new ArrayList<BluetoothDevice>();

        /**
         * Bluetooth Receiver from discovery
         */
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Notification
                    //Toast.makeText(getApplicationContext(), "Device found",Toast.LENGTH_LONG).show();
                    Log.i("BluetoothSearch", "Device found");

                    // Create a new device item
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Add to the list
                    discoveredDevices.add(device);
                }

                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                    Log.i("BluetoothSearch", "Discovery ended");
                }
            }
        };

        this.activity.registerReceiver(this.broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.activity.registerReceiver(this.broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }


    // On destroy
    protected void onDestroy(){
        this.bluetoothAdapter.cancelDiscovery();
        this.activity.unregisterReceiver(this.broadcastReceiver);
    }

}
