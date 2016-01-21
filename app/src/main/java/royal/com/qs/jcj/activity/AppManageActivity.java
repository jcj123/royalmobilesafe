package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.domian.AppManage;
import royal.com.qs.jcj.engine.AppInfos;

public class AppManageActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.lv_app)
    private ListView ls_app;
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    @ViewInject(R.id.tv_app)
    private TextView tv_app;

    private List<AppManage> appManages;
    private AppAdapter appAdapter;
    private List<AppManage> systemApps;
    private List<AppManage> userApps;
    private PopupWindow popupWindow;
    private AppManage clickAppManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        initData();
    }

    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_manage);
        /**
         * 利用xutils工具通过注解的方式来获取view
         */
        ViewUtils.inject(this);

        final long freeSpace_rom = Environment.getDataDirectory().getFreeSpace();
        final long freeSpace_sd = Environment.getExternalStorageDirectory().getFreeSpace();
        System.out.println("freeSpace_rom" + freeSpace_rom);
        System.out.println("freeSpace_sd" + freeSpace_sd);

        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, freeSpace_rom));
        tv_sd.setText("SD卡可用" + Formatter.formatFileSize(this, freeSpace_sd));

        ls_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popWindowDismiss();
                if (userApps != null && systemApps != null) {
                    if (firstVisibleItem > (userApps.size() + 1)) {
                        tv_app.setText("系统程序(" + systemApps.size() + ")");
                    } else {
                        tv_app.setText("用户程序(" + userApps.size() + ")");
                    }
                }
            }
        });
        /**
         * 给listview的条目设置点击事件，启动popup
         */
        ls_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private Object obj;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userApps != null && systemApps!=null) {
                    if (position < userApps.size() + 2) {
                        obj = userApps.get(position-1);
                    } else {
                        int location = 1 + userApps.size() + 1;
                        obj = systemApps.get(position-location);
                    }
                    if (obj != null && obj instanceof AppManage) {
                        clickAppManage = (AppManage) obj;
                        System.out.println(clickAppManage.getApkPackageName());
                        System.out.println(clickAppManage.getAppName());

                        View pop_view = View.inflate(AppManageActivity.this,
                                R.layout.list_items_popup, null);

                        LinearLayout ll_run = (LinearLayout) pop_view.findViewById(R.id.ll_run);
                        LinearLayout ll_uninstall = (LinearLayout) pop_view.findViewById(R.id.ll_uninstall);
                        LinearLayout ll_share = (LinearLayout) pop_view.findViewById(R.id.ll_share);
                        LinearLayout ll_detail = (LinearLayout) pop_view.findViewById(R.id.ll_detail);

                        ll_run.setOnClickListener(AppManageActivity.this);
                        ll_uninstall.setOnClickListener(AppManageActivity.this);
                        ll_share.setOnClickListener(AppManageActivity.this);
                        ll_detail.setOnClickListener(AppManageActivity.this);

                        popWindowDismiss();

                        popupWindow = new PopupWindow(pop_view, -2, -2);

                        int[] location = new int[2];
                        view.getLocationInWindow(location);

                        popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);
                    }
                }
            }

        });

    }

    private void popWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取到所有的应用信息
                appManages = AppInfos.getAppInfos(AppManageActivity.this);
                systemApps = new ArrayList<AppManage>();
                userApps = new ArrayList<AppManage>();

                for (AppManage appManage : appManages) {
                    if (appManage.isUserApp()) {
                        userApps.add(appManage);
                    } else {
                        systemApps.add(appManage);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            appAdapter = new AppAdapter(AppManageActivity.this, R.layout.list_items_app, appManages);
            ls_app.setAdapter(appAdapter);
        }
    };

    /**
     * 响应点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_run:
                Intent start_localIntent = this.getPackageManager()
                        .getLaunchIntentForPackage(clickAppManage.getApkPackageName());
                this.startActivity(start_localIntent);
                popWindowDismiss();
                break;
            case R.id.ll_uninstall:

                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE",
                        Uri.parse("package:" + clickAppManage.getApkPackageName()));
                startActivity(uninstall_localIntent);
                popWindowDismiss();
                initData();
                break;
            case R.id.ll_share:
                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppManage.getAppName()+
                                "下载地址:"+"https://play.google.com/store/apps/details?id="+
                                clickAppManage.getApkPackageName());
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                popWindowDismiss();
                break;
            case R.id.ll_detail:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppManage.getApkPackageName()));
                startActivity(detail_intent);
                break;
        }
    }

    private class AppAdapter extends ArrayAdapter<AppManage> {
        private int resourceId;

        public AppAdapter(Context context, int resource, List<AppManage> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(AppManageActivity.this);
                textView.setText("用户程序(" + userApps.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userApps.size() + 1) {
                TextView textView = new TextView(AppManageActivity.this);
                textView.setText("系统程序(" + systemApps.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }
            AppManage appManage = null;
            if (position < userApps.size() + 1) {
                appManage = userApps.get(position - 1);
            } else {
                int location = 1 + userApps.size() + 1;
                appManage = systemApps.get(position - location);
            }
            View view = View.inflate(AppManageActivity.this, resourceId, null);
            HolderView holderView = new HolderView();
            holderView.iv_app = (ImageView) view.findViewById(R.id.iv_app);
            holderView.tv_appName = (TextView) view.findViewById(R.id.tv_appName);
            holderView.tv_isSystemSize = (TextView) view.findViewById(R.id.tv_isSystemSize);
            holderView.tv_size = (TextView) view.findViewById(R.id.tv_size);
            view.setTag(holderView);
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holderView = (HolderView) view.getTag();
            }

            holderView.iv_app.setImageDrawable(appManage.getIcon());
            holderView.tv_appName.setText(appManage.getAppName());
            holderView.tv_size.setText(Formatter.formatFileSize(AppManageActivity.this, appManage.getSize()));
            if (appManage.isSystemSize()) {
                holderView.tv_isSystemSize.setText("系统内存");
            } else {
                holderView.tv_isSystemSize.setText("手机内存");
            }

            return view;
        }
    }

    class HolderView {
        ImageView iv_app;
        TextView tv_appName;
        TextView tv_isSystemSize;
        TextView tv_size;
    }

    @Override
    protected void onDestroy() {
        popWindowDismiss();
        super.onDestroy();
    }
}
