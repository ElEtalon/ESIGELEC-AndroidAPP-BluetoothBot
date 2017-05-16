package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ControlsActivity extends AppCompatActivity {

    // Bluetooth var
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnection bluetoothConnection;

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

        // bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // bluetooth connection class
        this.bluetoothConnection = new BluetoothConnection(this.bluetoothAdapter, this.bluetoothDevice);

        // get title to change
        this.deviceName = (TextView) findViewById(R.id.deviceName);
        this.deviceName.setText(this.bluetoothDevice.getName());

        // show connection success
        Toast.makeText(getApplicationContext(), "Connection successful to " + this.deviceName, Toast.LENGTH_LONG).show();

        // check if connected
        /*Thread tryConnection = new Thread() {
            public void run() {
                // connection
                if(bluetoothConnection.bluetoothConnect(bluetoothDevice)) {
                    Toast.makeText(getApplicationContext(), "Connection successful to " + bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Error when connecting to " + bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                    Intent controlsPage=new Intent(ControlsActivity.this, HomeActivity.class);
                    controlsPage.putExtra("BluetoothDevice", bluetoothDevice);
                    startActivity(controlsPage);
                }
            }
        };

        tryConnection.start();*/


    }
}
