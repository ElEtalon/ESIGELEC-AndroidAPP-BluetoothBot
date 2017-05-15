package fr.esigelec.bluetoothbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void doGoAbout(View v)
    {
        Toast.makeText(this, "Button Clicked", Toast.LENGTH_LONG).show();
    }
}
