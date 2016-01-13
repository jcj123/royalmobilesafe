package royal.com.qs.jcj.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import royal.com.qs.jcj.R;


public class SettingView extends RelativeLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

    TextView mTitle;
    TextView mDesp;
    CheckBox mCb;
    private String title;
    private String mDescOn;
    private String mDescOff;

    public SettingView(Context context) {
        super(context);
        init();
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        title = attrs.getAttributeValue(NAMESPACE, "setting_title");// 根据属性名称,获取属性的值
        mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        init();
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.view_setting_item, this);

        mTitle = (TextView) findViewById(R.id.tv_title);
        mDesp = (TextView) findViewById(R.id.tv_desp);
        mCb = (CheckBox) findViewById(R.id.cb_check);

        mTitle.setText(title);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setmDesp(String desp) {
        mDesp.setText(desp);
    }

    public boolean isChecked() {
        return mCb.isChecked();
    }
    public void setCheck(boolean check) {
        if(check) {
            mDesp.setText(mDescOn);
        }else {
            mDesp.setText(mDescOff);
        }
        mCb.setChecked(check);
    }
}
