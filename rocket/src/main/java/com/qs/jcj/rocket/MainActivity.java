package com.qs.jcj.rocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        startService(new Intent(this,RocketService.class));
        finish();
    }

    public void end(View view) {
        stopService(new Intent(this,RocketService.class));
        finish();
    }
}
