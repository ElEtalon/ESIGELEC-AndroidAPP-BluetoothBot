package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ControlsActivity extends AppCompatActivity {

    // Bluetooth var
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private boolean isConnected = false;
    private InputStream bTInputStream;
    private OutputStream bTOutputStream;
    private static final UUID BLUE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private TextView deviceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        // get the device name from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.bluetoothDevice = (BluetoothDevice) extras.get("BluetoothDevice");
        }

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // get title to change
        this.deviceName = (TextView) findViewById(R.id.deviceName);
        this.deviceName.setText(this.bluetoothDevice.getName());

        // connection
        if(bluetoothConnect(this.bluetoothDevice)) {
            Toast.makeText(getApplicationContext(), "Connection successful to " + this.bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Connection error to " + this.bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
            Intent controlsPage=new Intent(ControlsActivity.this, HomeActivity.class);
            controlsPage.putExtra("BluetoothDevice", this.bluetoothDevice);
            startActivity(controlsPage);
        }
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
            Toast.makeText(getApplicationContext(), "Error when opening the bluetooth stream " + e, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * bluetoothConnectThread
     * @param bluetoothDevice
     * @return
     */
    private boolean bluetoothConnectThread(BluetoothDevice bluetoothDevice) {
        this.bluetoothSocket = null;

        try {
            this.bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BLUE_UUID);
            return true;
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error when create connection socket " + e,Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * bluetoothConnect
     * @param bluetoothDevice
     * @return
     */
    private boolean bluetoothConnect(BluetoothDevice bluetoothDevice){
        if(this.bluetoothConnectThread(bluetoothDevice)) {
            if (this.bluetoothAdapter.isDiscovering()) {
                this.bluetoothAdapter.cancelDiscovery();
            }

            //Toast.makeText(getApplicationContext(), "Try to connect...", Toast.LENGTH_LONG).show();
            try {
                this.bluetoothSocket.connect();
                return true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error when connecting " + e, Toast.LENGTH_LONG).show();
                // Close the socket
                try {
                    this.bluetoothSocket.close();
                } catch (Exception e2) {
                    Toast.makeText(getApplicationContext(), "Error when closing the socket " + e, Toast.LENGTH_LONG).show();
                }
            }
        }
        return false;
    }
}
