package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.domian.TaskManage;
import royal.com.qs.jcj.engine.TaskInfo;
import royal.com.qs.jcj.utils.SystemUtils;
import royal.com.qs.jcj.utils.UIUtils;

public class TaskManageActivity extends Activity {
    @ViewInject(R.id.tv_task_process_count)
    private TextView tv_task_process_count;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    @ViewInject(R.id.lv_task)
    private ListView ls_task;

    private List<TaskManage> taskInfos;
    private List<TaskManage> userTaskInfos;
    private List<TaskManage> systemTaskInfos;
    private long availMem;
    private long totalMem;
    private TaskAdapter taskAdapter;
    private int processNum;

    private SharedPreferences sp;
    private boolean is_show_system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (taskAdapter != null) {
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfo.getTaskInfos(TaskManageActivity.this);
                userTaskInfos = new ArrayList<TaskManage>();
                systemTaskInfos = new ArrayList<TaskManage>();

                for (TaskManage taskManage : taskInfos) {
                    if (taskManage.isUserTask()) {
                        userTaskInfos.add(taskManage);
                    } else {
                        systemTaskInfos.add(taskManage);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskAdapter = new TaskAdapter();
                        ls_task.setAdapter(taskAdapter);
                    }
                });
            }
        }.start();
    }



    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_manage);

        ViewUtils.inject(this);
        processNum = SystemUtils.getTaskProcessNumbers(this);
        tv_task_process_count.setText("运行中进程" + processNum + "个");

        availMem = SystemUtils.getAvailMem(this);
        totalMem = SystemUtils.getTotalMem(this);
        tv_task_memory.setText("剩余/总内存" + Formatter.formatFileSize(this, availMem) + "/" +
                Formatter.formatFileSize(this, totalMem));

        ls_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Object itemAtPosition = ls_task.getItemAtPosition(position);

                if (itemAtPosition != null && itemAtPosition instanceof TaskManage) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    TaskManage taskManage = (TaskManage) itemAtPosition;
                    if (taskManage.getPackageName().equals(getPackageName())) {
                        return;
                    }

                    if (taskManage.isCheck()) {
                        taskManage.setCheck(false);
                        holder.cb_app_status.setChecked(false);
                    } else {
                        taskManage.setCheck(true);
                        holder.cb_app_status.setChecked(true);
                    }
                }
            }
        });

    }

    class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            /**
             * 判断当前用户是否需要展示系统进程
             * 如果需要就全部展示
             * 如果不需要就展示用户进程
             */
            sp = getSharedPreferences("config", MODE_PRIVATE);
            is_show_system = sp.getBoolean("is_show_system", false);
            if (is_show_system) {
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
            } else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }

            TaskManage taskInfo;

            if (position < (userTaskInfos.size() + 1)) {
                // 用户程序
                taskInfo = userTaskInfos.get(position - 1);// 多了一个textview的标签 ，
                // 位置需要-1
            } else {
                // 系统程序
                int location = position - 1 - userTaskInfos.size() - 1;
                taskInfo = systemTaskInfos.get(location);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                // 第0个位置显示的应该是 用户程序的个数的标签。
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户程序：" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == (userTaskInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统程序：" + systemTaskInfos.size() + "个");
                return tv;
            }


            final TaskManage taskManage;
            View view = View.inflate(TaskManageActivity.this, R.layout.item_task_manager, null);
            ViewHolder holder = new ViewHolder();
            holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
            holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
            holder.tv_app_memory_size = (TextView) view.findViewById(R.id.tv_app_memory_size);
            holder.cb_app_status = (CheckBox) view.findViewById(R.id.cb_app_status);
            view.setTag(holder);

            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            if (position < (userTaskInfos.size() + 1)) {
                taskManage = userTaskInfos.get(position - 1);
            } else {
                int location = 1 + userTaskInfos.size() + 1;
                taskManage = systemTaskInfos.get(position - location);
            }

            holder.iv_app_icon.setImageDrawable(taskManage.getIcon());
            holder.tv_app_name.setText(taskManage.getAppName());
            final long memorySize = taskManage.getMemorySize();
            holder.tv_app_memory_size.setText(Formatter.formatFileSize(TaskManageActivity.this, memorySize));

            if (taskManage.isCheck()) {
                holder.cb_app_status.setChecked(true);
            } else {
                holder.cb_app_status.setChecked(false);
            }

            if (taskManage.getPackageName().equals(getPackageName())) {
                holder.cb_app_status.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    class ViewHolder {

        ImageView iv_app_icon;
        TextView tv_app_name;
        CheckBox cb_app_status;
        TextView tv_app_memory_size;
    }

    /**
     * 进程管理全选功能
     *
     * @param view
     */
    public void selectAll(View view) {
        for (TaskManage taskManage : userTaskInfos) {
            if (taskManage.getPackageName().equals(getPackageName())) {
                continue;
            }
            taskManage.setCheck(true);
        }

        for (TaskManage taskManage : systemTaskInfos) {
            taskManage.setCheck(true);
        }

        taskAdapter.notifyDataSetChanged();
    }

    /**
     * 进程管理反选功能
     *
     * @param view
     */
    public void selectOppsite(View view) {
        for (TaskManage taskManage : userTaskInfos) {
            if (taskManage.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (taskManage.isCheck()) {
                taskManage.setCheck(false);
            } else {
                taskManage.setCheck(true);
            }
        }

        for (TaskManage taskManage : systemTaskInfos) {
            if (taskManage.isCheck()) {
                taskManage.setCheck(false);
            } else {
                taskManage.setCheck(true);
            }
        }

        taskAdapter.notifyDataSetChanged();
    }

    /**
     * 杀死进程
     *
     * @param view
     */
    public void killProcess(View view) {
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<TaskManage> killTasks = new ArrayList<TaskManage>();
        int totalkills = 0;
        long totalKillMem = 0;

        for (TaskManage taskManage : userTaskInfos) {
            if (taskManage.isCheck()) {
                killTasks.add(taskManage);
            }
        }
        for (TaskManage taskManage : systemTaskInfos) {
            if (taskManage.isCheck()) {
                killTasks.add(taskManage);
            }
        }
        /**
         * 集合在迭代的时候不能改变集合的大小
         */
        for (TaskManage taskManage : killTasks) {
            if (taskManage.isUserTask()) {
                userTaskInfos.remove(taskManage);
                activityManager.killBackgroundProcesses(taskManage.getPackageName());
                totalkills++;
                totalKillMem += taskManage.getMemorySize();
            } else {
                systemTaskInfos.remove(taskManage);
                activityManager.killBackgroundProcesses(taskManage.getPackageName());
                totalkills++;
                totalKillMem += taskManage.getMemorySize();
            }
            taskAdapter.notifyDataSetChanged();
        }
        tv_task_process_count.setText("运行中进程" + (processNum - totalkills) + "个");
        UIUtils.showToast(this, "已经清理了" + totalkills + "个进程");

        tv_task_memory.setText("剩余/总内存" + Formatter.formatFileSize(this, availMem + totalKillMem) + "/" +
                Formatter.formatFileSize(this, totalMem));
        UIUtils.showToast(this, "已经清理了" + Formatter.formatFileSize(this, totalKillMem) + "内存");
    }

    /**
     * 进程管理设置功能
     *
     * @param view
     */
    public void openSetting(View view) {
        final Intent intent = new Intent(this, TaskManageSettingActivity.class);
        startActivity(intent);
    }
}
