package fr.esigelec.bluetoothbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;

public class HomeActivity extends AppCompatActivity {

    ImageButton imgAboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

    /*
    public void onClick(View v){
        if(v.getId()==R.id.imageAboutButton) {
            Toast.makeText(getApplicationContext(), "About button clicked", Toast.LENGTH_LONG).show();
        }
    }*/
}
