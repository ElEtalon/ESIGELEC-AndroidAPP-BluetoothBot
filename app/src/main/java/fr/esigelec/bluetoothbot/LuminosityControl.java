package fr.esigelec.bluetoothbot;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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
     * Set new luminosity value
     * @param newLuminosity
     */
    public void setCurrentLuminosity(float newLuminosity){
        this.currentLuminosity = newLuminosity;
    }

    /**
     * Set Mode auto change luminosity on sensor change
     */
    private void autoModeLuminosity(){
         this.luminosityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                setCurrentLuminosity(event.values[0]);
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
    public void setLuminosityMode(Boolean newMode){
        this.luminosityMode = newMode;
        if(this.luminosityMode){
            this.autoModeLuminosity();
        }else{
            this.stopAutoModeLuminosity();
        }
    }
}
