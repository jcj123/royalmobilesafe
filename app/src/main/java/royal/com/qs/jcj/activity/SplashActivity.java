package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import royal.com.qs.jcj.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import royal.com.qs.jcj.utils.MobileUtils;

public class SplashActivity extends Activity {
    private static final int CODE_UPDATE = 1;
    private static final int CODE_URL_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    public static final int CODE_NET_ERROR = 4;
    private static final int ENTER_HOME = 5;

    private TextView tvVersion;
    private TextView tvProgress;//下载进度显示
     RelativeLayout rl;

    String mVersionName;
    int mVersionCode;
    String mDesc;
    String mDownloadUrl;

    SharedPreferences mPref;//存储数据的

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_UPDATE:
                    showDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url请求错误", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "json解析错误", Toast.LENGTH_LONG).show();
                    System.out.println("json解析错误");
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                    System.out.println("网络连接错误");
                    enterHome();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText("版本号:" + getVersionName());
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        final boolean isChecked = mPref.getBoolean("isChecked", true);
        if(isChecked) {
            checkUpdate();
        } else {
            final Message msg = handler.obtainMessage();
            msg.what = ENTER_HOME;
            handler.sendMessageDelayed(msg,2000);
        }

        splashAnimation();
        initDBAddress("address.db");
    }

    private void initDBAddress(String dbName) {
        File file = new File(getFilesDir(),dbName);
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            is = getAssets().open(dbName);
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = is.read(bytes))!=-1) {
                fos.write(bytes,0,len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置splash渐变的动画效果
     */
    private void splashAnimation() {

        rl = (RelativeLayout) findViewById(R.id.rl_root);

        AlphaAnimation animation = new AlphaAnimation(0.3f,1f);
        animation.setDuration(2000);
        rl.startAnimation(animation);
    }
    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        System.out.println("进入主界面");
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + mVersionName);
        builder.setMessage(mDesc);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateApp();
                System.out.println("升级成功");
            }
        });
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("稍后升级");
                enterHome();
            }
        });

        //当用户点击取消安装按钮时，进入主界面
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    /**
     * 从服务器下载app并安装
     */
    private void updateApp() {
        tvProgress.setVisibility(View.VISIBLE);
        HttpUtils http = new HttpUtils();
        String target = Environment.getExternalStorageDirectory() + "/safe_2.0.apk";
        http.download(mDownloadUrl, target, true, true,
                new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        System.out.println("下载成功");
                        // 跳转到系统下载页面
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(Uri.fromFile(responseInfo.result),
                                "application/vnd.android.package-archive");
                        //startActivity(intent);
                        startActivityForResult(intent, 0);// 如果用户取消安装的话,
                        // 会返回结果,回调方法onActivityResult
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        tvProgress.setText("下载进度：" + current * 100 / total + "%");
                    }
                }
        );
    }

    /**
     * 在每次启动app的时候进行版本跟新检查
     */
    private void checkUpdate() {
        final long startTime = System.currentTimeMillis();
        new Thread() {

            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message msg = handler.obtainMessage();
                try {
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);

                    if (conn.getResponseCode() == 200) {
                        final InputStream inputStream = conn.getInputStream();
                        final String s = MobileUtils.readSteam(inputStream);
                        System.out.println(s);

                        //解析json
                        JSONObject jo = new JSONObject(s);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDesc = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
                        if (mVersionCode > getVersionCode()) {
                            System.out.println("需要升级");
                            msg.what = CODE_UPDATE;
                        } else {
                            System.out.println("直接进入系统");
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long during = endTime - startTime;
                    if (during < 2000) {
                        try {
                            sleep(2000 - during);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(msg.what);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }

    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getVersionName() {
        PackageManager pm = getPackageManager();

        try {
            final PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            final String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
