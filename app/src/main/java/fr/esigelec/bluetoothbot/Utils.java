package fr.esigelec.bluetoothbot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by Paul on 19/05/2017.
 */

public class Utils {

    /**
     * Test permission
     */
    public static void testPermission(Activity activity, String permission){
        Intent intent = new Intent(permission);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
