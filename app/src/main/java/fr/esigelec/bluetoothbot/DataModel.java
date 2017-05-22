package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Paul on 22/05/2017.
 */

class DataModel {
    private static final DataModel ourInstance = new DataModel();

    public BluetoothDevice connectedDevice;

    static DataModel getInstance() {
        return ourInstance;
    }

    private DataModel() {
    }
}
