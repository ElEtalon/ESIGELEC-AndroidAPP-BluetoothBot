package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ControlsActivity extends AppCompatActivity {

    BluetoothDevice bluetoothDevice;
    TextView deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        // get the device name from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bluetoothDevice = (BluetoothDevice) extras.get("BluetoothDevice");
        }

        // get title to change
        deviceName = (TextView) findViewById(R.id.deviceName);
        deviceName.setText(bluetoothDevice.getName());
    }
}
