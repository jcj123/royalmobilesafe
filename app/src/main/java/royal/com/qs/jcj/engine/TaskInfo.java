package royal.com.qs.jcj.engine;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.domian.TaskManage;

/**
 * Created by jcj on 16/1/28.
 */
public class TaskInfo {

    /**
     * 获取所有的进程的信息
     *
     * @param context
     * @return
     */
    public static List<TaskManage> getTaskInfos(Context context) {
        List<TaskManage> taskManageList = new ArrayList<TaskManage>();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : runningAppProcesses) {
            final TaskManage taskManage = new TaskManage();

            final String processName = appProcessInfo.processName;
            System.out.println("processname=====" + processName);
            //设置包名
            taskManage.setPackageName(processName);

            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{appProcessInfo.pid});
            //得到当前程序弄脏了多少内存，即占用的内存数
            final long totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
            taskManage.setMemorySize(totalPrivateDirty);

            try {
                final PackageInfo packageInfo = pm.getPackageInfo(processName, 0);

                //设置应用的图标
                final Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                taskManage.setIcon(icon);

                //设置应用的名称
                final String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                taskManage.setAppName(appName);

                //设置是否是用户程序
                final int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) !=0) {
                    taskManage.setUserTask(false);
                }else {
                    taskManage.setUserTask(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                taskManage.setAppName(processName);
                taskManage.setIcon(context.getResources().getDrawable(
                        R.mipmap.ic_launcher));
            }
            taskManageList.add(taskManage);
        }

        return taskManageList;
    }
}
