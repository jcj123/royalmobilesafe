package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import royal.com.qs.jcj.R;

/**
 * 引导页4
 */
public class Setup4Activity extends BaseSetupActivity {
    private SharedPreferences mPref;
    private CheckBox cb_safe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup4);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        final boolean isOpend = mPref.getBoolean("isOpend", true);
        cb_safe = (CheckBox) findViewById(R.id.cb_safe);
        if(isOpend){
            cb_safe.setChecked(isOpend);
            cb_safe.setText("防盗保护已开启");
        }else {
            cb_safe.setChecked(isOpend);
            cb_safe.setText("防盗保护已关闭");

        }

        cb_safe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mPref.edit().putBoolean("isOpend",true).commit();

                }else {
                    mPref.edit().putBoolean("isOpend",false).commit();

                }
            }
        });
    }
    public void moveToPrevious() {
        startActivity(new Intent(this,Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.transtate_previous_in,R.anim.transtate_previous_out);
    }

    public void moveToNext() {
        startActivity(new Intent(this,LostFindActivity.class));
        mPref.edit().putBoolean("done",true).commit();

        finish();
        overridePendingTransition(R.anim.transtate_in,R.anim.transtate_out);
    }
}
