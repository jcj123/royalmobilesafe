package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import royal.com.qs.jcj.R;

public abstract class BaseSetupActivity extends Activity {
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                final float beginX = e1.getRawX();
                final float endX = e2.getRawX();
                //滑动速度不能过慢
                if(Math.abs(velocityX)<100) {
                    Toast.makeText(BaseSetupActivity.this,"亲，滑动太慢了呦",Toast.LENGTH_LONG).show();
                    return true;
                }
                //向右划 上一页
                if (endX - beginX > 200) {
                    moveToPrevious();
                    return true;
                }
                //向左划  下一页
                if (beginX - endX > 200) {
                    moveToNext();
                    return true;
                }
                return false;
            }
        });
    }

    public void next(View view) {
        moveToNext();
    }
    public void previous(View view) {
        moveToPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void moveToPrevious();

    public abstract void moveToNext() ;
}
