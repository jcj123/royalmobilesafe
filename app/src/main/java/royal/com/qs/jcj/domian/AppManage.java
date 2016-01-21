package royal.com.qs.jcj.domian;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by jcj on 16/1/14.
 */
public class AppManage implements Serializable{
    //图标
    private Drawable icon;
    private String appName;//应用名
    private Long size;//内存大小
    private boolean isUserApp;//是否是用户程序
    private boolean isSystemSize;//是否系统内存
    /**
     * 包名
     */
    private String apkPackageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }

    public boolean isSystemSize() {
        return isSystemSize;
    }

    public void setSystemSize(boolean systemSize) {
        isSystemSize = systemSize;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    @Override
    public String toString() {
        return "AppManage{" +
                "appName='" + appName + '\'' +
                ", icon=" + icon +
                ", size='" + size + '\'' +
                ", apkPackageName='" + apkPackageName + '\'' +
                '}';
    }
}
