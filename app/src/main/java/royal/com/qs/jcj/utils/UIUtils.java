package royal.com.qs.jcj.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by jcj on 16/1/26.
 */
public class UIUtils {

    public static void showToast(final Activity context,final String msg) {
        if (Thread.currentThread().getName().equals("main")) {
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
