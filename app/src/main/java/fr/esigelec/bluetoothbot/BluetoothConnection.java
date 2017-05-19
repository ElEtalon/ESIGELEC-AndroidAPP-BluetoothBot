package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothConnection {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private InputStream bTInputStream;
    private OutputStream bTOutputStream;

    private static final UUID BLUE_UUID = UUID.fromString("00030000-0000-1000-8000-00805F9B34FB");

    public BluetoothConnection(BluetoothAdapter adapter, BluetoothDevice device){
        this.bluetoothAdapter = adapter;
        this.bluetoothDevice = device;
    }


    /**
     * bluetoothStream
     * @param bluetoothDevice
     */
    private void bluetoothStream(BluetoothDevice bluetoothDevice){
        this.bTInputStream   = null;
        this.bTOutputStream  = null;
        try {
            this.bTOutputStream = this.bluetoothSocket.getOutputStream();
            this.bTInputStream  = this.bluetoothSocket.getInputStream();
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "Error when opening the bluetooth stream " + e, Toast.LENGTH_LONG).show();
            Log.e("BluetoothConnection", "Error when opening the bluetooth stream " + e);
        }
    }

    /**
     * bluetoothConnect
     * @param bluetoothDevice
     * @return
     */
    public boolean bluetoothConnect(BluetoothDevice bluetoothDevice){
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }

        this.bluetoothSocket = null;

        try {
            this.bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BLUE_UUID);
            this.bluetoothSocket.connect();
        } catch (Exception e){
            //Toast.makeText(getApplicationContext(), "Error when create connection socket " + e,Toast.LENGTH_LONG).show();
            Log.e("BluetoothConnection", "Error when create connection socket " + e);
            //Toast.makeText(getApplicationContext(), "Try to connect...", Toast.LENGTH_LONG).show();
            Log.i("BluetoothConnection", "Try to connect by fallback...");
            try {
                this.bluetoothSocket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                this.bluetoothSocket.connect();
            } catch (Exception e1) {
                Log.e("BluetoothConnection", "Error when closing the socket " + e1);
                //Toast.makeText(getApplicationContext(), "Error when closing the socket " + e, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }
}
