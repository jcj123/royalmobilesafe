package royal.com.qs.jcj.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.sax.EndElementListener;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import royal.com.qs.jcj.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {

    private BlackNumberDao dao;
    private InnerSmsReceiver innerSmsReceiver;
    private TelephonyManager tm;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object [] objs = (Object[]) intent.getExtras().get("pdus");
            for(Object obj : objs){
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String sender = sms.getOriginatingAddress();//得到一个电话号码
                final String messageBody = sms.getMessageBody();
                //看一看这个电话号码是否是黑名单里面的
                String mode = dao.findMode(sender);
                if("1".equals(mode)||"3".equals(mode)){
                    abortBroadcast();//把这个广播终止掉
                }

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        /**
         * 短信拦截
         */
        innerSmsReceiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

        registerReceiver(innerSmsReceiver,filter);
        /**
         * 电话拦截
         */
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        final MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        tm.listen(myPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    final String mode = dao.findMode(incomingNumber);
                    System.out.println("mode="+mode);
                    if (mode.equals("1") || mode.equals("2")) {
                        System.out.println("挂断来电");


                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri,true,
                                new MyContentObserver(new Handler(),incomingNumber));

                        endPhone();
                    }
                    break;
            }
        }
    }
    private class MyContentObserver extends ContentObserver {
        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {

            getContentResolver().unregisterContentObserver(this);

            deleteCallLog(incomingNumber);

            super.onChange(selfChange);
        }
    }
    //删掉电话号码
    private void deleteCallLog(String incomingNumber) {

        Uri uri = Uri.parse("content://call_log/calls");

        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});

    }
    private void endPhone() {
        try {
            final Class tmClazz = tm.getClass();
            final Method iTelephony = tmClazz.getDeclaredMethod("getITelephony");
            final Object telephonyObject = iTelephony.invoke(tm);

            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();
                Method endCallMethod = telephonyClass.getMethod("endCall");
                endCallMethod.setAccessible(true);
                endCallMethod.invoke(telephonyObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerSmsReceiver);
        innerSmsReceiver = null;
    }
}
