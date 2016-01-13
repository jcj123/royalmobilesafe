package com.qs.jcj.rocket;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 *烟雾效果
 */
public class BackbroundActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backbround);

        ImageView smoke_t = (ImageView) findViewById(R.id.iv_top);
        ImageView smoke_b = (ImageView) findViewById(R.id.iv_bottom);

        //设置渐变的动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        smoke_b.setAnimation(alphaAnimation);
        smoke_t.setAnimation(alphaAnimation);

        Handler handler = new Handler();
        handler.postDelayed(new Thread(){
            @Override
            public void run() {
                super.run();
                finish();
            }
        },1000);
    }
}
