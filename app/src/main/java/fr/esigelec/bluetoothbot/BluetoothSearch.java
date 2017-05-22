package fr.esigelec.bluetoothbot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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
    private boolean bluetoothState;
    private Activity activity;
    private BluetoothCallback bluetoothCallback;

    private ArrayList<BluetoothDevice> discoveredDevices;
    private ArrayList<BluetoothDevice> pairedDevices;

    public BluetoothSearch(BluetoothAdapter adapter, Activity activity, BluetoothCallback callback){
        this.bluetoothAdapter   = adapter;
        this.activity           = activity;
        this.discoveredDevices  = new ArrayList<BluetoothDevice>();
        this.pairedDevices      = new ArrayList<BluetoothDevice>();
        this.bluetoothState     = this.bluetoothAdapter.isEnabled();
        this.bluetoothCallback  = callback;
        this.stopDiscovery();
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
     * Stop discovery mode if adapter is in discovery mode
     */
    private void stopDiscovery(){
        try{
            this.activity.unregisterReceiver(this.broadcastReceiver);
        } catch(Exception e){
            Log.e("BluetoothSearch", "Try to unregister failed");
        }

        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * Begin bluetooth device discovery
     * @param
     */
    private void updateListDiscoveredBluetooth(){
        this.stopDiscovery();
        this.bluetoothAdapter.startDiscovery();

        // init array
        this.discoveredDevices = new ArrayList<BluetoothDevice>();

        BluetoothManager manager = new android.bluetooth.BluetoothManager();

        /*this.activity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        this.activity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        this.activity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        this.activity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        this.bluetoothAdapter.startDiscovery();*/

    }

    /**
     * Bluetooth Receiver from discovery
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("BluetoothSearch", "Action:"+action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Notification"
                Toast.makeText(activity.getApplicationContext(), "Device found",Toast.LENGTH_LONG).show();
                Log.i("BluetoothSearch", "Device found");

                // Create a new device item
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevices.add(device);
                }

                bluetoothCallback.onBluetoothDiscoveryFound(device);

            }

            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                Log.i("BluetoothSearch", "MODE : " + mode);
            }

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                if (discoveredDevices.size() == 0) {
                    Log.i("BluetoothSearch", "No device found");
                }
                Toast.makeText(activity.getApplicationContext(), "Discovery ended",Toast.LENGTH_LONG).show();
                Log.i("BluetoothSearch", "Discovery ended");
            }
        }
    };

    // On destroy
    protected void onDestroy(){
        this.bluetoothAdapter.cancelDiscovery();
        this.activity.unregisterReceiver(this.broadcastReceiver);
    }

}
