package fr.esigelec.bluetoothbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ControlsActivity extends AppCompatActivity {

    String device_name;
    TextView deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        // get the device name from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            device_name = extras.getString("device_name");
        }

        // get title to change
        deviceName = (TextView) findViewById(R.id.deviceName);
        deviceName.setText(device_name);
    }
}
