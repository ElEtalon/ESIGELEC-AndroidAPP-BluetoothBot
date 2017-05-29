package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Paul on 20/05/2017.
 */

public class BluetoothConnectThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private boolean connected;

    private static final UUID BLUE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
        this.bluetoothDevice = device;
        this.bluetoothAdapter = adapter;
        this.connected = false;
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
     *
     * @return
     */
    public boolean getConnected(){
        return this.connected;
    }

    /**
     *
     * @return
     */
    public BluetoothSocket getSocket(){
        return this.bluetoothSocket;
    }

    /**
     * With the openned socket, try to connect to the device
     */
    @Override
    public void run() {
        Log.i("BluetoothConnectThread", "begin to connect...");

        // Prevent fail
        this.stopDiscovery();

        BluetoothSocket socket = null;
        BluetoothSocket socketFallback;

        // Try to connect to the device
        try {
            socket = this.bluetoothDevice.createRfcommSocketToServiceRecord(BLUE_UUID);
            socket.connect();
            this.connected = true;
        } catch (Exception e) {
            Log.e("BluetoothConnectThread", "Error when opening socket, try with falling back..", e);
            Class<?> clazz = socket.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                socketFallback = (BluetoothSocket) m.invoke(socket.getRemoteDevice(), params);
                socketFallback.connect();
                this.connected = true;
            } catch (Exception e2) {
                Log.e("BluetoothConnectThread", "Error when conection with fallback...", e2);
                this.connected = false;
            }
        }

        // set the final value of the socket
        this.bluetoothSocket = socket;
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
