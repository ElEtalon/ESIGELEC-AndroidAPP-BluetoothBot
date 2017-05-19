package fr.esigelec.bluetoothbot;

import android.hardware.Sensor;
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

    /**
     * Constructor
     * @param sensorManager
     */
    public LuminosityControl(SensorManager sensorManager){
        this.sensorManager = sensorManager;

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
     * Update luminosity on listener event
     * @param listener
     */
    public void updateLuminosityOnListener(SensorEventListener listener){
        this.sensorManager.registerListener(listener, this.lightSensor, SensorManager.SENSOR_DELAY_UI);
    }
}
