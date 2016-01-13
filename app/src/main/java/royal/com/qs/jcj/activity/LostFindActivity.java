package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import royal.com.qs.jcj.R;

public class LostFindActivity extends Activity {
    private SharedPreferences mPref;
    private TextView safe_phone;
    private ImageView isSafed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        final boolean done = mPref.getBoolean("done", false);

        if(done){
            setContentView(R.layout.activity_lost_find);
            safe_phone = (TextView) findViewById(R.id.tv_safe_phone);
            System.out.println("safe_home"+safe_phone);
            isSafed = (ImageView) findViewById(R.id.iv_locked);

            final String phone = mPref.getString("safe_phone", "");
            safe_phone.setText(phone);

            final boolean isOpend = mPref.getBoolean("isOpend", true);
            if(isOpend) {
                isSafed.setImageResource(R.drawable.lock);
            }else {
                isSafed.setImageResource(R.drawable.unlock);
            }

        }else {
            startActivity(new Intent(this,Setup1Activity.class));
        }
    }

    public void reEnter(View view) {
        mPref.edit().remove("done").commit();
        startActivity(new Intent(this,Setup1Activity.class));
    }
}
