package royal.com.qs.jcj.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import royal.com.qs.jcj.domian.AppManage;

/**
 * Created by jcj on 16/1/14.
 */
public class AppInfos {

    public static List<AppManage> getAppInfos(Context context) {
        List<AppManage> appManages = new ArrayList<AppManage>();

        final PackageManager packageManager = context.getPackageManager();
        final List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            final AppManage appManage = new AppManage();
            //设置应用的图标
            final Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            appManage.setIcon(icon);
            //设置应用的名字
            final String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appManage.setAppName(appName);
            //设置应用的包名
            final String packageName = packageInfo.packageName;
            appManage.setApkPackageName(packageName);
            //获取应用路径
            final String sourceDir = packageInfo.applicationInfo.sourceDir;
            final File file = new File(sourceDir);
            //设置应用的大小
            final long fileSize = file.length();
            appManage.setSize(fileSize);
            //获取安装程序的标记
            final int flags = packageInfo.applicationInfo.flags;
            //判断是否是系统应用
            if((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户应用
                appManage.setUserApp(true);
            } else {
                appManage.setUserApp(false);
            }

            //判断是否占用的时系统内存
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0) {
                //表示在sd卡
                appManage.setSystemSize(false);
            }else {
                appManage.setSystemSize(true);
            }

            appManages.add(appManage);
        }
        return appManages;
    }
}
