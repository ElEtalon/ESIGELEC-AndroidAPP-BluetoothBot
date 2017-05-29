package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by Paul on 22/05/2017.
 */

class DataModel {
    private static final DataModel ourInstance = new DataModel();

    public BluetoothDevice connectedDevice;
    public BluetoothSocket connectedSocket;

    static DataModel getInstance() {
        return ourInstance;
    }

    private DataModel() {
    }
}
