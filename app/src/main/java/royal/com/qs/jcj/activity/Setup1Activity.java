package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import royal.com.qs.jcj.R;

/**
 * 引导页1
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup1);
    }

    public void next(View view) {
        startActivity(new Intent(this,Setup2Activity.class));
        finish();

        overridePendingTransition(R.anim.transtate_in,R.anim.transtate_out);
    }

    public void moveToPrevious() {
    }

    public void moveToNext() {
        startActivity(new Intent(this,Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.transtate_in,R.anim.transtate_out);
    }
}
