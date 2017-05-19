package fr.esigelec.bluetoothbot;

import android.content.ContentResolver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;

import static android.hardware.SensorManager.*;

/**
 * Created by Paul on 19/05/2017.
 */

public class LuminosityControl {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private float currentLuminosity;
    private SensorEventListener luminosityListener;
    private boolean luminosityMode;

    private static int maxLum = 255;

    /**
     * Constructor
     * @param sensorManager
     */
    public LuminosityControl(SensorManager sensorManager){
        this.sensorManager = sensorManager;

        // false = manual mode
        this.luminosityMode = false;

        // Get light sensor
        this.lightSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    /**
     * Set the current luminosity with the current screen luminosity
     * @param resolver
     */
    public void updateCurrentLuminosityWithCurrentSystemLuminosity(ContentResolver resolver){
        try {
            this.currentLuminosity = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * set new luminosity
     * @param newLum
     */
    public void setCurrentLuminosity(float newLum){
        if(this.currentLuminosity + maxLum > 255){
            this.currentLuminosity = maxLum;
        }else{
            this.currentLuminosity += newLum;
        }
    }

    /**
     * return current luminosity
     * @return
     */
    public float getCurrentLuminosity(){
        return this.currentLuminosity;
    }

    /**
     * Set new luminosity value
     * @param resolver
     * @param progress
     */
    public void setNewLuminosity(ContentResolver resolver, int progress){
        // update attribute
        this.setCurrentLuminosity(progress);

        // update system new luminosity
        changeCurrentSystemLuminosity(resolver, progress);
    }

    private void changeCurrentSystemLuminosity(ContentResolver resolver, int lux){
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, lux);
    }

    /**
     * Set Mode auto change luminosity on sensor change
     */
    private void autoModeLuminosity(final ContentResolver resolver){
         this.luminosityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                setCurrentLuminosity(event.values[0]);

                // update system new luminosity
                changeCurrentSystemLuminosity(resolver, (int) currentLuminosity);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // void
            }
        };

        // register listener
        this.sensorManager.registerListener(this.luminosityListener, this.lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Stop autoMode by deleting listener
     */
    private void stopAutoModeLuminosity(){
        if(this.luminosityListener != null){
            this.sensorManager.unregisterListener(this.luminosityListener);
        }
    }

    /**
     * change luminosity mode (manual / auto)
     * @param newMode
     */
    public void setLuminosityMode(Boolean newMode, ContentResolver resolver){
        this.luminosityMode = newMode;
        if(this.luminosityMode){
            this.autoModeLuminosity(resolver);
        }else{
            this.stopAutoModeLuminosity();
        }
    }

}
