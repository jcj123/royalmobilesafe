package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.domian.Icon;
import royal.com.qs.jcj.utils.MD5Utils;

public class HomeActivity extends Activity {

    private GridView gvHome;
    private List<Icon> iconList;
    private TextView iconName;
    private ImageView iconImage;
    private AlertDialog setPasswordDialog;
    private SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        initData();
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new MyAdapter(this, R.layout.home_list_item, iconList));

        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showPasswordDialog();
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this, CallSmsSafeActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this, AppManageActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(HomeActivity.this, TaskManageActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(HomeActivity.this, HighLevelToolsActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
    }

    private void showPasswordDialog() {
        final String password = mPref.getString("password", null);
        if (password != null) {
            showLoginDialog();
        } else {
            showRegistDialog();
        }
    }

    /**
     * 弹出登录页面
     */
    private void showLoginDialog() {
        View v = View.inflate(this, R.layout.safe_login_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(v);
        alertDialog.show();

        final EditText mPassword = (EditText) v.findViewById(R.id.et_password);
        Button mBt_ok = (Button) v.findViewById(R.id.bt_ok);
        Button mBt_cancel = (Button) v.findViewById(R.id.bt_cancel);

        mBt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = mPassword.getText().toString();
                String confirmPW = mPref.getString("password", null);
                if (!TextUtils.isEmpty(password)) {
                    if (MD5Utils.encode(password).equals(confirmPW)) {
                        alertDialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
        mBt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 弹出设置密码界面，并设置密码
     */
    private void showRegistDialog() {
        View v = View.inflate(HomeActivity.this, R.layout.safe_regist_dialog, null);
        final EditText mPassword = (EditText) v.findViewById(R.id.et_password);
        final EditText mConfirmPw = (EditText) v.findViewById(R.id.et_password_confirm);
        Button mBt_ok = (Button) v.findViewById(R.id.bt_ok);
        Button mBt_cancel = (Button) v.findViewById(R.id.bt_cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        setPasswordDialog = builder.create();
        setPasswordDialog.setView(v);
        setPasswordDialog.show();


        mBt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = mPassword.getText().toString();
                final String confirmPW = mConfirmPw.getText().toString();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPW)) {
                    if (password.equals(confirmPW)) {
                        mPref.edit().putString("password", MD5Utils.encode(password)).commit();
                        setPasswordDialog.dismiss();

                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "账户名和密码不一致", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
        mBt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPasswordDialog.dismiss();
            }
        });
    }


    private void initData() {
        iconList = new ArrayList<Icon>();
        iconList.add(new Icon("手机防盗", R.drawable.home_safe));
        iconList.add(new Icon("通讯卫士", R.drawable.home_callmsgsafe));
        iconList.add(new Icon("软件管理", R.drawable.home_apps));
        iconList.add(new Icon("进程管理", R.drawable.home_taskmanager));
        iconList.add(new Icon("流量统计", R.drawable.home_netmanager));
        iconList.add(new Icon("手机杀毒", R.drawable.home_trojan));
        iconList.add(new Icon("缓存清理", R.drawable.home_sysoptimize));
        iconList.add(new Icon("高级工具", R.drawable.home_tools));
        iconList.add(new Icon("设置中心", R.drawable.home_settings));
    }

    class MyAdapter extends ArrayAdapter<Icon> {
        private int resourceId;

        public MyAdapter(Context context, int resource, List<Icon> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Icon icon = getItem(position);
            View view = View.inflate(HomeActivity.this, resourceId, null);

            iconName = (TextView) view.findViewById(R.id.tv_icon);
            iconImage = (ImageView) view.findViewById(R.id.iv_icon);
            iconName.setText(icon.getName());
            iconImage.setImageResource(icon.getResourceId());
            return view;
        }
    }


}
