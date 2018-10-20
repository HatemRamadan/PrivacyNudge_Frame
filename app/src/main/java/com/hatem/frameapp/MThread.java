package com.hatem.frameapp;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class MThread extends Thread {
    private Context context;
    private Boolean flag = true;
    private Intent intent;
    private NotificationCompat.Builder mBuilder;
    private Random rnd = new Random();
    private int counter = 1;
    final boolean oreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    private int limit = 6;
    public MThread(Context context){

        this.context = context;
        if(!oreo)
        this.mBuilder = new NotificationCompat.Builder(this.context, "1")
                .setSmallIcon(R.drawable.lock).setContentTitle("Privacy notice")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel(false,false);
        if(oreo){
         this.mBuilder = new NotificationCompat.Builder(this.context,"my_id")
                 .setSmallIcon(R.drawable.lock)
                 .setBadgeIconType(R.drawable.lock)
                 .setContentTitle("Privacy notice")
                 .setAutoCancel(true)
                 .setNumber(1)
                 .setColor(255);
        }
    }
    private void createNotificationChannel(boolean sound, boolean vibration) {
        Log.d("Create channel","channel"+ sound +" "+ vibration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(NOTIFICATION_SERVICE);
            String id = "my_id";
            // The user-visible name of the channel.
            CharSequence name = "frame";
            // The user-visible description of the channel.
            String description = "Notifications regarding my app";
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            // Configure the notification channel.
            mChannel.setDescription(description);

            Uri uri = Uri.parse("android.resource://"+this.context.getPackageName()+"/raw/sound.mp3");
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            mChannel.setSound(uri,att);
            mChannel.enableVibration(vibration);

            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(mChannel);
        }
    }
    @Override
    public void run() {
        //Creating notification
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        this.intent = new Intent("custom-event-name");
        while (flag & limit !=0) {
            int type = rnd.nextInt(6) + 1;
            if(type==1){
                limit = limit - 2;
                int fbLevel = rnd.nextInt(2) + 1;
                //notify access FB
                notificationManager.cancel(1);
                mBuilder.setContentText("Facebook is using your front camera");
                orange();
                notificationManager.notify(1, mBuilder.build());
                delay(10,15);
                fbLevel=1;
                if(fbLevel==1){
                    //notify FB processing
                    notificationManager.cancel(1);
                    mBuilder.setContentText("Facebook is processing captured photos");
                    red();
                    notificationManager.notify(1,mBuilder.build());
                    delay(10,20);
                    transparent();
                    notificationManager.cancel(1);
                }

            } else if(type==2){
                limit = limit - 2;
                int scLevel = rnd.nextInt(2) + 1;
                //notify access SC
                notificationManager.cancel(1);
                mBuilder.setContentText("Snapchat is using your front camera");
                orange();
                notificationManager.notify(1, mBuilder.build());
                delay(10,15);
                scLevel=1;
                if(scLevel==1){
                    //notify SC processing
                    notificationManager.cancel(1);
                    mBuilder.setContentText("Snapchat is processing captured photos");
                    red();
                    notificationManager.notify(1,mBuilder.build());
                    delay(10,20);
                    transparent();
                    notificationManager.cancel(1);
                }

            } else {
                transparent();
                notificationManager.cancel(1);
                delay(8,16);
            }

        }
        notificationManager.cancel(1);
    }

    //A timer generated randomely to mock up when privacy nudges should be sent.
    private void delay(int min, int max) {
        int delay = rnd.nextInt(max+1 - min) + min;
        try {
            Thread.sleep(delay*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //This method sends a broadcast that the nudge (frame) color should change to red to denote photos/videos processsing.
    private void red(){
        this.intent.putExtra("message", "Red");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        log((counter++)+"");
    }

    //This method sends a broadcast that the nudge (frame) color should change to orange to denote camera accessing.
    private void orange(){
        this.intent.putExtra("message", "Orange");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        log((counter++)+"");
    }

    //This method sends a broadcast that the nudge (frame) should disappear denoting that there are no privacy issues.
    private void transparent(){
        this.intent.putExtra("message", "Transparent");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
    }

    //This method is called when the sound/vibration settings have been changed. It changes the nudges sound/vibration accoding to that change.
    void switchToggled(boolean sound, boolean vibration){
        if(sound && vibration){
            if(!oreo)
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            createNotificationChannel(sound,vibration);
        } else if(sound && !vibration){
            if(!oreo)
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            createNotificationChannel(sound,vibration);
        }else if(!sound && vibration){
            if(!oreo)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            createNotificationChannel(sound,vibration);
        }else {
            if(!oreo)
            mBuilder.setDefaults(Notification.VISIBILITY_SECRET);
            createNotificationChannel(sound,vibration);
        }
    }
    //In case of orientation has changed, a broadcast is sent in order to rotate the frame such that the new width = the previous height and vice versa.
    public void orientationChanged(String orientation){
        this.intent.putExtra("message", "OrientationChanged");
        this.intent.putExtra("orientation", orientation);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
    }

    //This function logs the number of nudges have been sent in a text file in the app directory.
    public void log(String text)
    {
        File logFile = new File(this.context.getFilesDir(),"log.txt");
        if (!logFile.exists())  //If this is the first nudge, a new text file named log.txt will be created
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {   logFile.delete();
            logFile.createNewFile();
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.write(text);
            buf.flush();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        this.flag = false;
        Log.d("Interrupt", "interrupted!");
        super.interrupt();
    }
}