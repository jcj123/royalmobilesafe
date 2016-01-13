package com.qs.jcj.multiclick;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private long firstClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        //如果大于0 说明不是第一次点击
        if (firstClickTime > 0) {
            if (System.currentTimeMillis() - firstClickTime < 500) {
                Toast.makeText(this, "double killer", Toast.LENGTH_LONG).show();
                firstClickTime = 0;
                return;
            }
        }

        firstClickTime = System.currentTimeMillis();
    }

    /**
     * 响应多击事件
     * @param view
     */
    long[] mHits = new long[3];

    public void multiClick(View view) {
        System.out.println("duoji");
        System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
        mHits[mHits.length-1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
            Toast.makeText(this, "double killer", Toast.LENGTH_SHORT).show();
        }
    }


}
