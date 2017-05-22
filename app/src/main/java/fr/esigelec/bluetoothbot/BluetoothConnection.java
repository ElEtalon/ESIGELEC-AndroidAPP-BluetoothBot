package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnection {

    private final BluetoothCallback bluetoothCallback;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private InputStream bTInputStream;
    private OutputStream bTOutputStream;

    private BluetoothConnectThread bluetoothConnectThread;
    private BluetoothConnectedThread bluetoothConnectedThread;
    private int bluetoothState;

    /**
     * Constructor
     * @param adapter
     * @param device
     */
    public BluetoothConnection(BluetoothAdapter adapter, BluetoothDevice device, BluetoothCallback callback){
        this.bluetoothAdapter = adapter;
        this.bluetoothDevice = device;
        this.bluetoothState = Constants.STATE_NONE;
        this.bluetoothCallback  = callback;
    }

    /**
     * Set new state
     * @param state
     */
    private void setState(int state){
        switch(state){
            case Constants.BLUETOOTH_CONNECTED:
                this.bluetoothCallback.onBluetoothConnection(Constants.BLUETOOTH_CONNECTED);

        }
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
        return this.bluetoothState == Constants.getInstance().BLUETOOTH_CONNECTED;
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

        this.setState(Constants.getInstance().STATE_NONE);
    }

    /**
     * Called when connection failed
     * TODO : add toast into homeactivity
     */
    private void connectionFailed() {
        this.stop();
        this.setState(Constants.getInstance().BLUETOOTH_CONNECTABLE);
    }

    /**
     * Called when connection is lost
     * TODO : go back to homeActivity
     */
    private void connectionLost(){
        this.stop();
        this.setState(Constants.getInstance().BLUETOOTH_CONNECTABLE);
    }

    /**
     * Called when connection is successful
     */
    private void connectionSuccessful(){
        // set device in data model
        DataModel.getInstance().connectedDevice = this.bluetoothDevice;

        // get connected socket
        this.bluetoothSocket = this.bluetoothConnectThread.getSocket();

        // set state to connection
        this.setState(Constants.getInstance().BLUETOOTH_CONNECTED);

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
        this.bluetoothConnectedThread.run();

        // set the state to connected
        this.setState(Constants.getInstance().BLUETOOTH_CONNECTED_ERROR);
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
        this.bluetoothConnectThread.run();

        if(this.bluetoothConnectThread.getConnected()){
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
