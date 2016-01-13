package royal.com.qs.jcj.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by jcj on 15/12/29.
 */
public class ServiceUtils {

    public static boolean isServiceRunning(Context context, String serviceName) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : rs) {
            String className = runningServiceInfo.service.getClassName();// 获取服务的名称
             System.out.println(className);
            if (className.equals(serviceName)) {// 服务存在
                return true;
            }
        }
        return false;
    }
}
