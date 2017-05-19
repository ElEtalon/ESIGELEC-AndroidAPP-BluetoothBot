package fr.esigelec.bluetoothbot;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by pmocq on 19/05/2017.
 */

public class Connectivity {

    public Connectivity(TextView TextConnectivity, ConnectivityManager connectivityManager)
    {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //TextView tv = (TextView) findViewById(R.id.TextConnectivity);
        if(networkInfo != null)
        {
            TextConnectivity.setText(networkInfo.getTypeName());
        }
        else
        {
            TextConnectivity.setText("Pas de réseau !");
        }
    }
}

