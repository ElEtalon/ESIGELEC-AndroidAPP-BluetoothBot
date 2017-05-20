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
     * Return true if connected to the device
     * @return
     */
    public boolean isConnected(){
        if(this.bluetoothState == STATE_CONNECTED){
            return true;
        }
        return false;
    }

    /**
     * Stop all bluetooth connection threads
     */
    public void stop() {
        Log.d("BluetoothConnection", "stop");

            if (bluetoothConnectThread != null) {
            this.bluetoothConnectThread.cancel();
            this.bluetoothConnectThread = null;
        }
        if (bluetoothConnectedThread != null) {
            this.bluetoothConnectedThread.cancel();
            this.bluetoothConnectedThread = null;
        }

        this.setState(STATE_NONE);
    }

    /**
     * Called when connection failed
     * TODO : add toast into homeactivity
     */
    private void connectionFailed() {
        this.stop();
        this.setState(STATE_LISTEN);
    }

    /**
     * Called when connection is lost
     * TODO : go back to homeActivity
     */
    private void connectionLost(){
        this.stop();
        this.setState(STATE_LISTEN);
    }

    /**
     * Called when connection is successful
     */
    private void connectionSuccessful(){
        // reset the connect thread
        this.bluetoothConnectThread = null;
    }

    /**
     * Start the connected thread to manage exchanges
     */
    public void bluetoothConnected(){
        Log.d("BluetoothConnection", "connected");

        // Cancel all threads
        this.stop();

        // Start the thread to manage the connection and perform transmissions
        this.bluetoothConnectedThread = new BluetoothConnectedThread(this.bluetoothSocket);
        this.bluetoothConnectedThread.connected();

        // set the state to connected
        this.setState(STATE_CONNECTED);
    }

    /**
     * bluetoothConnect
     */
    public void bluetoothConnect(){
        Log.d("BluetoothConnection", "connect to: " + this.bluetoothDevice);

        // Cancel all threads
        this.stop();

        // Start the thread to connect with the given device
        this.bluetoothConnectThread = new BluetoothConnectThread(this.bluetoothAdapter, this.bluetoothDevice);

        // if connection is successful
        if(this.bluetoothConnectThread.tryConnection()){
            // set state to connection
            this.setState(STATE_CONNECTING);

            // reset the thread
            this.connectionSuccessful();

            // Start the connected thread
            this.bluetoothConnected();
        }else{
            // return to listenning mode
            this.connectionFailed();
        }
    }
}
