package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.Annotation;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.db.dao.AddressDao;

/**
 * 电话归属地查询页面
 */
public class AddressActivity extends Activity {
    private EditText et_address;
    private TextView tv_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address);

        et_address = (EditText) findViewById(R.id.et_address);
        tv_address = (TextView) findViewById(R.id.tv_address);

        //实时跟新归属地状态
        et_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = AddressDao.getAddress(s.toString());
                tv_address.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 输入号码，查询响应的归属地信息
     * @param view
     */
    public void addressSearch(View view){
        final String phone = et_address.getText().toString();
        final String address = AddressDao.getAddress(phone);
        if(!TextUtils.isEmpty(phone)) {
            tv_address.setText(address);
        }else {
            //当输入内容为空时点击查询，设置抖动效果
            System.out.println("shake");
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_address.startAnimation(animation);
            vibrate();
        }
    }
    /**
     * 手机震动效果
     */
    private void vibrate() {
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000,2000,1000, },-1);
    }
}
