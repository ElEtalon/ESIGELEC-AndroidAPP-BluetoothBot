package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Paul on 20/05/2017.
 */

public class BluetoothConnectedThread extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream bluetoothInStream;
    private final OutputStream bluetoothOutStream;

    public BluetoothConnectedThread(BluetoothSocket socket) {
        Log.d("ConnectedThread", "Connected Thread created");
        this.bluetoothSocket = socket;

        // create temp variable for trying
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Try to create input and output stream
        try {
            tmpIn = this.bluetoothSocket.getInputStream();
            tmpOut = this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Log.e("ConnectedThread", "Error when creating streams", e);
        }

        // assign final value to streams
        this.bluetoothInStream = tmpIn;
        this.bluetoothOutStream = tmpOut;
    }

    /**
     * Manage bluetooth exchanges between the phone and the robot
     */
    public void connected() {
        Log.i("ConnectedThread", "begin Connected Thread");
        byte[] buffer = new byte[1024];

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                int bytes = this.bluetoothInStream.read(buffer);
            } catch (IOException e) {
                Log.e("ConnectedThread", "Disconnected", e);
                break;
            }
        }
    }

    /**
     * Send bytes
     * @param buffer
     */
    public void writeByte(byte[] buffer) {
        try {
            this.bluetoothOutStream.write(buffer);
        } catch (IOException e) {
            Log.e("ConnectedThread", "Error when writing bytes", e);
        }
    }

    /**
     * Send command to the robot
     * @param out
     */
    public void sendCommand(int command) {
        try {
            this.bluetoothOutStream.write(command);
        } catch (IOException e) {
            Log.e("ConnectedThread", "Error when sending command", e);
        }
    }

    /**
     * Cancel connection
     */
    public void cancel() {
        try {
            this.bluetoothOutStream.write(-1); //EXIT_CMD
            this.bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("ConnectedThread", "Connection cancel failed", e);
        }
    }
}
