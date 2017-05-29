package fr.esigelec.bluetoothbot;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by pmocq on 22/05/2017.
 */

public class FireBaseToken extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        //exemple token: ePxqKjkJEEk:APA91bGlzlocQ7WlfwG6y0FzPi_hldO7SlWyL-MQEpp6qTAEaR9N6KtwuQkNsX37gfIJKSKw6qoKO7L-9a35XCv17klMdD5d4pQb8ZB3hw_kCvl432OmL-6oK7rIM1YCqylVA1TYCoZS
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
        Log.d("Jean-Jacques", "Refreshed Token: "+ refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
    }
}

