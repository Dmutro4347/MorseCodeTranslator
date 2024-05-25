package com.newenergy.arfors.myapplication;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {



    private Switch swLight, swMorze;

    private CameraManager objCameraManager;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    String mCameraId;
    private int MinLux;
    private SeekBar skbLuxMin;
    boolean isTorchON;
    private EditText edtMorzeInput, edtMorzeOutput;

    boolean isVibratedForState;
    Vibrator vibrator;

    ArrayList<Integer> durationTimeOuts = new ArrayList<>();

    Spinner spSpeedMorse;
    private Integer speedMorse = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swLight = findViewById(R.id.swLight);
        swMorze = findViewById(R.id.swMorze);
        edtMorzeInput = findViewById(R.id.edtInputMorze);
        edtMorzeOutput = findViewById(R.id.edtOutputMorze);
        objCameraManager = (CameraManager) getSystemService(getApplicationContext().CAMERA_SERVICE);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        skbLuxMin = findViewById(R.id.skbLuxMin);
        skbLuxMin.setMax(50);
        skbLuxMin.setProgress(10);
        isVibratedForState = false;
        spSpeedMorse = findViewById(R.id.spSpeedMorse);
       try {
            mCameraId = objCameraManager.getCameraIdList()[0];
//            Log.d("Cameras", ""+ objCameraManager.getCameraIdList());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        swLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSwitched) {
                if (isSwitched){
                    Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_SHORT).show();
                    }
                isTorchON = isSwitched;
                }
        });


        skbLuxMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MinLux = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        swMorze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                durationTimeOuts.clear();
                String morzeCoded = "";
                if (isChecked){
                    for (char letter : edtMorzeInput.getText().toString().toUpperCase().toCharArray()) {
                        morzeCoded += " " + MorseCodeTranslator.CodeMorse(letter);
                        durationTimeOuts.addAll(MorseCodeTranslator.morseCodeDurations(letter));
                    }
                    edtMorzeOutput.setText(morzeCoded);
                    for (int i = 0; i < durationTimeOuts.size(); i++) {
                        int tmp;
                        tmp = durationTimeOuts.get(i) * (12000/ speedMorse);
                        durationTimeOuts.set(i, tmp);
                    }
                    Log.d("durations", durationTimeOuts.toString());
                    FlashAsyncTask flashTask = new FlashAsyncTask();
                    if(flashTask != null) flashTask.cancel(true);
                    flashTask.execute(durationTimeOuts);

                }
            }
        });
        spSpeedMorse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SelectedListener", "onSelectedClick " + position + " "+id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            Log.d("lightLevel", "" + lightLevel);
            if (isTorchON) {
                if (lightLevel < MinLux) {
                    try {
                        objCameraManager.setTorchMode(mCameraId, true);
                        if (!isVibratedForState) {
                           vibrator.vibrate(500);
                           isVibratedForState = true;
                        }
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        objCameraManager.setTorchMode(mCameraId, false);
                        if (isVibratedForState) {
                            vibrator.vibrate(500);
                            isVibratedForState = false;
                        }

                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class FlashAsyncTask extends AsyncTask<ArrayList<Integer>, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swMorze.setEnabled(false);
        }

        @Override
        protected String doInBackground(ArrayList<Integer>... arr) {
            if (arr != null) {
                for (Integer i: arr[0]) {
                    try {
                        objCameraManager.setTorchMode(mCameraId, (i > 0) ? true : false);
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(abs(i));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swMorze.setEnabled(true);
        }
    }
}

