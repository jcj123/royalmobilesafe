package royal.com.qs.jcj.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import royal.com.qs.jcj.domian.TaskManage;
import royal.com.qs.jcj.engine.TaskInfo;
import royal.com.qs.jcj.utils.SystemUtils;

public class AutoKillProcessService extends Service {

    private Timer timer;
    private ScreenOffBroadCast screenOffBroadCast;

    public AutoKillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        screenOffBroadCast = new ScreenOffBroadCast();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffBroadCast,filter);


        timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时清理进程");
            }
        };

        timer.schedule(timerTask,0,1000);
    }

    class ScreenOffBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            final List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : runningAppProcesses) {
                am.killBackgroundProcesses(appProcessInfo.processName);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        unregisterReceiver(screenOffBroadCast);
    }
}
