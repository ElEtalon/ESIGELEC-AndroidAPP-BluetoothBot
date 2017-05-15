package fr.esigelec.bluetoothbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    ImageButton imgAboutButton;
    ListView listViewDevices;
    final ArrayList<String> deviceList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /// LIST VIEW
        // Devices
        listViewDevices =(ListView)findViewById(R.id.listViewDevices);

        // get list of devices
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for(BluetoothDevice bt : pairedDevices)
            deviceList.add(bt.getName());

        listViewDevices.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList));


        // ON CLICK
        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "ListView item : " + position + " & id : " + deviceList.get((int)id), Toast.LENGTH_LONG).show();
                Intent controlsPage=new Intent(HomeActivity.this, ControlsActivity.class);
                controlsPage.putExtra("device_name",deviceList.get((int)id));
                startActivity(controlsPage);
            }
        });

        imgAboutButton =(ImageButton)findViewById(R.id.imageAboutButton);
        imgAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "About button clicked", Toast.LENGTH_LONG).show();
                //Log.i("clicks","You Clicked B1");
                Intent aboutPage=new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(aboutPage);
            }
        });
    }
}
