package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothDevice;

/**
 * Interface definition for a callback to be invoked when bluetooth state changed.
 */
public interface BluetoothCallback {
        /**
     * Called when the bluetooth is off.
     */
    //void onBluetoothOff();

    /**
     * Called when the bluetooth is turning on.
     */
    //void onBluetoothTurningOn();

    /**
     * Called when the bluetooth is on, and ready for use.
     */
    //void onBluetoothOn();

    /**
     * Called when the bluetooth is turning off.
     */
    //void onBluetoothTurningOff();

    /*
     * Callback to see how the connection between the devices has been
     */
    void onBluetoothConnection(int returnCode);

    /*
     * Callback to receive data between a BT connection
     */
    void onReceiveData(String data);

    /*
     * Callback in order to see how the Discovering is behaving
     */
    //void onBluetoothDiscovery(int returnCode);

    /**
     *
     */
    void onBluetoothDiscoveryFound(BluetoothDevice device);
}