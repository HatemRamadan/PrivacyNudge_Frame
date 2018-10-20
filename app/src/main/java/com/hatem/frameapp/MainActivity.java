package com.hatem.frameapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hatem.frameapp.floatingwindowclass.floatingwindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public static int height;
    public static int width;
    public MThread mThread;
    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private Button button;
    private TextView textView;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDimensions();
        final Intent intent = new Intent(MainActivity.this, floatingwindow.class);
        startService( intent);
        mThread = new MThread(MainActivity.this);
        Handler mHandler = new Handler();
        Runnable start = new Runnable() {
            @Override
            public void run() {
                mThread.start(); //starting the background thread which creats the notification and acts as a timer to manage when nudges should be sent
            }
        };
        Runnable stop = new Runnable() {
            @Override
            public void run() {
              stopService(intent);
              mThread.interrupt();
            }
        };
        mHandler.postDelayed(stop,30*60000); // interrupts the thread after 30 minutes.
        mHandler.postDelayed(start,10000);
        this.toggleButton1 = new ToggleButton(this);
        this.toggleButton2 = new ToggleButton(this);
        this.toggleButton1 = findViewById(R.id.toggleButton);
        this.toggleButton2 = findViewById(R.id.toggleButton2);
        this.toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mThread.switchToggled(isChecked,toggleButton2.isChecked()); // In case sound toggle value has changed
            }
        });
        this.toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mThread.switchToggled(toggleButton1.isChecked(),isChecked);// In case vibration toggle value has changed
            }
        });
        this.button = new Button(this);
        this.textView = new TextView(this);
        this.button = findViewById(R.id.button);
        this.textView = findViewById(R.id.textView);
        this.button.setBackgroundColor(Color.argb(0,0,0,0));
        this.textView.setBackgroundColor(Color.argb(0,0,0,0));
        this.textView.setTextSize(30);

        //To show how many times nudges have been shown so far, when this button is clicked, the file log.txt is read and the informaction is retrieved and displayed.
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if(counter<3)
                    return;
                counter=0;
                File file = new File(getFilesDir(),"log.txt");
                String line = "";
                try {
                    FileInputStream fileInputStream = new FileInputStream (file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    line = bufferedReader.readLine();
                    fileInputStream.close();

                    bufferedReader.close();
                }
                catch(FileNotFoundException e) {
                    Log.d("FileNotFoundException", e.getMessage());
                }
                catch(IOException e) {
                    Log.d("IOException", e.getMessage());
                }
                textView.setText(line);
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,floatingwindow.class));
        this.mThread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           mThread.orientationChanged("landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mThread.orientationChanged("portrait");
        }
    }

    private void setDimensions(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
    }

}

