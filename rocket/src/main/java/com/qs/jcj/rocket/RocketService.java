package com.qs.jcj.rocket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class RocketService extends Service {
    private TelephonyManager tm;
    private PhoneStateListener listener;
    private WindowManager mWM;
    private View view;
    private SharedPreferences mPref;
    private BroadcastReceiver receiver;
    private WindowManager.LayoutParams params;
    private int startX;
    private int startY;
    private int winWidth;
    private int winHeight;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.gravity = Gravity.LEFT + Gravity.TOP;//把重心设置到左上角
        params.setTitle("Toast");

        view = View.inflate(this, R.layout.rocket_activity, null);
        final ImageView rocket = (ImageView) view.findViewById(R.id.iv_rocket);
        rocket.setBackgroundResource(R.drawable.rocket_anim);
        final AnimationDrawable rocketAnimation = (AnimationDrawable) rocket.getBackground();
        rocketAnimation.start();

        mWM.addView(view, params);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //起始位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //终止位置
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        //偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        params.x += dx;
                        params.y += dy;
                        mWM.updateViewLayout(v, params);

                        //跟新初始位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x > 100 && params.x < 250
                                && params.y > winHeight - 120) {
                            System.out.println("发射火箭!!!");
                            sendRocket();

                            //启动烟雾
                            Intent intent = new Intent(RocketService.this,BackbroundActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }

                return true;
            }
        });
    }

    private void sendRocket() {
        //设置火箭居中
        params.x = winWidth / 2 - view.getWidth() / 2;
        mWM.updateViewLayout(view, params);

        new Thread() {
            @Override
            public void run() {
                int pos = 380;

                for (int i = 0; i <= 10; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int y = pos - i * 38;
                    final Message msg = handler.obtainMessage();
                    msg.arg1 = y;
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            params.y = msg.arg1;
            mWM.updateViewLayout(view, params);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWM != null && view != null) {
            mWM.removeViewImmediate(view);
        }
    }
}
