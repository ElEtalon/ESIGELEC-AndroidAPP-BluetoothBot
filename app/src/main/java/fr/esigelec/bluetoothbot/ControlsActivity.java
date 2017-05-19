package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ControlsActivity extends AppCompatActivity {

    // Bluetooth var
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnection bluetoothConnection;

    private TextView deviceName;
    private TextView console;
    private Switch switchMode;
    private Button goBottom;
    private Button goForward;
    private Button goLeft;
    private Button goRight;
    private Button buttonPlus;
    private Button buttonMinus;
    private ImageButton imageButtonPower;

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

        // get title to change & init console
        this.deviceName = (TextView) findViewById(R.id.deviceName);
        this.console    = (TextView) findViewById(R.id.textViewConsole);
        this.deviceName.setText(this.bluetoothDevice.getName());
        this.console.setText("> console started");

        // get buttons
        this.switchMode         = (Switch) findViewById(R.id.SwitchMode);
        this.goBottom           = (Button) findViewById(R.id.GoBottom);
        this.goForward          = (Button) findViewById(R.id.GoForward);
        this.goLeft             = (Button) findViewById(R.id.GoLeft);
        this.goRight            = (Button) findViewById(R.id.GoRight);
        this.buttonMinus        = (Button) findViewById(R.id.ButtonMinus);
        this.buttonPlus         = (Button) findViewById(R.id.ButtonPlus);
        this.imageButtonPower   = (ImageButton) findViewById(R.id.ImageButtonPower);

        // show connection success
        Toast.makeText(getApplicationContext(), "Connection successful to " + this.bluetoothDevice.getName(), Toast.LENGTH_LONG).show();

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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.GoBottom:
                Toast.makeText(getApplicationContext(), "Go Bottom", Toast.LENGTH_SHORT).show();
                break;
            case R.id.GoForward:
                Toast.makeText(getApplicationContext(), "Go Forward", Toast.LENGTH_SHORT).show();
                break;
            case R.id.GoLeft:
                Toast.makeText(getApplicationContext(), "Go Left", Toast.LENGTH_SHORT).show();
                break;
            case R.id.GoRight:
                Toast.makeText(getApplicationContext(), "Go Right", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ButtonMinus:
                Toast.makeText(getApplicationContext(), "Speed -", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ButtonPlus:
                Toast.makeText(getApplicationContext(), "Speed +", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ImageButtonPower:
                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SwitchMode:
                Toast.makeText(getApplicationContext(), "Switched", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * Add string to the console
     * @param toAdd
     */
    private void updateConsole(String toAdd){
        this.console.append(toAdd);
    }
}
