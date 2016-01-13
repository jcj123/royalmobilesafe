package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import royal.com.qs.jcj.R;

/**
 * 高级工具页面
 */
public class HighLevelToolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_high_level_tools);
    }

    /**
     * 点击查询电话归属地
     * @param view
     */
    public void phone(View view) {
        startActivity(new Intent(this,AddressActivity.class));
    }
}
