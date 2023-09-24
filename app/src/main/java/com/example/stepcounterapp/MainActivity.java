package com.example.stepcounterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ProgressBar progress_bar;
    private TextView steps;
    private SensorManager manager = null;
    private Sensor sensor;
    private  int total_steps=0;
    private int preview_steps=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress_bar=findViewById(R.id.progress_bar);
        steps=findViewById(R.id.tv_steps);

        resetSteps();
        loadData();
        manager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }
    protected void onResume(){
        super.onResume();

        if(sensor==null){
            Toast.makeText(this, "This device has no sensor", Toast.LENGTH_SHORT).show();
        }else{
            manager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    protected void onPause(){
        super.onPause();
        manager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            total_steps=(int) event.values[0];
            int currentSteps=total_steps-preview_steps;
            steps.setText(String.valueOf(currentSteps));

            progress_bar.setProgress(currentSteps);
        }

    }

    private void resetSteps(){
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Long press to reset steps", Toast.LENGTH_SHORT).show();
            }
        });

       steps.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               preview_steps=total_steps;
               steps.setText("0");
               progress_bar.setProgress(0);
               saveData();
               return true;
           }
       });
    }

    private void saveData() {
        SharedPreferences sharedPreferences=getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("key1",String.valueOf(preview_steps));
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences=getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int savedNum=(int) sharedPreferences.getFloat("key1",0f);
        preview_steps=savedNum;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}