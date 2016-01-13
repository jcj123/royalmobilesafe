package com.qs.jcj.autolockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {
    private DevicePolicyManager mDPM;
    private ComponentName mDAS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDPM = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDAS = new ComponentName(this, DeviceAdminSample.class);
    }

    public void lock(View view) {
        if (mDPM.isAdminActive(mDAS)){
            mDPM.lockNow();
        }
    }
}
