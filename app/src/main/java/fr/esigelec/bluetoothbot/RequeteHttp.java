package fr.esigelec.bluetoothbot;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequeteHttp extends AsyncTask<TextView, Void, String> {

    TextView textConnectivity;

    @Override
    protected String doInBackground(TextView... params){
        textConnectivity = params[0];
        return "ok";//executerRequete(strRequest);
    }

    @Override
    protected void onPostExecute(String result){
        //textConnectivity.setText(result);
    }

    public String executerRequete(String strRequest) {
        HttpURLConnection urlConnection = null;
        String webcontent = null;

        try{
            String strBaseRequest = "http://cabani.net/ise/adddata.php?idproject=8&lux=10&timestamp=1488534460&"; //action=avancer
            strBaseRequest = strBaseRequest + strRequest;
            URL url = new URL(strBaseRequest);         //"http://ip-api.com/json/192.52.189.194"
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            webcontent = generateString(in);
            Log.d("TP", webcontent);
            Log.d("RequeteHttp", "Success to get the webcontent");
        }
        catch(IOException e){
            e.printStackTrace();
            Log.e("RequeteHttp", "Error when attempt to 'openconnect'", e);
        }
        finally{
            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
        return webcontent;
    }

    private String generateString(InputStream stream)
    {
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        try{
            String cur;
            while((cur = buffer.readLine()) != null) {
                sb.append(cur + System.getProperty("line.separator"));
                stream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
