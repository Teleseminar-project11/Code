package com.example.automaticvideodirector;

import java.util.Observable;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



/**
 * 
 * @author thilo
 * This class is based on the approach of the "Android Cooking Book"
 */

public class ShakeDetection extends Observable implements SensorEventListener {
	
	
	/**
	 * Variables declaration
	 */
	private static final String DEBUG_TAG = "ShakeDetetction";
	
	private SensorManager sensorManager;
	
	/* Current values of acceleration, one for each axis */
	private float xAccel;
	private float yAccel;
	private float zAccel;

	/* Previous values of acceleration */
	private float xPreviousAccel;
	private float yPreviousAccel;
	private float zPreviousAccel;

	/* Used to suppress the first shaking */
	private boolean firstUpdate = true;

	/*Threshold is based on experimenting */
	private final float shakeThreshold = 0.7f;
	
	/* Has a shaking motion been started (one direction) */
	private boolean shakeInitiated = false;
	
	
	/**
	 * Constructor
	 */
	public ShakeDetection(SensorManager sensorManager){
		this.sensorManager = sensorManager;
		sensorManager.registerListener(this, sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
		
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		updateAccelParameters(event.values[0], event.values[1], event.values[2]);
        if ((!shakeInitiated) && isAccelerationChanged()) {   
		    shakeInitiated = true; 
	    } else if ((shakeInitiated) && isAccelerationChanged()) {
		    executeShakeAction();
	    } else if ((shakeInitiated) && (!isAccelerationChanged())) {
		    shakeInitiated = false;
        }
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	
	/** Store the acceleration values given by the sensor  */
	private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {
        /* we have to suppress the first change of acceleration, it results from first values being initialized with 0 */
		if (firstUpdate) {  
			xPreviousAccel = xNewAccel;
			yPreviousAccel = yNewAccel;
			zPreviousAccel = zNewAccel;
			firstUpdate = false;
		} else {
			xPreviousAccel = xAccel;
			yPreviousAccel = yAccel;
			zPreviousAccel = zAccel;
		}
		xAccel = xNewAccel;
		yAccel = yNewAccel;
		zAccel = zNewAccel;
	}
	
	/** If the values of acceleration have changed on at least two axises, we are probably in a shake motion */
	private boolean isAccelerationChanged() {
		float deltaX = Math.abs(xPreviousAccel - xAccel);
		float deltaY = Math.abs(yPreviousAccel - yAccel);
		float deltaZ = Math.abs(zPreviousAccel - zAccel);
		return (deltaX > shakeThreshold && deltaY > shakeThreshold)
				|| (deltaX > shakeThreshold && deltaZ > shakeThreshold)
				|| (deltaY > shakeThreshold && deltaZ > shakeThreshold);
	}
	
	/** Notify Observers */
	private void executeShakeAction(){
		setChanged();
		notifyObservers();
		
	}
	
	
	

}
