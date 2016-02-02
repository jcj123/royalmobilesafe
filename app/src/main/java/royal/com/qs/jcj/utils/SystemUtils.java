package royal.com.qs.jcj.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

import royal.com.qs.jcj.domian.TaskManage;

/**
 * Created by jcj on 16/1/28.
 * 系统工具类
 */
public class SystemUtils {
    /**
     * 获取正在运行的任务数
     *
     * @param context
     * @return
     */
    public static int getTaskProcessNumbers(Context context) {
        ActivityManager activityManager = (ActivityManager) context.
                getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.
                getRunningAppProcesses();

        return runningAppProcesses.size();
    }

    /**
     * 获取可用内存
     *
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        ActivityManager activityManager = (ActivityManager) context.
                getSystemService(Context.ACTIVITY_SERVICE);

        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);
        final long availMem = memoryInfo.availMem;

        return availMem;
    }

    /**
     * 获取总内存
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMem(Context context) {
        ActivityManager activityManager = (ActivityManager) context.
                getSystemService(Context.ACTIVITY_SERVICE);

        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);
        final long totalMem = memoryInfo.totalMem;

        return totalMem;
    }


}
