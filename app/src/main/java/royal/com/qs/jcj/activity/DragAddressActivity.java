package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import royal.com.qs.jcj.R;

public class DragAddressActivity extends Activity {
    private TextView tv_top;
    private TextView tv_bottom;
    private ImageView iv_drag;
    private SharedPreferences mPref;

    private int winWidth;
    private int winHeight;

    private int startX;
    private int startY;

    long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_drag_address);
        mPref = getSharedPreferences("config",MODE_PRIVATE);


        tv_top = (TextView) findViewById(R.id.tv_top);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        //获取屏幕高度和宽度
        winHeight = getWindowManager().getDefaultDisplay().getHeight();
        winWidth = getWindowManager().getDefaultDisplay().getWidth();

        //设置上次保存的位置
        final int lastX = mPref.getInt("lastX", 0);
        final int lastY = mPref.getInt("lastY", 0);
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                iv_drag.getLayoutParams();
        layoutParams.leftMargin = lastX;
        layoutParams.topMargin = lastY;
        //重新设置新的布局属性
        iv_drag.setLayoutParams(layoutParams);

        //响应双击事件
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
                    int l = winWidth/2 - iv_drag.getWidth();
                    int r = winWidth/2 + iv_drag.getWidth();
                    iv_drag.layout(l,iv_drag.getTop(),r,iv_drag.getBottom());
                }
            }
        });

        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //得到起始位置坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //得到终止位置坐标
                        final int endX = (int) event.getRawX();
                        final int endY = (int) event.getRawY();
                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
                        //跟新图片位置
                        int l = iv_drag.getLeft() + dx;
                        int t = iv_drag.getTop() + dy;
                        int r = iv_drag.getRight() + dx;
                        int b = iv_drag.getBottom()+dy;

                        if(l<0||r>winWidth||t<5||b>winHeight-23) {
                            break;
                        }
                        if(b<winHeight/2) {
                            tv_top.setVisibility(View.INVISIBLE);
                            tv_bottom.setVisibility(View.VISIBLE);
                        }else {
                            tv_top.setVisibility(View.VISIBLE);
                            tv_bottom.setVisibility(View.INVISIBLE);
                        }
                        iv_drag.layout(l,t,r,b);

                        //重新设置初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        final SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("lastX",iv_drag.getLeft());
                        edit.putInt("lastY", iv_drag.getRight());
                        edit.commit();
                        break;
                }
                return true;//false使点击事件能够执行
            }
        });
    }
}
