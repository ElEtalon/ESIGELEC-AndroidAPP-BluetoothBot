package fr.esigelec.bluetoothbot;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.Manifest;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by pmocq on 19/05/2017.
 */

public class Connectivity {

    public Connectivity(TextView TextConnectivity, ConnectivityManager connectivityManager)
    {
        try
        {
            //android.permission.ACCESS_NETWORK_STATE;   VOIR Android Manifest
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            TextConnectivity.setText(networkInfo.getTypeName());
        }
        catch(Exception e)
        {
            TextConnectivity.setText("getActiveNetworkInfo failed");
            Log.e("Connectivity", "Error when attempt to 'get active network info'", e);
        }
    }
}

