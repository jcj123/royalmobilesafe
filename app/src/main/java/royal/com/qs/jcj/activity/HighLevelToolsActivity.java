package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.utils.SmsUtils;
import royal.com.qs.jcj.utils.UIUtils;

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
     *
     * @param view
     */
    public void phone(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    /**
     * 点击进行短信备份
     *
     * @param view
     */
    public void backupSms(View view) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("备份短信");
        pd.setMessage("正在备份中，请稍安勿躁。。。。。");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        //备份短信
        new Thread() {
            @Override
            public void run() {
                final boolean result = SmsUtils.backupSms(HighLevelToolsActivity.this, new SmsUtils.CallBack() {
                    @Override
                    public void before(int count) {
                        pd.setMax(count);
                    }

                    @Override
                    public void after(int progress) {
                        pd.setProgress(progress);
                    }
                });

                if (result) {
                    UIUtils.showToast(HighLevelToolsActivity.this, "备份成功");
                } else {
                    UIUtils.showToast(HighLevelToolsActivity.this, "备份失败");
                }

                 pd.dismiss();
            }
        }.start();
    }
}
