package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

//inspirate from https://github.com/luugiathuy/Remote-Bluetooth-Android/blob/master/RemoteBluetooth/src/com/luugiathuy/apps/remotebluetooth/BluetoothCommandService.java
public class BluetoothConnection {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private InputStream bTInputStream;
    private OutputStream bTOutputStream;

    private BluetoothConnectThread bluetoothConnectThread;
    private BluetoothConnectedThread bluetoothConnectedThread;
    private int bluetoothState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    // Constants that indicate command to the robot
    public static final int EXIT_CMD = -1;
    public static final int GO_UP = 1;
    public static final int GO_DOWN = 2;
    public static final int GO_RIGHT = 3;
    public static final int GO_LEFT = 4;
    public static final int STOP = 5;
    public static final int SPEED_UP = 6;
    public static final int SPEED_DOWN = 7;
    public static final int MANUAL_MODE = 8;
    public static final int AUTO_MODE = 9;

    /**
     * Constructor
     * @param adapter
     * @param device
     */
    public BluetoothConnection(BluetoothAdapter adapter, BluetoothDevice device){
        this.bluetoothAdapter = adapter;
        this.bluetoothDevice = device;
        this.bluetoothState = STATE_NONE;
    }

    /**
     * Set new state
     * @param state
     */
    private void setState(int state){
        this.bluetoothState = state;
    }

    /**
     * Return the current connection state
     */
    public int getState() {
        return this.bluetoothState;
    }

    /**
     * Stop all bluetooth connection threads
     */
    public void stop() {
        if (D) Log.d("BluetoothConnection", "stop");

        if (bluetoothConnectThread != null) {
            bluetoothConnectThread.cancel();
            bluetoothConnectThread = null;
        }
        if (bluetoothConnectedThread != null) {
            bluetoothConnectedThread.cancel();
            bluetoothConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Called when connection failed
     * TODO : add toast into homeactivity
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
    }

    /**
     * Called when connection is lost
     * TODO : go back to homeActivity
     */
    private void connectionLost(){
        setState(STATE_LISTEN);
    }

    /**
     * Start the connected thread to manage exchanges
     * @param socket
     * @param device
     */
    public void bluetoothConnected(BluetoothSocket socket, BluetoothDevice device){
        if (D) Log.d("BluetoothConnection", "connected");

        // Cancel all threads
        this.stop();

        // Start the thread to manage the connection and perform transmissions
        bluetoothConnectedThread = new BluetoothConnectedThread(bluetoothSocket);
        bluetoothConnectedThread.start();

        // set the state to connected
        setState(STATE_CONNECTED);
    }

    /**
     * bluetoothConnect
     * @param bluetoothDevice
     */
    public void bluetoothConnect(BluetoothDevice bluetoothDevice){
        if (D) Log.d("BluetoothConnection", "connect to: " + bluetoothDevice);

        // Cancel all threads
        this.stop();

        // Start the thread to connect with the given device
        bluetoothConnectThread = new BluetoothConnectThread(bluetoothDevice);
        bluetoothConnectThread.start();

        // set state to connection
        setState(STATE_CONNECTING);
    }
}
