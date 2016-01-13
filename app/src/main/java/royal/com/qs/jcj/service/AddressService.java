package royal.com.qs.jcj.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.db.dao.AddressDao;

/**
 * 电话归属地提示框（服务后台监听来电与去电）
 * 来电用系统服务，去电使用广播接收者监听
 */
public class AddressService extends Service {
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
        //来电通过服务监听
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//坚挺电话状态

        mPref = getSharedPreferences("config",MODE_PRIVATE);
        //去电通过广播接受者拦截
        receiver = new CallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver,filter);
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                //响铃状态下
                case TelephonyManager.CALL_STATE_RINGING:
                    final String address = AddressDao.getAddress(incomingNumber);
//                    Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
                    showView(address);
                    break;
                //挂断时
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWM != null && view != null) {
                        mWM.removeView(view);
                        view = null;
                    }
                    break;

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tm.listen(listener, PhoneStateListener.LISTEN_NONE);//停止监听
        unregisterReceiver(receiver);
    }

    private void showView(String text) {
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

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

        //设置电话归属地的位置
        final int lastX = mPref.getInt("lastX", 0);
        final int lastY = mPref.getInt("lastY", 0);
        params.x = lastX;
        params.y = lastY;

        view = View.inflate(this, R.layout.toast_address, null);

        final TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_number.setText(text);

        int[] styles = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        final int address_style = mPref.getInt("address_style", 0);
        view.setBackgroundResource(styles[address_style]);
        mWM.addView(view, params);//将view添加到window上

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //得到起始位置坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //得到终止位置坐标
                        final int endX = (int) event.getRawX();
                        final int endY = (int) event.getRawY();
                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
                        //跟新图片位置

                        params.x += dx;
                        params.y += dy;

                        //不能让view飞出边界
                        if(params.x<0) {
                            params.x = 0;
                        }

                        if(params.y < 0) {
                            params.y = 0;
                        }

                        if(params.x > winWidth-view.getWidth()) {
                            params.x = winWidth-view.getWidth();
                        }

                        if (params.y > winHeight-view.getHeight()) {
                            params.y = winHeight-view.getHeight();
                        }
                        mWM.updateViewLayout(view,params);
                        //重新设置初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //记录坐标点
                        final SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("lastX",params.x);
                        edit.putInt("lastY",params.y);
                        edit.commit();
                        break;
                }
                return true;
            }
        });


    }
    /**
     * 监听去电的广播接受者 需要权限: android.permission.PROCESS_OUTGOING_CALLS
     *
     * @author jcj
     *
     */
    class CallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String phone = getResultData();
            final String address = AddressDao.getAddress(phone);
            showView(address);
        }
    }
}
