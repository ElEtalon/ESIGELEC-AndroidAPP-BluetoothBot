package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ControlsActivity extends AppCompatActivity implements BluetoothCallback{

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

    private LuminosityControl luminosityControl;
    private SensorManager sensorManager;

    private RequeteHttp requeteHttp = new RequeteHttp();
    private String strRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        //-----------------------------------------------------------------------
        // get sensor manager
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Create luminiosity control class
        luminosityControl = new LuminosityControl(sensorManager);
        //----------------------------------------------------------------------
      
        // get the device name
        this.bluetoothDevice = (BluetoothDevice) DataModel.getInstance().connectedDevice;

        // bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // bluetooth connection class
        this.bluetoothConnection = new BluetoothConnection(this.bluetoothAdapter, this.bluetoothDevice, this);
        this.bluetoothConnection.setBluetoothSocket(DataModel.getInstance().connectedSocket);
        this.bluetoothConnection.bluetoothConnected(DataModel.getInstance().connectedSocket);

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
        Log.i("ControlsActivity", "Connection successful to " + this.bluetoothDevice.getName());
    }

    public void onClick(View v) {
        // Set the current luminosity with the current screen luminosity
        luminosityControl.updateCurrentLuminosityWithCurrentSystemLuminosity(getContentResolver());
        switch (v.getId()) {
            case R.id.GoBottom:
                Toast.makeText(getApplicationContext(), "Go Bottom", Toast.LENGTH_SHORT).show();
                strRequest = "action=reculer";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.GoForward:
                Toast.makeText(getApplicationContext(), "Go Forward", Toast.LENGTH_SHORT).show();
                strRequest = "action=avancer";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.GoLeft:
                Toast.makeText(getApplicationContext(), "Go Left", Toast.LENGTH_SHORT).show();
                strRequest = "action=gauche";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.GoRight:
                Toast.makeText(getApplicationContext(), "Go Right", Toast.LENGTH_SHORT).show();
                strRequest = "action=droite";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.ButtonMinus:
                Toast.makeText(getApplicationContext(), "Speed -", Toast.LENGTH_SHORT).show();
                strRequest = "action=speed--";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.ButtonPlus:
                Toast.makeText(getApplicationContext(), "Speed +", Toast.LENGTH_SHORT).show();
                strRequest = "action=speed++";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.ImageButtonPower:
                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
                strRequest = "action=stop";
                requeteHttp.executerRequete(strRequest);
                break;
            case R.id.SwitchMode:
                Toast.makeText(getApplicationContext(), "Switched", Toast.LENGTH_SHORT).show();
                strRequest = "action=switch";
                requeteHttp.executerRequete(strRequest);
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

    @Override
    public void onBluetoothConnection(int returnCode) {
        if(returnCode == Constants.BLUETOOTH_CONNECTED_ERROR){
            Log.d("ControlsActivity", "Disconnected - go to home");
            Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
            Intent homePage = new Intent(ControlsActivity.this, HomeActivity.class);
            startActivity(homePage);
        }
    }

    @Override
    public void onReceiveData(String data) {
        updateConsole(data);
    }

    @Override
    public void onBluetoothDiscovery(int code) {

    }

    @Override
    public void onBluetoothDiscoveryFound(BluetoothDevice device) {

    }
}
