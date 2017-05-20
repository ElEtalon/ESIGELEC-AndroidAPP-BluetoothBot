package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Paul on 20/05/2017.
 */

public class BluetoothConnectThread extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothAdapter bluetoothAdapter;

    private static final UUID BLUE_UUID = UUID.fromString("00030000-0000-1000-8000-00805F9B34FB");

    public BluetoothConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
        this.bluetoothDevice = device;
        this.bluetoothAdapter = adapter;
        BluetoothSocket tmp = null;

        // Try to connect to the device
        try {
            tmp = device.createRfcommSocketToServiceRecord(BLUE_UUID);
        } catch (IOException e) {
            Log.e("BluetoothConnectThread", "connection failed", e);
        }

        // set the final value of the socket
        this.bluetoothSocket = tmp;
    }

    /**
     * Stop discovery mode if adapter is in discovery mode
     */
    private void stopDiscovery(){
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * With the openned socket, try to connect to the device
     */
    public boolean tryConnection() {
        Log.i("BluetoothConnectThread", "begin to connect...");

        // Prevent fail
        this.stopDiscovery();

        // Use the socket to create connection
        try {
            // Try to connect thanks to the socket instantiated in the constructor
            this.bluetoothSocket.connect();
        } catch (IOException e) {
            // Close the socket
            try {
                this.bluetoothSocket.close();
            } catch (IOException e2) {
                Log.e("BluetoothConnectThread", "Error when closing the socket", e2);
            }
            return false;
        }
       return true;
    }

    /**
     * Concel connection
     */
    public void cancel() {
        try {
            this.bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("BluetoothConnectThread", "Connection cancel failed", e);
        }
    }
}
