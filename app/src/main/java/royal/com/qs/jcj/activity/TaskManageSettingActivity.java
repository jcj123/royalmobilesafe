package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.service.AutoKillProcessService;

public class TaskManageSettingActivity extends Activity {
    @ViewInject(R.id.cb_status_isSystemTask)
    private CheckBox cb_status_isSystemTask;
    @ViewInject(R.id.cb_status_kill_process)
    private CheckBox cb_status_kill_process;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_manage_setting);
        ViewUtils.inject(this);
        sp = getSharedPreferences("config",MODE_PRIVATE);

        final boolean is_show_system = sp.getBoolean("is_show_system", false);
        cb_status_isSystemTask.setChecked(is_show_system);
        cb_status_isSystemTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("is_show_system",isChecked).commit();
            }
        });

        cb_status_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final Intent intent = new Intent(TaskManageSettingActivity.this,
                            AutoKillProcessService.class);
                    startService(intent);
                }else {
                    final Intent intent = new Intent(TaskManageSettingActivity.this,
                            AutoKillProcessService.class);
                    stopService(intent);
                }
            }
        });
    }
}
