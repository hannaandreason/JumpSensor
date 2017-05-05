package com.example.hannaandreason.jumpsensor;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView nbrJump;
    private int currentNbrJumps = 10;
    private boolean currentPosUp = false;
    private Sensor mAccelerator;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager= (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerator=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        nbrJump = (TextView) findViewById(R.id.nbrJumps);
        mSensorManager.registerListener(this, mAccelerator, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accelerationValues= getValues(event);

        for(int i=0; i<accelerationValues.length; i++){
            if(i==1) {
                Log.d(Integer.toString(i), Float.toString(accelerationValues[i]));
            }
        }

        if (currentPosUp) {
            if (detectDown(accelerationValues)) {
                currentPosUp = false;
            }
        } else {
            if (detectUp(accelerationValues)) {
                currentPosUp = true;
                if(currentNbrJumps>1) {
                    currentNbrJumps--;
                    nbrJump.setText(Integer.toString(currentNbrJumps));
                }else{
                    nbrJump.setText("Done!");
                }
            }
        }
    }

    private boolean detectDown(float[] values) {
      if(values[1]>4) {
          return true;
      }
      return false;
    }

    private boolean detectUp(float[] values) {
        if(values[1]<1.5){
            return true;
        }
        return false;
    }

    private float[] getValues(SensorEvent event){
        final float alpha = 0.8f;
        float[] gravity= new float[3];
        float[] linear_acceleration= new float[3];

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        return linear_acceleration;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mAccelerator !=  null) {
            mSensorManager.registerListener(this, mAccelerator, SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            Toast.makeText(this, "This is not supported", Toast.LENGTH_SHORT).show();

        }
    }

    public void reset(View view) {
        currentNbrJumps=10;
        nbrJump.setText(Integer.toString(currentNbrJumps));
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
