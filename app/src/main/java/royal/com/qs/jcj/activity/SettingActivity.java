package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.service.AddressService;
import royal.com.qs.jcj.utils.ServiceUtils;
import royal.com.qs.jcj.view.SettingDialog;
import royal.com.qs.jcj.view.SettingView;

/**
 * 设置中心页面
 */
public class SettingActivity extends Activity {
    private SettingView stv;
    private SettingView stv_address;
    private SettingDialog sd_address;
    private SettingDialog sd_address_position;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        stvSetting();

        //电话归属地显示设置
        stv_addressSetting();
        //选择归属地提示框风格
        chooseAddress();
        //归属地提示框显示位置
        showAddressPosition();
    }

    private void showAddressPosition() {
        sd_address_position = (SettingDialog) findViewById(R.id.view_position_sd);
        sd_address_position.setTitle("归属地提示框显示位置");
        sd_address_position.setmDesp("显示框位置");

        sd_address_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DragAddressActivity.class));
            }
        });
    }

    /**
     * 自动跟新设置
     */
    private void stvSetting() {
        stv = (SettingView) findViewById(R.id.stv);
        // stv.setTitle("自动跟新设置");
        final boolean isChecked = mPref.getBoolean("isChecked", true);
        if(isChecked) {
            stv.setmDesp("自动跟新已开启");
            stv.setCheck(true);
        }else {
            stv.setmDesp("自动跟新已关闭");
            stv.setCheck(false);
        }

        stv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stv.isChecked()) {
                    stv.setCheck(false);
                    mPref.edit().putBoolean("isChecked", false).commit();
                } else {
                    stv.setCheck(true);
                    mPref.edit().putBoolean("isChecked", true).commit();
                }
            }
        });
    }

    private void stv_addressSetting() {
        stv_address = (SettingView) findViewById(R.id.stv_address);

        // 根据归属地服务是否运行来更新checkbox
        boolean serviceRunning = ServiceUtils.isServiceRunning(this,
                "royal.com.qs.jcj.service.AddressService");

        if (serviceRunning) {
            stv_address.setCheck(true);
        } else {
            stv_address.setCheck(false);
        }

        stv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stv_address.isChecked()) {
                    stv_address.setCheck(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                }else {
                    stv_address.setCheck(true);
                    //开启电话归属地监听的服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }
    /**
     * 归属地提示框风格
     */
    final String[] items = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    private void chooseAddress() {
        sd_address = (SettingDialog) findViewById(R.id.view_sd);
        sd_address.setTitle("归属地提示框风格");

        final int address_style = mPref.getInt("address_style", 0);
        sd_address.setmDesp(items[address_style]);
        //点击后弹出提示框选择提示框风格
        sd_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");

                builder.setSingleChoiceItems(items, address_style, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPref.edit().putInt("address_style", which).commit();
                        sd_address.setmDesp(items[which]);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }
}
