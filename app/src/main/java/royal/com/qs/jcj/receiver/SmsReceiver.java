package royal.com.qs.jcj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {
    private SharedPreferences mPref;
    @Override
    public void onReceive(Context context, Intent intent) {
        mPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);

        final Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        for(Object object : pdus) {
            final SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            final String originatingAddress = message.getOriginatingAddress();//短信来源号码
            final String messageBody = message.getMessageBody();//短信内容

            System.out.println(originatingAddress+","+messageBody);

            if ("#*alarm*#".equals(messageBody)) {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                mediaPlayer.setVolume(1f,1f);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                abortBroadcast();
            }else if("#*location*#".equals(messageBody)) {
                context.startService(new Intent(context,LocationService.class));

                final String location = mPref.getString("location", "getting location");
                System.out.println(location);
            }else if("#*wipedata*#".equals(messageBody)) {
                System.out.println("初始化设备，清除设备数据");
            }else if("#*lockscreen*#".equals(messageBody)) {
                System.out.println("锁屏");
            }
        }
    }
}
