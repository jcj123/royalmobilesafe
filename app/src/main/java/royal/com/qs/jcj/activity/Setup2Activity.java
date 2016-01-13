package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.view.SettingView;

/**
 * 引导页2
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingView sim_sv;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup2);
        sim_sv = (SettingView) findViewById(R.id.sim_sv);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        final String sim = mPref.getString("sim", null);
        if(!TextUtils.isEmpty(sim)) {
            sim_sv.setCheck(true);
        }else {
            sim_sv.setCheck(false);
        }


        sim_sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sim_sv.isChecked()) {
                    sim_sv.setCheck(false);
                    mPref.edit().remove("sim").commit();
                }else {
                    sim_sv.setCheck(true);
                    // 保存sim卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();// 获取sim卡序列号
                    System.out.println("sim卡序列号:" + simSerialNumber);

                    mPref.edit().putString("sim", simSerialNumber).commit();// 将sim卡序列号保存在sp中
                }
            }
        });
    }


    public void moveToPrevious() {
        startActivity(new Intent(this,Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.transtate_previous_in,R.anim.transtate_previous_out);
    }

    public void moveToNext() {
        startActivity(new Intent(this,Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.transtate_in,R.anim.transtate_out);
    }
}
