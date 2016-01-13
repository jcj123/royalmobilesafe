package royal.com.qs.jcj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences mPref = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        final String sim = mPref.getString("sim", "");
        final TelephonyManager tm = (TelephonyManager) //
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();

        if(!TextUtils.isEmpty(sim)){
            if(!sim.equals(simSerialNumber)){
                System.out.println("sim卡变更");
                String phone = mPref.getString("safe_phone", "");// 读取安全号码

                // 发送短信给安全号码
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null,
                        "sim card changed!", null, null);
            }else {
                System.out.println("手机安全");
            }
        }
    }
}
