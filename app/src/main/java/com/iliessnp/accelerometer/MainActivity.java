package com.iliessnp.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;
    TextView x, y, z, sum, jump, fall;
    float xx, yy, zz, summ;
    int falll, jumpp;
    ArrayList<Float> xData = new ArrayList<>();
    ArrayList<Float> yData = new ArrayList<>();
    ArrayList<Float> zData = new ArrayList<>();
    ArrayList<Float> acceleration = new ArrayList<>();
    long timeNow, timePrv = 0;
    
    //save to file
    String filename, filepath, filecontent;
    Button btnSave, btnLoad;
    TextView tvLoad;
    EditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);
        sum = findViewById(R.id.sum);
        jump = findViewById(R.id.jump);
        fall = findViewById(R.id.fall);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        falll = Integer.parseInt(fall.getText().toString());
        jumpp = Integer.parseInt(jump.getText().toString());

        //file save
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);
        etInput = findViewById(R.id.etInput);
        tvLoad = findViewById(R.id.tvLoad);
        filename = "myFile.txt";
        filepath = "myFileDir";
        if (!isExternalStorageAvailableForRW()) {
            btnSave.setEnabled(false);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLoad.setText("");
                filecontent = "acceleration => " + acceleration;
                if (!filecontent.equals("")) {
                    File myExternalFile = new File(getExternalFilesDir(filepath), filename);
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(myExternalFile);
                        fos.write(filecontent.getBytes());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    etInput.setText("");
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Fail;  text cant be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileReader fr = null;
                File myExternalFile = new File(getExternalFilesDir(filepath), filename);
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    fr = new FileReader(myExternalFile);
                    BufferedReader br = new BufferedReader(fr);
                    String line = br.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append("\n");
                        line = br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    String fileContents = "File Content\n" + stringBuilder.toString();
                    tvLoad.setText(fileContents);
                }
            }
        });
    }

    //save to file
    private boolean isExternalStorageAvailableForRW() {
        String externalState = Environment.getExternalStorageState();
        if (externalState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void showZData(View view) {
        for (float xX : xData) {
            Log.d(TAG, "xData: " + xData);
        }
        for (float yY : yData) {
            Log.d(TAG, "yData: " + yData);
        }
        for (float zZ : zData) {
            Log.d(TAG, "zData: " + zData);
        }
    }

    //accelerometer
    @Override
    public void onSensorChanged(SensorEvent event) {
        xx = event.values[0];
        yy = event.values[1];
        zz = event.values[2];
        xData.add(event.values[0]);
        yData.add(event.values[1]);
        zData.add(event.values[2]);

        float t = (float) (Math.pow(xx, 2) + Math.pow(yy, 2) + Math.pow(zz, 2));
        summ = (float) Math.sqrt(t);
        timeNow = System.currentTimeMillis();

        if (summ >= 65) {
            jumpp = +1;
            jump.setText(String.valueOf(jumpp));
        } else if (summ > 35 && timeNow > timePrv + 20000) {
            Toast.makeText(this, "you Fallen \nsumm is=> " + summ, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "time now: " + timeNow + "\ntime prev: " + timePrv);
            falll = +1;
            fall.setText(String.valueOf(falll));
            acceleration.add(summ);
            timePrv = timeNow;
        }

        x.setText(String.valueOf(xx));
        y.setText(String.valueOf(yy));
        z.setText(String.valueOf(zz));
        sum.setText(String.valueOf(summ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}