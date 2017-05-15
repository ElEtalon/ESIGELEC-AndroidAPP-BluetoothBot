package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Paul on 15/05/2017.
 */

public class BluetoothConnection {

    BluetoothAdapter adapter;

    public BluetoothConnection (BluetoothAdapter bluetoothAdapter){
        this.adapter = bluetoothAdapter;
    }

    /**
     * Get the list of the bluetooth devices
     * @param
     */
    public ArrayList<String> getListPairedBluetooth(ArrayList<String> deviceList){
        // get list of devices
        Set<BluetoothDevice> pairedDevices = this.adapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
            deviceList.add(bt.getName());
        return deviceList;
    }

    public void getListDiscoveredBluetooth(){
        if (this.adapter.isDiscovering()) {
            this.adapter.cancelDiscovery();
        }
        this.adapter.startDiscovery();
    }
}
