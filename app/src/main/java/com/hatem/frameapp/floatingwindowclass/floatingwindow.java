package com.hatem.frameapp.floatingwindowclass;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.hatem.frameapp.MainActivity;



public class floatingwindow extends Service{

    private WindowManager wm;
    public static RelativeLayout rl2;
    public static RelativeLayout rl3;
    public static RelativeLayout rl4;
    public static RelativeLayout rl5;
    public RelativeLayout rl;
    public int height = MainActivity.height;
    public int width = MainActivity.width ;
    public WindowManager.LayoutParams parameters;
    public RelativeLayout.LayoutParams rl2Parameters;
    public RelativeLayout.LayoutParams rl3Parameters;
    public RelativeLayout.LayoutParams rl4Parameters;
    public RelativeLayout.LayoutParams rl5Parameters;
    private boolean flag = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //creating the floating window which will contain the frame
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        rl2 = new RelativeLayout(this);
        rl3 = new RelativeLayout(this);
        rl4 = new RelativeLayout(this);
        rl5 = new RelativeLayout(this);

        rl = new RelativeLayout(this);
        rl.setBackgroundColor(Color.rgb(255, 0, 0));

        //Setting the parameters of the floating window to be not toauchable and not focusable so that when the user presses on the floating window, it passes the touch to any layer of UI beneath it.
        WindowManager.LayoutParams parameters2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            parameters2 = new WindowManager.LayoutParams(width, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        }else{
            parameters2 = new WindowManager.LayoutParams(width, height, WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        }

        parameters = parameters2;
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.CENTER;

        RelativeLayout.LayoutParams rlParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.setBackgroundColor(Color.argb(0, 0, 0, 0));
        rl.setLayoutParams(rlParameters);

        rl2Parameters = new RelativeLayout.LayoutParams((1 * width) / 60, height);
        rl3Parameters = new RelativeLayout.LayoutParams((1 * width) / 60, height);
        rl4Parameters = new RelativeLayout.LayoutParams(width, (1 * height) / 18);
        rl5Parameters = new RelativeLayout.LayoutParams(width, (1 * height) / 60);

        rl2.setBackgroundColor(Color.argb(0, 255, 0, 0));
        rl3.setBackgroundColor(Color.argb(0, 255, 0, 0));
        rl4.setBackgroundColor(Color.argb(0, 255, 0, 0));
        rl5.setBackgroundColor(Color.argb(0, 255, 0, 0));
        rl2.setLayoutParams(rl2Parameters);
        rl3.setLayoutParams(rl3Parameters);
        rl4.setLayoutParams(rl4Parameters);
        rl5.setLayoutParams(rl5Parameters);
        rl2Parameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rl3Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rl4Parameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl5Parameters.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        rl.addView(rl2, rl2Parameters);
        rl.addView(rl3, rl3Parameters);
        rl.addView(rl4, rl4Parameters);
        rl.addView(rl5, rl5Parameters);

        wm.addView(rl, parameters);


        rl.setClickable(false);
        rl.setFocusable(false);
        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
       final Handler mHandler = new Handler();
       final Handler mHandler2 = new Handler();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));



    }
    @Override
    public void onDestroy() {
        flag = true;
        rl.removeAllViews();
        wm.removeView(rl);
        super.onDestroy();
    }

    public static void setFrameColorToOrange(){
        rl2.setBackgroundColor(Color.rgb(252, 131, 45));
        rl3.setBackgroundColor(Color.rgb(252, 131, 45));
        rl4.setBackgroundColor(Color.rgb(252, 131, 45));
        rl5.setBackgroundColor(Color.rgb(252, 131, 45));
    }
    public static void setFrameColorToRed(){
        rl2.setBackgroundColor(Color.rgb(255, 0, 0));
        rl3.setBackgroundColor(Color.rgb(255, 0, 0));
        rl4.setBackgroundColor(Color.rgb(255, 0, 0));
        rl5.setBackgroundColor(Color.rgb(255, 0, 0));
    }
    public static void setFrameTransparent(){
        rl2.setBackgroundColor(Color.argb(0,0, 0, 0));
        rl3.setBackgroundColor(Color.argb(0,0, 0, 0));
        rl4.setBackgroundColor(Color.argb(0,0, 0, 0));
        rl5.setBackgroundColor(Color.argb(0,0, 0, 0));
    }

    //When receiving broadcasts from mThread to change the color of the frame.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String orientation = intent.getStringExtra("orientation");
            if(message == "Orange")
                setFrameColorToOrange();

            if(message == "Red")
                setFrameColorToRed();

            if(message=="Transparent")
                setFrameTransparent();

            if(message=="OrientationChanged"){
                wm.removeView(rl);
                if(orientation=="landscape"){
                    parameters = new WindowManager.LayoutParams(height,width,WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    parameters.x = 0;
                    parameters.y = 0;
                    parameters.gravity = Gravity.CENTER;
                } else{
                    parameters = new WindowManager.LayoutParams(width,height,WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    parameters.x = 0;
                    parameters.y = 0;
                    parameters.gravity = Gravity.CENTER;
                }
                wm.addView(rl,parameters);
            }
            Log.d("receiver", "Got message: " + message);
        }
    };
    private void adjustInnerLayoutsPortrait(){
        rl2Parameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rl3Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rl4Parameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl5Parameters.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    }

    private void adjustInnerLayoutsLandscape(){
        rl2Parameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl3Parameters.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rl4Parameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rl5Parameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

}
