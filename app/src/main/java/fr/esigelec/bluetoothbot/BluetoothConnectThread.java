package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Paul on 20/05/2017.
 */

public class BluetoothConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    private static final UUID BLUE_UUID = UUID.fromString("00030000-0000-1000-8000-00805F9B34FB");

    public BluetoothConnectThread(BluetoothDevice device) {
        mmDevice = device;
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(BLUE_UUID);
        } catch (IOException e) {
            Log.e("BluetoothConnectThread", "create() failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        Log.i("BluetoothConnectThread", "BEGIN mConnectThread");
        //setName("ConnectThread");

        // Always cancel discovery because it will slow down a connection
        mAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            mmSocket.connect();
        } catch (IOException e) {
            connectionFailed();
            // Close the socket
            try {
                mmSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
            // Start the service over to restart listening mode
            BluetoothCommandService.this.start();
            return;
        }

        // Reset the ConnectThread because we're done
        synchronized (BluetoothCommandService.this) {
            mConnectThread = null;
        }

        // Start the connected thread
        connected(mmSocket, mmDevice);
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
