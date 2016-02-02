package royal.com.qs.jcj.domian;

import android.graphics.drawable.Drawable;

/**
 * Created by jcj on 16/1/28.
 */
public class TaskManage {

    private String packageName;
    private String appName;
    private Drawable icon;
    private long memorySize;
    private boolean isUserTask;//是否用户进程
    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserTask() {
        return isUserTask;
    }

    public void setUserTask(boolean userTask) {
        isUserTask = userTask;
    }
}
